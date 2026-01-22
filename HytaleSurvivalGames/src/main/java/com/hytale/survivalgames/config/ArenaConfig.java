package com.hytale.survivalgames.config;

import com.hytale.api.Location;

import java.util.List;

/**
 * Data class representing arena configuration from file
 */
public class ArenaConfig {

    private String name;
    private String displayName;
    private String worldName;

    // Boundaries
    private double corner1X, corner1Y, corner1Z;
    private double corner2X, corner2Y, corner2Z;

    // Lobby spawn
    private double lobbyX, lobbyY, lobbyZ;
    private float lobbyYaw, lobbyPitch;

    // Spawn points
    private List<SpawnPoint> spawnPoints;

    // Chest locations
    private List<ChestLocation> chestLocations;

    // Game settings
    private int minPlayers = 2;
    private int maxPlayers = 24;
    private int gameTime = 600; // 10 minutes default
    private int deathmatchTime = 300; // 5 minutes until deathmatch
    private int borderShrinkRadius = 50;

    /**
     * Nested class for spawn point data
     */
    public static class SpawnPoint {
        private double x, y, z;
        private float yaw, pitch;

        public SpawnPoint() {}

        public SpawnPoint(double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        // Getters and setters
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
        public double getZ() { return z; }
        public void setZ(double z) { this.z = z; }
        public float getYaw() { return yaw; }
        public void setYaw(float yaw) { this.yaw = yaw; }
        public float getPitch() { return pitch; }
        public void setPitch(float pitch) { this.pitch = pitch; }
    }

    /**
     * Nested class for chest location data
     */
    public static class ChestLocation {
        private double x, y, z;

        public ChestLocation() {}

        public ChestLocation(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        // Getters and setters
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
        public double getZ() { return z; }
        public void setZ(double z) { this.z = z; }
    }

    // Getters and setters for ArenaConfig

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getWorldName() { return worldName; }
    public void setWorldName(String worldName) { this.worldName = worldName; }

    public double getCorner1X() { return corner1X; }
    public void setCorner1X(double corner1X) { this.corner1X = corner1X; }

    public double getCorner1Y() { return corner1Y; }
    public void setCorner1Y(double corner1Y) { this.corner1Y = corner1Y; }

    public double getCorner1Z() { return corner1Z; }
    public void setCorner1Z(double corner1Z) { this.corner1Z = corner1Z; }

    public double getCorner2X() { return corner2X; }
    public void setCorner2X(double corner2X) { this.corner2X = corner2X; }

    public double getCorner2Y() { return corner2Y; }
    public void setCorner2Y(double corner2Y) { this.corner2Y = corner2Y; }

    public double getCorner2Z() { return corner2Z; }
    public void setCorner2Z(double corner2Z) { this.corner2Z = corner2Z; }

    public double getLobbyX() { return lobbyX; }
    public void setLobbyX(double lobbyX) { this.lobbyX = lobbyX; }

    public double getLobbyY() { return lobbyY; }
    public void setLobbyY(double lobbyY) { this.lobbyY = lobbyY; }

    public double getLobbyZ() { return lobbyZ; }
    public void setLobbyZ(double lobbyZ) { this.lobbyZ = lobbyZ; }

    public float getLobbyYaw() { return lobbyYaw; }
    public void setLobbyYaw(float lobbyYaw) { this.lobbyYaw = lobbyYaw; }

    public float getLobbyPitch() { return lobbyPitch; }
    public void setLobbyPitch(float lobbyPitch) { this.lobbyPitch = lobbyPitch; }

    public List<SpawnPoint> getSpawnPoints() { return spawnPoints; }
    public void setSpawnPoints(List<SpawnPoint> spawnPoints) { this.spawnPoints = spawnPoints; }

    public List<ChestLocation> getChestLocations() { return chestLocations; }
    public void setChestLocations(List<ChestLocation> chestLocations) { this.chestLocations = chestLocations; }

    public int getMinPlayers() { return minPlayers; }
    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public int getGameTime() { return gameTime; }
    public void setGameTime(int gameTime) { this.gameTime = gameTime; }

    public int getDeathmatchTime() { return deathmatchTime; }
    public void setDeathmatchTime(int deathmatchTime) { this.deathmatchTime = deathmatchTime; }

    public int getBorderShrinkRadius() { return borderShrinkRadius; }
    public void setBorderShrinkRadius(int borderShrinkRadius) { this.borderShrinkRadius = borderShrinkRadius; }
}
