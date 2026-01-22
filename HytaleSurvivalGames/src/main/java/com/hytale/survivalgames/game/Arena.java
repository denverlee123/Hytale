package com.hytale.survivalgames.game;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Survival Games arena
 *
 * Contains arena boundaries, spawn points, and manages active players.
 * Note: This is simplified as Hytale uses an ECS architecture, so we store
 * player UUIDs and look them up when needed rather than caching Player objects.
 */
public class Arena {

    private final String name;
    private final String displayName;
    private final World world;

    // Arena boundaries (cuboid region)
    private final Vector3d corner1;
    private final Vector3d corner2;

    // Lobby spawn point
    private final Vector3d lobbySpawn;

    // Player spawn points (where players start in-game)
    private final List<Vector3d> spawnPoints;

    // Chest locations for loot
    private final List<Vector3d> chestLocations;

    // Game settings
    private final int minPlayers;
    private final int maxPlayers;
    private final int gameTime;
    private final int deathmatchTime;
    private final int borderRadius;

    // Current game state
    private GameState gameState;

    // Active players (using UUIDs due to ECS architecture)
    private final List<UUID> activePlayers;
    private final List<UUID> spectators;

    /**
     * Constructor for Arena
     */
    public Arena(@Nonnull String name, @Nonnull String displayName, @Nonnull World world,
                 @Nonnull Vector3d corner1, @Nonnull Vector3d corner2, @Nonnull Vector3d lobbySpawn,
                 int minPlayers, int maxPlayers, int gameTime, int deathmatchTime, int borderRadius) {
        this.name = name;
        this.displayName = displayName;
        this.world = world;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.lobbySpawn = lobbySpawn;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.gameTime = gameTime;
        this.deathmatchTime = deathmatchTime;
        this.borderRadius = borderRadius;

        this.spawnPoints = new ArrayList<>();
        this.chestLocations = new ArrayList<>();
        this.activePlayers = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.gameState = GameState.WAITING;
    }

    // ========== Spawn Point Management ==========

    /**
     * Add a spawn point to the arena
     */
    public void addSpawnPoint(@Nonnull Vector3d position) {
        spawnPoints.add(position);
    }

    /**
     * Get all spawn points
     */
    @Nonnull
    public List<Vector3d> getSpawnPoints() {
        return new ArrayList<>(spawnPoints);
    }

    /**
     * Get a random spawn point for player spawning
     */
    @Nonnull
    public Vector3d getRandomSpawnPoint() {
        if (spawnPoints.isEmpty()) {
            return lobbySpawn;
        }
        int index = (int) (Math.random() * spawnPoints.size());
        return spawnPoints.get(index);
    }

    // ========== Chest Location Management ==========

    /**
     * Add a chest location for loot spawning
     */
    public void addChestLocation(@Nonnull Vector3d position) {
        chestLocations.add(position);
    }

    /**
     * Get all chest locations
     */
    @Nonnull
    public List<Vector3d> getChestLocations() {
        return new ArrayList<>(chestLocations);
    }

    // ========== Player Management ==========

    /**
     * Add a player to the arena
     *
     * @param playerId Player UUID
     * @return true if player was added, false if arena is full or not accepting players
     */
    public boolean addPlayer(@Nonnull UUID playerId) {
        if (activePlayers.size() >= maxPlayers) {
            return false;
        }
        if (!gameState.canJoin()) {
            return false;
        }
        activePlayers.add(playerId);
        return true;
    }

    /**
     * Remove a player from the arena
     */
    public void removePlayer(@Nonnull UUID playerId) {
        activePlayers.remove(playerId);
        spectators.remove(playerId);
    }

    /**
     * Convert a player to spectator (when eliminated)
     */
    public void addSpectator(@Nonnull UUID playerId) {
        activePlayers.remove(playerId);
        if (!spectators.contains(playerId)) {
            spectators.add(playerId);
        }
    }

    /**
     * Get all active players
     */
    @Nonnull
    public List<UUID> getPlayers() {
        return new ArrayList<>(activePlayers);
    }

    /**
     * Get all spectators
     */
    @Nonnull
    public List<UUID> getSpectators() {
        return new ArrayList<>(spectators);
    }

    /**
     * Get number of alive players
     */
    public int getPlayerCount() {
        return activePlayers.size();
    }

    /**
     * Check if a player is in this arena
     */
    public boolean hasPlayer(@Nonnull UUID playerId) {
        return activePlayers.contains(playerId) || spectators.contains(playerId);
    }

    // ========== Arena Boundaries ==========

    /**
     * Check if a position is within arena boundaries
     */
    public boolean isInArena(@Nonnull Vector3d position) {
        double minX = Math.min(corner1.x, corner2.x);
        double maxX = Math.max(corner1.x, corner2.x);
        double minY = Math.min(corner1.y, corner2.y);
        double maxY = Math.max(corner1.y, corner2.y);
        double minZ = Math.min(corner1.z, corner2.z);
        double maxZ = Math.max(corner1.z, corner2.z);

        return position.x >= minX && position.x <= maxX &&
               position.y >= minY && position.y <= maxY &&
               position.z >= minZ && position.z <= maxZ;
    }

    /**
     * Get center point of the arena
     */
    @Nonnull
    public Vector3d getCenterPoint() {
        double x = (corner1.x + corner2.x) / 2;
        double y = (corner1.y + corner2.y) / 2;
        double z = (corner1.z + corner2.z) / 2;
        return new Vector3d(x, y, z);
    }

    /**
     * Reset the arena for a new game
     */
    public void reset() {
        activePlayers.clear();
        spectators.clear();
        gameState = GameState.WAITING;
    }

    // ========== Getters ==========

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDisplayName() {
        return displayName;
    }

    @Nonnull
    public World getWorld() {
        return world;
    }

    @Nonnull
    public Vector3d getLobbySpawn() {
        return lobbySpawn;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getGameTime() {
        return gameTime;
    }

    public int getDeathmatchTime() {
        return deathmatchTime;
    }

    public int getBorderRadius() {
        return borderRadius;
    }

    @Nonnull
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(@Nonnull GameState gameState) {
        this.gameState = gameState;
    }

    @Nonnull
    public Vector3d getCorner1() {
        return corner1;
    }

    @Nonnull
    public Vector3d getCorner2() {
        return corner2;
    }
}
