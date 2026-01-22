package com.hytale.survivalgames.game;

import com.hytale.api.Location;
import com.hytale.api.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Survival Games arena with all its configuration and spawn points
 */
public class Arena {

    private final String name;
    private final String displayName;
    private final World world;

    // Arena boundaries
    private final Location corner1;
    private final Location corner2;
    private final Location lobbySpawn;

    // Spawn points for players
    private final List<Location> spawnPoints;

    // Loot chest locations
    private final List<Location> chestLocations;

    // Game settings
    private final int minPlayers;
    private final int maxPlayers;
    private final int gameTime; // in seconds
    private final int deathmatchTime; // when deathmatch starts
    private final int borderShrinkRadius; // for deathmatch

    // Current game state
    private GameState gameState;
    private final List<UUID> players;
    private final List<UUID> spectators;

    /**
     * Constructor for Arena
     */
    public Arena(String name, String displayName, World world, Location corner1, Location corner2,
                 Location lobbySpawn, int minPlayers, int maxPlayers, int gameTime,
                 int deathmatchTime, int borderShrinkRadius) {
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
        this.borderShrinkRadius = borderShrinkRadius;

        this.spawnPoints = new ArrayList<>();
        this.chestLocations = new ArrayList<>();
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.gameState = GameState.WAITING;
    }

    // Spawn point management

    /**
     * Add a spawn point to the arena
     */
    public void addSpawnPoint(Location location) {
        spawnPoints.add(location);
    }

    /**
     * Get all spawn points
     */
    public List<Location> getSpawnPoints() {
        return new ArrayList<>(spawnPoints);
    }

    /**
     * Get a random spawn point (used for player spawning)
     */
    public Location getRandomSpawnPoint() {
        if (spawnPoints.isEmpty()) {
            return lobbySpawn;
        }
        int index = (int) (Math.random() * spawnPoints.size());
        return spawnPoints.get(index);
    }

    // Chest location management

    /**
     * Add a chest location for loot spawning
     */
    public void addChestLocation(Location location) {
        chestLocations.add(location);
    }

    /**
     * Get all chest locations
     */
    public List<Location> getChestLocations() {
        return new ArrayList<>(chestLocations);
    }

    // Player management

    /**
     * Add a player to the arena
     */
    public boolean addPlayer(UUID playerId) {
        if (players.size() >= maxPlayers) {
            return false;
        }
        if (!gameState.canJoin()) {
            return false;
        }
        players.add(playerId);
        return true;
    }

    /**
     * Remove a player from the arena
     */
    public void removePlayer(UUID playerId) {
        players.remove(playerId);
        spectators.remove(playerId);
    }

    /**
     * Convert a player to spectator
     */
    public void addSpectator(UUID playerId) {
        players.remove(playerId);
        spectators.add(playerId);
    }

    /**
     * Get all active players
     */
    public List<UUID> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Get all spectators
     */
    public List<UUID> getSpectators() {
        return new ArrayList<>(spectators);
    }

    /**
     * Get number of alive players
     */
    public int getPlayerCount() {
        return players.size();
    }

    /**
     * Check if arena is within boundaries
     */
    public boolean isInArena(Location location) {
        if (!location.getWorld().equals(world)) {
            return false;
        }

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    /**
     * Get center point of the arena
     */
    public Location getCenterPoint() {
        double x = (corner1.getX() + corner2.getX()) / 2;
        double y = (corner1.getY() + corner2.getY()) / 2;
        double z = (corner1.getZ() + corner2.getZ()) / 2;
        return new Location(world, x, y, z);
    }

    /**
     * Reset the arena for a new game
     */
    public void reset() {
        players.clear();
        spectators.clear();
        gameState = GameState.WAITING;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public World getWorld() {
        return world;
    }

    public Location getLobbySpawn() {
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

    public int getBorderShrinkRadius() {
        return borderShrinkRadius;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }
}
