package com.hytale.survivalgames.game;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
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

    /**
     * Constructor
     */
    public GameManager(@Nonnull SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.arenas = new ConcurrentHashMap<>();
        this.playerArenas = new ConcurrentHashMap<>();
        this.gameTimers = new ConcurrentHashMap<>();

        // Initialize arenas from configuration or hardcoded for now
        initializeArenas();
    }

    /**
     * Initialize arenas
     *
     * TODO: Load from configuration file in future iteration
     */
    private void initializeArenas() {
        // Get the main world from the server
        World defaultWorld = HytaleServer.getUniverse().getWorlds().stream()
            .findFirst()
            .orElse(null);

        if (defaultWorld == null) {
            // No world available yet - server might still be loading
            // Plugin will work but no arenas until world is ready
            return;
        }

        // Create a default arena with example coordinates
        // In production, these would be loaded from a config file
        String arenaName = "default";
        String displayName = "Default Arena";

        // Arena boundaries (100x100x100 area centered at origin)
        Vector3d corner1 = new Vector3d(-50, 0, -50);
        Vector3d corner2 = new Vector3d(50, 100, 50);

        // Lobby spawn at the center, elevated
        Vector3d lobbySpawn = new Vector3d(0, 65, 0);

        // Create the arena
        Arena arena = new Arena(
            arenaName,
            displayName,
            defaultWorld,
            corner1,
            corner2,
            lobbySpawn,
            plugin.getConfig().getMinPlayers(),
            plugin.getConfig().getMaxPlayers(),
            plugin.getConfig().getGameTime(),
            plugin.getConfig().getDeathmatchTime(),
            plugin.getConfig().getBorderRadius()
        );

        // Add some spawn points around the arena
        arena.addSpawnPoint(new Vector3d(20, 64, 20));
        arena.addSpawnPoint(new Vector3d(-20, 64, 20));
        arena.addSpawnPoint(new Vector3d(20, 64, -20));
        arena.addSpawnPoint(new Vector3d(-20, 64, -20));
        arena.addSpawnPoint(new Vector3d(30, 64, 0));
        arena.addSpawnPoint(new Vector3d(-30, 64, 0));
        arena.addSpawnPoint(new Vector3d(0, 64, 30));
        arena.addSpawnPoint(new Vector3d(0, 64, -30));

        // Add some chest locations
        arena.addChestLocation(new Vector3d(15, 64, 15));
        arena.addChestLocation(new Vector3d(-15, 64, 15));
        arena.addChestLocation(new Vector3d(15, 64, -15));
        arena.addChestLocation(new Vector3d(-15, 64, -15));
        arena.addChestLocation(new Vector3d(0, 64, 0));

        // Register the arena
        arenas.put(arenaName, arena);
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
            Message.raw("§a" + player.getDisplayName() + " joined! (" +
                arena.getPlayerCount() + "/" + arena.getMaxPlayers() + ")"));

        // TODO: Teleport player to lobby spawn
        // This would require Transform/position manipulation API

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
        UUID playerId = player.getUuid();
        Arena arena = playerArenas.remove(playerId);

        if (arena == null) {
            return;
        }

        arena.removePlayer(playerId);

        broadcastToArena(arena,
            Message.raw("§c" + player.getDisplayName() + " left! (" +
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

        broadcastToArena(arena,
            Message.raw("§eGame starting in " + plugin.getConfig().getCountdownTime() + " seconds..."));

        // TODO: Implement actual countdown timer using Hytale's scheduler
        // For now, we'll immediately start the game as a placeholder
        startGame(arena);
    }

    /**
     * Start the actual game
     */
    private void startGame(@Nonnull Arena arena) {
        arena.setGameState(GameState.IN_GAME);

        broadcastToArena(arena, Message.raw("§a§l======================"));
        broadcastToArena(arena, Message.raw("§e§lSURVIVAL GAMES"));
        broadcastToArena(arena, Message.raw("§a§l======================"));
        broadcastToArena(arena, Message.raw("§7Last player standing wins!"));

        // Initialize game timer
        gameTimers.put(arena, arena.getGameTime());

        // TODO: Teleport players to spawn points
        // TODO: Spawn loot in chests
        // TODO: Start game timer

    }

    /**
     * Handle player elimination
     */
    public void handlePlayerElimination(@Nonnull UUID playerId, @Nonnull Arena arena) {
        arena.addSpectator(playerId);

        broadcastToArena(arena,
            Message.raw("§cA player §7was eliminated! §e" +
                arena.getPlayerCount() + " §7remaining."));

        // Check win condition
        if (arena.getPlayerCount() == 1) {
            UUID winnerId = arena.getPlayers().get(0);
            endGame(arena, null); // TODO: Get winner Player object
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
            broadcastToArena(arena, Message.raw("§a§l======================"));
            broadcastToArena(arena, Message.raw("§6§l" + winner.getDisplayName() + " WINS!"));
            broadcastToArena(arena, Message.raw("§a§l======================"));

            if (plugin.getConfig().isBroadcastEnd()) {
                // TODO: Broadcast to entire server
            }
        } else {
            broadcastToArena(arena, Message.raw("§cGame ended!"));
        }

        // Clean up timers
        gameTimers.remove(arena);

        // TODO: Schedule arena reset after delay
        resetArena(arena);
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
            // TODO: Teleport player back to spawn
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

        broadcastToArena(arena, Message.raw("§cGame stopped by administrator!"));
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
     * Broadcast a message to all players in an arena
     */
    private void broadcastToArena(@Nonnull Arena arena, @Nonnull Message message) {
        // Get all players (alive + spectators) in the arena
        List<UUID> allPlayers = new ArrayList<>();
        allPlayers.addAll(arena.getPlayers());
        allPlayers.addAll(arena.getSpectators());

        // Send message to each player
        for (UUID playerId : allPlayers) {
            // Get player from server's entity registry
            HytaleServer.getUniverse().getWorlds().forEach(world -> {
                world.getEntities().stream()
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> (Player) entity)
                    .filter(player -> player.getUuid().equals(playerId))
                    .findFirst()
                    .ifPresent(player -> player.sendMessage(message));
            });
        }
    }
}
