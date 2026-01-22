package com.hytale.survivalgames.game;

import com.hytale.api.Location;
import com.hytale.api.Player;
import com.hytale.api.scheduler.Task;
import com.hytale.api.sound.Sound;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.player.SGPlayer;

import java.util.*;

/**
 * Manages all active Survival Games matches
 * Handles game state transitions, timers, and win conditions
 */
public class GameManager {

    private final SurvivalGamesPlugin plugin;
    private final Map<String, Arena> arenas;
    private final Map<UUID, Arena> playerArenas; // Track which arena each player is in
    private final Map<Arena, Task> gameTasks; // Active game timer tasks
    private final Map<Arena, Integer> gameTimers; // Current time remaining for each game

    public GameManager(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.playerArenas = new HashMap<>();
        this.gameTasks = new HashMap<>();
        this.gameTimers = new HashMap<>();

        // Load arenas from config
        loadArenas();
    }

    /**
     * Load all arenas from configuration
     */
    private void loadArenas() {
        arenas.clear();
        List<Arena> configArenas = plugin.getConfigManager().getArenas();
        for (Arena arena : configArenas) {
            arenas.put(arena.getName(), arena);
        }
        plugin.getLogger().info("Loaded " + arenas.size() + " arenas");
    }

    /**
     * Get an arena by name
     */
    public Arena getArena(String name) {
        return arenas.get(name);
    }

    /**
     * Get all available arenas
     */
    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    /**
     * Get the arena a player is currently in
     */
    public Arena getPlayerArena(UUID playerId) {
        return playerArenas.get(playerId);
    }

    /**
     * Add a player to an arena
     */
    public boolean joinArena(Player player, Arena arena) {
        if (playerArenas.containsKey(player.getUniqueId())) {
            return false; // Already in an arena
        }

        if (!arena.addPlayer(player.getUniqueId())) {
            return false; // Arena full or not accepting players
        }

        playerArenas.put(player.getUniqueId(), arena);

        // Teleport to lobby
        player.teleport(arena.getLobbySpawn());

        // Clear inventory and apply lobby effects
        player.getInventory().clear();
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);

        // Broadcast join message
        broadcastToArena(arena, "§a" + player.getName() + " joined the game! (" +
                arena.getPlayerCount() + "/" + arena.getMaxPlayers() + ")");

        // Check if we can start countdown
        if (arena.getPlayerCount() >= arena.getMinPlayers() && arena.getGameState() == GameState.WAITING) {
            startCountdown(arena);
        }

        return true;
    }

    /**
     * Remove a player from their current arena
     */
    public void leaveArena(Player player) {
        Arena arena = playerArenas.remove(player.getUniqueId());
        if (arena == null) {
            return;
        }

        arena.removePlayer(player.getUniqueId());

        // Return to spawn
        player.teleport(plugin.getServer().getSpawnLocation());
        player.getInventory().clear();
        player.setHealth(player.getMaxHealth());

        // Broadcast leave message
        broadcastToArena(arena, "§c" + player.getName() + " left the game! (" +
                arena.getPlayerCount() + "/" + arena.getMaxPlayers() + ")");

        // Check if game should end
        if (arena.getGameState().isRunning() && arena.getPlayerCount() < 2) {
            endGame(arena);
        }
    }

    /**
     * Start the countdown before a game begins
     */
    private void startCountdown(Arena arena) {
        arena.setGameState(GameState.STARTING);

        final int[] countdown = {10}; // 10 second countdown

        Task task = plugin.getScheduler().scheduleRepeating(() -> {
            if (arena.getPlayerCount() < arena.getMinPlayers()) {
                // Not enough players, cancel countdown
                arena.setGameState(GameState.WAITING);
                broadcastToArena(arena, "§cCountdown cancelled! Not enough players.");
                return;
            }

            if (countdown[0] == 0) {
                startGame(arena);
                return;
            }

            // Play sound and show countdown message
            if (countdown[0] <= 5 || countdown[0] % 5 == 0) {
                broadcastToArena(arena, "§eGame starting in §6" + countdown[0] + " §eseconds...");
                playSound(arena, Sound.CLICK);
            }

            countdown[0]--;
        }, 0, 20); // 20 ticks = 1 second

        gameTasks.put(arena, task);
    }

    /**
     * Start the actual game
     */
    private void startGame(Arena arena) {
        arena.setGameState(GameState.IN_GAME);

        broadcastToArena(arena, "§a§l======================");
        broadcastToArena(arena, "§e§lSURVIVAL GAMES");
        broadcastToArena(arena, "§a§l======================");
        broadcastToArena(arena, "§7Last player standing wins!");

        // Teleport players to spawn points
        List<Location> spawns = new ArrayList<>(arena.getSpawnPoints());
        Collections.shuffle(spawns);

        int spawnIndex = 0;
        for (UUID playerId : arena.getPlayers()) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null) {
                // Teleport to spawn
                Location spawn = spawns.get(spawnIndex % spawns.size());
                player.teleport(spawn);

                // Prepare player
                player.getInventory().clear();
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);

                // Create SGPlayer wrapper
                plugin.getPlayerManager().createSGPlayer(player);

                spawnIndex++;
            }
        }

        // Spawn loot in chests
        plugin.getLootManager().populateChests(arena);

        // Start game timer
        startGameTimer(arena);

        playSound(arena, Sound.LEVEL_UP);
    }

    /**
     * Start the game timer
     */
    private void startGameTimer(Arena arena) {
        gameTimers.put(arena, arena.getGameTime());

        Task task = plugin.getScheduler().scheduleRepeating(() -> {
            int timeLeft = gameTimers.get(arena);
            timeLeft--;
            gameTimers.put(arena, timeLeft);

            // Deathmatch check
            if (timeLeft == arena.getDeathmatchTime() && arena.getGameState() == GameState.IN_GAME) {
                startDeathmatch(arena);
            }

            // Time warnings
            if (timeLeft == 60 || timeLeft == 30 || timeLeft == 10 || timeLeft == 5) {
                broadcastToArena(arena, "§e" + timeLeft + " seconds remaining!");
            }

            // Time's up
            if (timeLeft <= 0) {
                // End game - highest kills wins
                endGameByTime(arena);
            }
        }, 20, 20); // Every second

        gameTasks.put(arena, task);
    }

    /**
     * Start deathmatch phase (shrinking border)
     */
    private void startDeathmatch(Arena arena) {
        arena.setGameState(GameState.DEATHMATCH);

        broadcastToArena(arena, "§c§l======================");
        broadcastToArena(arena, "§4§lDEATHMATCH!");
        broadcastToArena(arena, "§c§l======================");
        broadcastToArena(arena, "§7The border is shrinking!");

        playSound(arena, Sound.WITHER_SPAWN);

        // Schedule border damage
        plugin.getScheduler().scheduleRepeating(() -> {
            Location center = arena.getCenterPoint();
            int radius = arena.getBorderShrinkRadius();

            for (UUID playerId : arena.getPlayers()) {
                Player player = plugin.getServer().getPlayer(playerId);
                if (player != null) {
                    Location playerLoc = player.getLocation();
                    double distance = playerLoc.distance(center);

                    if (distance > radius) {
                        // Player is outside border
                        player.damage(2.0);
                        player.sendMessage("§cYou are outside the border! Get to the center!");
                    }
                }
            }
        }, 20, 20); // Check every second
    }

    /**
     * Handle player elimination
     */
    public void handlePlayerDeath(Player player, Arena arena) {
        if (arena.getGameState() != GameState.IN_GAME && arena.getGameState() != GameState.DEATHMATCH) {
            return;
        }

        arena.addSpectator(player.getUniqueId());

        // Broadcast elimination
        broadcastToArena(arena, "§c" + player.getName() + " §7has been eliminated! §e" +
                arena.getPlayerCount() + " §7players remaining.");

        // Make player spectator
        player.setGameMode(com.hytale.api.GameMode.SPECTATOR);

        // Check win condition
        if (arena.getPlayerCount() == 1) {
            // We have a winner!
            UUID winnerId = arena.getPlayers().get(0);
            Player winner = plugin.getServer().getPlayer(winnerId);
            if (winner != null) {
                endGameWithWinner(arena, winner);
            }
        } else if (arena.getPlayerCount() == 0) {
            // Everyone died somehow
            endGame(arena);
        }
    }

    /**
     * End game with a winner
     */
    private void endGameWithWinner(Arena arena, Player winner) {
        arena.setGameState(GameState.ENDING);

        // Cancel game tasks
        cancelGameTasks(arena);

        // Broadcast winner
        broadcastToArena(arena, "§a§l======================");
        broadcastToArena(arena, "§6§l" + winner.getName() + " WINS!");
        broadcastToArena(arena, "§a§l======================");

        playSound(arena, Sound.LEVEL_UP);

        // Schedule arena reset
        plugin.getScheduler().scheduleDelayed(() -> {
            resetArena(arena);
        }, 200); // 10 seconds
    }

    /**
     * End game by time (most kills wins)
     */
    private void endGameByTime(Arena arena) {
        arena.setGameState(GameState.ENDING);
        cancelGameTasks(arena);

        // Find player with most kills
        Player topPlayer = null;
        int topKills = 0;

        for (UUID playerId : arena.getPlayers()) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null) {
                SGPlayer sgPlayer = plugin.getPlayerManager().getSGPlayer(playerId);
                if (sgPlayer != null && sgPlayer.getKills() > topKills) {
                    topKills = sgPlayer.getKills();
                    topPlayer = player;
                }
            }
        }

        if (topPlayer != null) {
            broadcastToArena(arena, "§6§l" + topPlayer.getName() + " wins with " + topKills + " kills!");
        } else {
            broadcastToArena(arena, "§cGame ended - No winner!");
        }

        // Schedule reset
        plugin.getScheduler().scheduleDelayed(() -> {
            resetArena(arena);
        }, 200);
    }

    /**
     * End game without winner
     */
    private void endGame(Arena arena) {
        arena.setGameState(GameState.ENDING);
        cancelGameTasks(arena);

        broadcastToArena(arena, "§cGame ended!");

        plugin.getScheduler().scheduleDelayed(() -> {
            resetArena(arena);
        }, 100);
    }

    /**
     * Reset arena after game ends
     */
    private void resetArena(Arena arena) {
        // Teleport all players back to spawn
        List<UUID> allPlayers = new ArrayList<>();
        allPlayers.addAll(arena.getPlayers());
        allPlayers.addAll(arena.getSpectators());

        for (UUID playerId : allPlayers) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null) {
                player.teleport(plugin.getServer().getSpawnLocation());
                player.setGameMode(com.hytale.api.GameMode.SURVIVAL);
                player.getInventory().clear();
                player.setHealth(player.getMaxHealth());
            }
            playerArenas.remove(playerId);
            plugin.getPlayerManager().removeSGPlayer(playerId);
        }

        // Reset arena
        arena.reset();
        gameTimers.remove(arena);
    }

    /**
     * Force stop a game
     */
    public void stopGame(Arena arena) {
        if (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.DISABLED) {
            return;
        }

        broadcastToArena(arena, "§cGame force stopped by an administrator!");
        endGame(arena);
    }

    /**
     * Stop all active games
     */
    public void stopAllGames() {
        for (Arena arena : arenas.values()) {
            if (arena.getGameState().isRunning()) {
                stopGame(arena);
            }
        }
    }

    /**
     * Cancel all tasks for an arena
     */
    private void cancelGameTasks(Arena arena) {
        Task task = gameTasks.remove(arena);
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * Broadcast a message to all players in an arena
     */
    private void broadcastToArena(Arena arena, String message) {
        for (UUID playerId : arena.getPlayers()) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null) {
                player.sendMessage(message);
            }
        }
        for (UUID playerId : arena.getSpectators()) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Play a sound to all players in an arena
     */
    private void playSound(Arena arena, Sound sound) {
        for (UUID playerId : arena.getPlayers()) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null) {
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            }
        }
    }
}
