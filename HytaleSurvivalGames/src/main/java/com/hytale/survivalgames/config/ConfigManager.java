package com.hytale.survivalgames.config;

import com.hytale.api.Location;
import com.hytale.api.World;
import com.hytale.api.configuration.Configuration;
import com.hytale.api.configuration.ConfigurationSection;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages plugin configuration files
 * Loads arenas, settings, and loot tables from YAML files
 */
public class ConfigManager {

    private final SurvivalGamesPlugin plugin;
    private Configuration mainConfig;
    private Configuration arenasConfig;
    private Configuration lootConfig;

    private final List<Arena> arenas;

    public ConfigManager(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.arenas = new ArrayList<>();
    }

    /**
     * Load all configuration files
     */
    public void loadConfigs() {
        // Create config directory if it doesn't exist
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        // Load main config
        loadMainConfig();

        // Load arenas config
        loadArenasConfig();

        // Load loot config
        loadLootConfig();

        plugin.getLogger().info("All configurations loaded successfully");
    }

    /**
     * Load main plugin configuration
     */
    private void loadMainConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        mainConfig = Configuration.loadConfiguration(configFile);
        plugin.getLogger().info("Main config loaded");
    }

    /**
     * Load arenas configuration
     */
    private void loadArenasConfig() {
        arenas.clear();

        File arenasFile = new File(plugin.getDataFolder(), "arenas.yml");

        if (!arenasFile.exists()) {
            plugin.saveResource("arenas.yml", false);
        }

        arenasConfig = Configuration.loadConfiguration(arenasFile);

        // Parse arenas from config
        ConfigurationSection arenasSection = arenasConfig.getConfigurationSection("arenas");
        if (arenasSection == null) {
            plugin.getLogger().warning("No arenas defined in arenas.yml!");
            return;
        }

        for (String arenaName : arenasSection.getKeys(false)) {
            try {
                Arena arena = loadArena(arenaName, arenasSection.getConfigurationSection(arenaName));
                if (arena != null) {
                    arenas.add(arena);
                    plugin.getLogger().info("Loaded arena: " + arenaName);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load arena " + arenaName + ": " + e.getMessage());
            }
        }

        plugin.getLogger().info("Loaded " + arenas.size() + " arenas");
    }

    /**
     * Load a single arena from configuration
     */
    private Arena loadArena(String name, ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        // Basic info
        String displayName = section.getString("displayName", name);
        String worldName = section.getString("world");

        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("World " + worldName + " not found for arena " + name);
            return null;
        }

        // Boundaries
        ConfigurationSection corner1Sec = section.getConfigurationSection("corner1");
        ConfigurationSection corner2Sec = section.getConfigurationSection("corner2");

        Location corner1 = new Location(
            world,
            corner1Sec.getDouble("x"),
            corner1Sec.getDouble("y"),
            corner1Sec.getDouble("z")
        );

        Location corner2 = new Location(
            world,
            corner2Sec.getDouble("x"),
            corner2Sec.getDouble("y"),
            corner2Sec.getDouble("z")
        );

        // Lobby spawn
        ConfigurationSection lobbySec = section.getConfigurationSection("lobby");
        Location lobbySpawn = new Location(
            world,
            lobbySec.getDouble("x"),
            lobbySec.getDouble("y"),
            lobbySec.getDouble("z"),
            (float) lobbySec.getDouble("yaw", 0),
            (float) lobbySec.getDouble("pitch", 0)
        );

        // Game settings
        int minPlayers = section.getInt("minPlayers", 2);
        int maxPlayers = section.getInt("maxPlayers", 24);
        int gameTime = section.getInt("gameTime", 600);
        int deathmatchTime = section.getInt("deathmatchTime", 300);
        int borderShrinkRadius = section.getInt("borderShrinkRadius", 50);

        // Create arena
        Arena arena = new Arena(
            name, displayName, world, corner1, corner2,
            lobbySpawn, minPlayers, maxPlayers, gameTime,
            deathmatchTime, borderShrinkRadius
        );

        // Load spawn points
        List<?> spawnPointsList = section.getList("spawnPoints");
        if (spawnPointsList != null) {
            for (Object obj : spawnPointsList) {
                if (obj instanceof ConfigurationSection) {
                    ConfigurationSection spawnSec = (ConfigurationSection) obj;
                    Location spawn = new Location(
                        world,
                        spawnSec.getDouble("x"),
                        spawnSec.getDouble("y"),
                        spawnSec.getDouble("z"),
                        (float) spawnSec.getDouble("yaw", 0),
                        (float) spawnSec.getDouble("pitch", 0)
                    );
                    arena.addSpawnPoint(spawn);
                }
            }
        }

        // Load chest locations
        List<?> chestLocationsList = section.getList("chestLocations");
        if (chestLocationsList != null) {
            for (Object obj : chestLocationsList) {
                if (obj instanceof ConfigurationSection) {
                    ConfigurationSection chestSec = (ConfigurationSection) obj;
                    Location chestLoc = new Location(
                        world,
                        chestSec.getDouble("x"),
                        chestSec.getDouble("y"),
                        chestSec.getDouble("z")
                    );
                    arena.addChestLocation(chestLoc);
                }
            }
        }

        return arena;
    }

    /**
     * Load loot configuration
     */
    private void loadLootConfig() {
        File lootFile = new File(plugin.getDataFolder(), "loot.yml");

        if (!lootFile.exists()) {
            plugin.saveResource("loot.yml", false);
        }

        lootConfig = Configuration.loadConfiguration(lootFile);
        plugin.getLogger().info("Loot config loaded");
    }

    /**
     * Save all configurations
     */
    public void saveConfigs() {
        try {
            if (mainConfig != null) {
                mainConfig.save(new File(plugin.getDataFolder(), "config.yml"));
            }
            if (arenasConfig != null) {
                arenasConfig.save(new File(plugin.getDataFolder(), "arenas.yml"));
            }
            if (lootConfig != null) {
                lootConfig.save(new File(plugin.getDataFolder(), "loot.yml"));
            }
            plugin.getLogger().info("Configurations saved");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save configurations: " + e.getMessage());
        }
    }

    /**
     * Get all loaded arenas
     */
    public List<Arena> getArenas() {
        return new ArrayList<>(arenas);
    }

    /**
     * Get main config
     */
    public Configuration getMainConfig() {
        return mainConfig;
    }

    /**
     * Get arenas config
     */
    public Configuration getArenasConfig() {
        return arenasConfig;
    }

    /**
     * Get loot config
     */
    public Configuration getLootConfig() {
        return lootConfig;
    }
}
