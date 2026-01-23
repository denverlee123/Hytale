package com.hytale.survivalgames.game;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all Survival Games matches
 *
 * Handles game state transitions, player management, and win conditions.
 * Adapted for Hytale's ECS architecture.
 */
public class GameManager {

    private final SurvivalGamesPlugin plugin;

    // Arena storage
    private final Map<String, Arena> arenas;

    // Player to arena mapping
    private final Map<UUID, Arena> playerArenas;

    // Game timers
    private final Map<Arena, Integer> gameTimers;

    // Countdown timers
    private final Map<Arena, Integer> countdownTimers;

    /**
     * Constructor
     */
    public GameManager(@Nonnull SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.arenas = new ConcurrentHashMap<>();
        this.playerArenas = new ConcurrentHashMap<>();
        this.gameTimers = new ConcurrentHashMap<>();
        this.countdownTimers = new ConcurrentHashMap<>();

        // Initialize arenas from configuration or hardcoded for now
        initializeArenas();

        // Start game tick timer
        startGameTick();
    }

    /**
     * Initialize arenas
     *
     * TODO: Load from configuration file in future iteration
     *
     * Note: This is a placeholder. Arenas should be created through commands
     * once worlds are loaded, as we don't have access to get worlds at plugin init.
     */
    private void initializeArenas() {
        // Arena initialization is deferred until worlds are available
        // Arenas can be created via commands once the server is fully loaded

        // Server admins should use commands to create arenas:
        // Example: /sgcreate <name> <world> <coords...>

        System.out.println("Arena initialization deferred - create arenas via commands");
    }

    /**
     * Start the game tick timer to handle countdowns and game logic
     */
    private void startGameTick() {
        // Schedule a repeating task every second (20 ticks)
        plugin.getServer().getScheduler().scheduleRepeating(() -> {
            tick();
        }, 1, 20);
    }

    /**
     * Game tick - runs every second
     */
    private void tick() {
        // Handle countdowns
        for (Map.Entry<Arena, Integer> entry : new HashMap<>(countdownTimers).entrySet()) {
            Arena arena = entry.getKey();
            int timeLeft = entry.getValue() - 1;

            if (timeLeft <= 0) {
                countdownTimers.remove(arena);
                startGame(arena);
            } else {
                countdownTimers.put(arena, timeLeft);
                if (timeLeft <= 5 || timeLeft % 10 == 0) {
                    broadcastToArena(arena, Message.raw("Game starting in " + timeLeft + " seconds..."));
                }
            }
        }

        // Handle game timers
        for (Map.Entry<Arena, Integer> entry : new HashMap<>(gameTimers).entrySet()) {
            Arena arena = entry.getKey();
            int timeLeft = entry.getValue() - 1;

            if (timeLeft <= 0) {
                // Start deathmatch
                if (arena.getGameState() == GameState.IN_GAME) {
                    arena.setGameState(GameState.DEATHMATCH);
                    broadcastToArena(arena, Message.raw("DEATHMATCH! The border is shrinking!"));
                    gameTimers.put(arena, arena.getDeathmatchTime());
                } else {
                    // End game if nobody won
                    endGame(arena, null);
                }
            } else {
                gameTimers.put(arena, timeLeft);
            }
        }
    }

    /**
     * Create and register a new arena
     */
    public void createArena(@Nonnull String name, @Nonnull String displayName,
                           @Nonnull World world, @Nonnull Vector3d corner1,
                           @Nonnull Vector3d corner2, @Nonnull Vector3d lobbySpawn) {
        Arena arena = new Arena(
            name,
            displayName,
            world,
            corner1,
            corner2,
            lobbySpawn,
            plugin.getConfig().getMinPlayers(),
            plugin.getConfig().getMaxPlayers(),
            plugin.getConfig().getGameTime(),
            plugin.getConfig().getDeathmatchTime(),
            plugin.getConfig().getBorderRadius()
        );

        arenas.put(name, arena);
    }

    /**
     * Get arena by name
     */
    @Nullable
    public Arena getArena(@Nonnull String name) {
        return arenas.get(name);
    }

    /**
     * Get all arenas
     */
    @Nonnull
    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    /**
     * Get arena count
     */
    public int getArenaCount() {
        return arenas.size();
    }

    /**
     * Get the arena a player is currently in
     */
    @Nullable
    public Arena getPlayerArena(@Nonnull UUID playerId) {
        return playerArenas.get(playerId);
    }

    /**
     * Add a player to an arena
     */
    public boolean joinArena(@Nonnull Player player, @Nonnull Arena arena) {
        @SuppressWarnings("removal")
        UUID playerId = player.getUuid();

        // Check if already in an arena
        if (playerArenas.containsKey(playerId)) {
            return false;
        }

        // Try to add to arena
        if (!arena.addPlayer(playerId)) {
            return false;
        }

        // Track player-arena mapping
        playerArenas.put(playerId, arena);

        // Send join message
        broadcastToArena(arena,
            Message.raw(player.getDisplayName() + " joined! (" +
                arena.getPlayerCount() + "/" + arena.getMaxPlayers() + ")"));

        // Teleport player to lobby spawn
        teleportPlayer(playerId, arena.getLobbySpawn());

        // Check if we can start countdown
        if (arena.getPlayerCount() >= arena.getMinPlayers() &&
            arena.getGameState() == GameState.WAITING) {
            startCountdown(arena);
        }

        return true;
    }

    /**
     * Remove a player from their current arena
     */
    public void leaveArena(@Nonnull Player player) {
        @SuppressWarnings("removal")
        UUID playerId = player.getUuid();
        Arena arena = playerArenas.remove(playerId);

        if (arena == null) {
            return;
        }

        arena.removePlayer(playerId);

        broadcastToArena(arena,
            Message.raw(player.getDisplayName() + " left! (" +
                arena.getPlayerCount() + "/" + arena.getMaxPlayers() + ")"));

        // Check if game should end
        if (arena.getGameState().isRunning() && arena.getPlayerCount() < 2) {
            endGame(arena, null);
        }
    }

    /**
     * Start countdown before game begins
     */
    private void startCountdown(@Nonnull Arena arena) {
        arena.setGameState(GameState.STARTING);

        int countdownTime = plugin.getConfig().getCountdownTime();
        countdownTimers.put(arena, countdownTime);

        broadcastToArena(arena,
            Message.raw("Game starting in " + countdownTime + " seconds..."));
    }

    /**
     * Force start a game immediately without countdown
     * Used by admin commands to manually start games
     */
    public void forceStart(@Nonnull Arena arena) {
        if (arena.getGameState().isRunning()) {
            return; // Already running
        }

        System.out.println("Force starting game in arena: " + arena.getName());
        countdownTimers.remove(arena); // Cancel any countdown
        startGame(arena);
    }

    /**
     * Start the actual game
     */
    private void startGame(@Nonnull Arena arena) {
        arena.setGameState(GameState.IN_GAME);

        broadcastToArena(arena, Message.raw("======================"));
        broadcastToArena(arena, Message.raw("SURVIVAL GAMES"));
        broadcastToArena(arena, Message.raw("======================"));
        broadcastToArena(arena, Message.raw("Last player standing wins!"));

        // Initialize game timer
        gameTimers.put(arena, arena.getGameTime());

        // Teleport players to spawn points
        teleportPlayersToSpawnPoints(arena);

        // Spawn loot in chests
        spawnChestLoot(arena);
    }

    /**
     * Teleport all players to random spawn points
     */
    private void teleportPlayersToSpawnPoints(@Nonnull Arena arena) {
        List<Vector3d> spawnPoints = arena.getSpawnPoints();

        if (spawnPoints.isEmpty()) {
            broadcastToArena(arena, Message.raw("ERROR: No spawn points configured!"));
            return;
        }

        // Shuffle spawn points for random distribution
        List<Vector3d> shuffledSpawns = new ArrayList<>(spawnPoints);
        Collections.shuffle(shuffledSpawns);

        List<UUID> players = arena.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            UUID playerId = players.get(i);
            Vector3d spawnPoint = shuffledSpawns.get(i % shuffledSpawns.size());
            teleportPlayer(playerId, spawnPoint);
        }

        broadcastToArena(arena, Message.raw("Players teleported to spawn points!"));
    }

    /**
     * Spawn loot in all configured chest locations
     */
    private void spawnChestLoot(@Nonnull Arena arena) {
        List<Vector3d> chestLocations = arena.getChestLocations();

        if (chestLocations.isEmpty()) {
            broadcastToArena(arena, Message.raw("WARNING: No chest locations configured!"));
            return;
        }

        // TODO: Implement actual chest spawning with loot
        // This would require:
        // 1. Accessing the world's block system
        // 2. Placing chest blocks at the locations
        // 3. Filling chests with random loot from loot tables

        broadcastToArena(arena, Message.raw(chestLocations.size() + " chest locations ready!"));
        System.out.println("Chest loot spawning not yet implemented - need block/inventory API");
    }

    /**
     * Teleport a player to a location
     */
    private void teleportPlayer(@Nonnull UUID playerId, @Nonnull Vector3d location) {
        try {
            PlayerRef playerRef = Universe.get().getPlayer(playerId);
            if (playerRef == null) {
                return;
            }

            // Set the player's transform position
            Transform transform = playerRef.getTransform();
            transform.getPosition().assign(location);

        } catch (Exception e) {
            System.err.println("Failed to teleport player " + playerId + ": " + e.getMessage());
        }
    }

    /**
     * Handle player elimination
     */
    public void handlePlayerElimination(@Nonnull UUID playerId, @Nonnull Arena arena) {
        arena.addSpectator(playerId);

        broadcastToArena(arena,
            Message.raw("A player was eliminated! " +
                arena.getPlayerCount() + " remaining."));

        // Check win condition
        if (arena.getPlayerCount() == 1) {
            UUID winnerId = arena.getPlayers().get(0);
            Player winner = getPlayerById(winnerId);
            endGame(arena, winner);
        } else if (arena.getPlayerCount() == 0) {
            endGame(arena, null);
        }
    }

    /**
     * End the game
     */
    private void endGame(@Nonnull Arena arena, @Nullable Player winner) {
        arena.setGameState(GameState.ENDING);

        if (winner != null) {
            broadcastToArena(arena, Message.raw("======================"));
            broadcastToArena(arena, Message.raw(winner.getDisplayName() + " WINS!"));
            broadcastToArena(arena, Message.raw("======================"));

            if (plugin.getConfig().isBroadcastEnd()) {
                HytaleServer.get().broadcast(Message.raw(
                    winner.getDisplayName() + " won Survival Games!"));
            }
        } else {
            broadcastToArena(arena, Message.raw("Game ended!"));
        }

        // Clean up timers
        gameTimers.remove(arena);
        countdownTimers.remove(arena);

        // Reset arena after 5 seconds
        plugin.getServer().getScheduler().schedule(() -> {
            resetArena(arena);
        }, 100); // 5 seconds = 100 ticks
    }

    /**
     * Reset arena after game ends
     */
    private void resetArena(@Nonnull Arena arena) {
        // Remove all players from tracking
        List<UUID> allPlayers = new ArrayList<>();
        allPlayers.addAll(arena.getPlayers());
        allPlayers.addAll(arena.getSpectators());

        for (UUID playerId : allPlayers) {
            playerArenas.remove(playerId);
            // Teleport player back to lobby spawn
            teleportPlayer(playerId, arena.getLobbySpawn());
            // TODO: Reset player inventory/health
        }

        // Reset arena state
        arena.reset();

    }

    /**
     * Stop a specific game
     */
    public void stopGame(@Nonnull Arena arena) {
        if (!arena.getGameState().isRunning()) {
            return;
        }

        broadcastToArena(arena, Message.raw("Game stopped by administrator!"));
        endGame(arena, null);
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
     * Reload game manager
     */
    public void reload() {
        stopAllGames();
        // Re-initialize if needed
    }

    /**
     * Get a player by their UUID
     */
    @Nullable
    private Player getPlayerById(@Nonnull UUID playerId) {
        PlayerRef playerRef = Universe.get().getPlayer(playerId);
        if (playerRef == null) {
            return null;
        }

        // Get the actual Player object from the server
        return HytaleServer.get().getPlayerByUuid(playerId);
    }

    /**
     * Broadcast a message to all players in an arena
     */
    private void broadcastToArena(@Nonnull Arena arena, @Nonnull Message message) {
        List<UUID> allPlayers = new ArrayList<>();
        allPlayers.addAll(arena.getPlayers());
        allPlayers.addAll(arena.getSpectators());

        for (UUID playerId : allPlayers) {
            Player player = getPlayerById(playerId);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }
}
