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

        // For now, we'll create a single default arena
        // In a full implementation, this would load from a config file

        // Note: World loading would need to be done through HytaleServer API
        // For this example, we're showing the structure

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
            Message.raw("" + player.getDisplayName() + " joined! (" +
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
            Message.raw("" + player.getDisplayName() + " left! (" +
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
            Message.raw("Game starting in " + plugin.getConfig().getCountdownTime() + " seconds..."));

        // TODO: Implement actual countdown timer using Hytale's scheduler
        // For now, we'll immediately start the game as a placeholder
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
        teleportPlayersToSpawns(arena);

        // Spawn loot in chests
        spawnChestLoot(arena);

    }

    /**
     * Teleport all players to random spawn points
     */
    private void teleportPlayersToSpawns(@Nonnull Arena arena) {
        List<Vector3d> spawnPoints = arena.getSpawnPoints();
        List<UUID> players = arena.getPlayers();

        if (spawnPoints.isEmpty()) {
            return;
        }

        // Shuffle spawn points for random assignment
        List<Vector3d> shuffledSpawns = new ArrayList<>(spawnPoints);
        Collections.shuffle(shuffledSpawns);

        // TODO: Implement player teleportation when Hytale's Transform/Position API is available
        // Current API limitations:
        // - HytaleServer.getServer() method doesn't exist
        // - Player.setPosition() method doesn't exist
        // - Need to use Transform component system when documented
        //
        // Planned implementation:
        // 1. Get each player entity by UUID
        // 2. Access their Transform component
        // 3. Set position to assigned spawn point
        // 4. Send "Good luck!" message to each player

        // For now, spawn points are assigned but teleportation is pending API availability
        for (int i = 0; i < players.size(); i++) {
            UUID playerId = players.get(i);
            Vector3d spawnPoint = shuffledSpawns.get(i % shuffledSpawns.size());
            // Teleportation will happen here when API is available
        }
    }

    /**
     * Spawn loot in all chest locations
     */
    private void spawnChestLoot(@Nonnull Arena arena) {
        plugin.getLootManager().populateArenaChests(arena);
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
            broadcastToArena(arena, Message.raw("======================"));
            broadcastToArena(arena, Message.raw("" + winner.getDisplayName() + " WINS!"));
            broadcastToArena(arena, Message.raw("======================"));

            if (plugin.getConfig().isBroadcastEnd()) {
                // TODO: Broadcast to entire server
            }
        } else {
            broadcastToArena(arena, Message.raw("Game ended!"));
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
     * Broadcast a message to all players in an arena
     * TODO: Implement proper player lookup once API method is known
     */
    private void broadcastToArena(@Nonnull Arena arena, @Nonnull Message message) {
        // TODO: Get Player objects from UUIDs and send messages
        // For now, messages will only be logged
    }
}
