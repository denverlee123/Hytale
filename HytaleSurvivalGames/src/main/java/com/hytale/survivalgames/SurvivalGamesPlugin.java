package com.hytale.survivalgames;

import com.hytale.api.HytalePlugin;
import com.hytale.api.command.CommandManager;
import com.hytale.api.event.EventManager;
import com.hytale.api.scheduler.Scheduler;
import com.hytale.survivalgames.commands.SGCommand;
import com.hytale.survivalgames.config.ConfigManager;
import com.hytale.survivalgames.game.GameManager;
import com.hytale.survivalgames.listeners.PlayerListener;
import com.hytale.survivalgames.loot.LootManager;
import com.hytale.survivalgames.player.PlayerManager;

import java.util.logging.Logger;

/**
 * Main plugin class for Hytale Survival Games
 * Handles plugin lifecycle, initialization, and manages core components
 */
public class SurvivalGamesPlugin extends HytalePlugin {

    private static SurvivalGamesPlugin instance;
    private Logger logger;

    // Core managers
    private ConfigManager configManager;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private LootManager lootManager;

    // API references
    private Scheduler scheduler;
    private EventManager eventManager;
    private CommandManager commandManager;

    /**
     * Called when the plugin is enabled
     * Initialize all managers and register commands/events
     */
    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        logger.info("===========================================");
        logger.info("  Hytale Survival Games - Starting...");
        logger.info("===========================================");

        // Initialize API references
        scheduler = getServer().getScheduler();
        eventManager = getServer().getEventManager();
        commandManager = getServer().getCommandManager();

        // Initialize managers in correct order
        initializeManagers();

        // Register commands
        registerCommands();

        // Register event listeners
        registerListeners();

        logger.info("Hytale Survival Games enabled successfully!");
        logger.info("Loaded " + configManager.getArenas().size() + " arena(s)");
    }

    /**
     * Called when the plugin is disabled
     * Cleanup and save data
     */
    @Override
    public void onDisable() {
        logger.info("Disabling Hytale Survival Games...");

        // Stop all active games
        if (gameManager != null) {
            gameManager.stopAllGames();
        }

        // Save configurations
        if (configManager != null) {
            configManager.saveConfigs();
        }

        logger.info("Hytale Survival Games disabled successfully!");
    }

    /**
     * Initialize all manager classes
     */
    private void initializeManagers() {
        logger.info("Initializing managers...");

        // Config manager must be first
        configManager = new ConfigManager(this);
        configManager.loadConfigs();

        // Initialize other managers
        lootManager = new LootManager(this);
        playerManager = new PlayerManager(this);
        gameManager = new GameManager(this);

        logger.info("All managers initialized successfully");
    }

    /**
     * Register all plugin commands
     */
    private void registerCommands() {
        logger.info("Registering commands...");

        SGCommand sgCommand = new SGCommand(this);
        commandManager.registerCommand("sg", sgCommand);
        commandManager.registerCommand("sgjoin", sgCommand);
        commandManager.registerCommand("sgleave", sgCommand);

        logger.info("Commands registered successfully");
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        logger.info("Registering event listeners...");

        PlayerListener playerListener = new PlayerListener(this);
        eventManager.registerListener(this, playerListener);

        logger.info("Event listeners registered successfully");
    }

    /**
     * Reload plugin configuration
     */
    public void reload() {
        logger.info("Reloading Survival Games configuration...");

        // Stop all active games before reload
        gameManager.stopAllGames();

        // Reload configs
        configManager.loadConfigs();

        // Reinitialize loot tables
        lootManager.reload();

        logger.info("Reload complete!");
    }

    // Getters for managers and API access

    public static SurvivalGamesPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
