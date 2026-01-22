package com.hytale.survivalgames;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hytale.survivalgames.commands.*;
import com.hytale.survivalgames.config.SGConfig;
import com.hytale.survivalgames.game.GameManager;
import com.hytale.survivalgames.listeners.PlayerEventListener;
import com.hytale.survivalgames.player.PlayerDataManager;

import javax.annotation.Nonnull;

/**
 * Main plugin class for Hytale Survival Games
 *
 * A complete Survival Games (Hunger Games) implementation for Hytale servers.
 * Features multiple arenas, lobby management, loot systems, and player tracking.
 *
 * @author HytaleDevTeam
 * @version 1.0.0
 */
public class SurvivalGamesPlugin extends JavaPlugin {

    // Singleton instance
    private static SurvivalGamesPlugin instance;

    // Configuration
    private final Config<SGConfig> config;

    // Core managers
    private GameManager gameManager;
    private PlayerDataManager playerDataManager;

    /**
     * Constructor - called when plugin is loaded
     *
     * @param init Plugin initialization context from Hytale
     */
    public SurvivalGamesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;

        // Initialize configuration with Codec
        this.config = this.withConfig("SurvivalGames", SGConfig.CODEC);

        getLogger().info("===========================================");
        getLogger().info("  Hytale Survival Games - Initializing");
        getLogger().info("===========================================");
    }

    /**
     * Setup method - called after plugin is constructed
     * This is where we register commands, events, and initialize managers
     */
    @Override
    protected void setup() {
        getLogger().info("Setting up Survival Games plugin...");

        // Initialize managers
        initializeManagers();

        // Register commands
        registerCommands();

        // Register event listeners
        registerEventListeners();

        getLogger().info("Survival Games plugin setup complete!");
        getLogger().info("Loaded " + gameManager.getArenaCount() + " arena(s)");
    }

    /**
     * Initialize all manager classes
     */
    private void initializeManagers() {
        getLogger().info("Initializing managers...");

        // Player data manager
        this.playerDataManager = new PlayerDataManager(this);

        // Game manager - handles all game logic
        this.gameManager = new GameManager(this);

        getLogger().info("Managers initialized successfully");
    }

    /**
     * Register all plugin commands
     */
    private void registerCommands() {
        getLogger().info("Registering commands...");

        // Main SG command with subcommands
        this.getCommandRegistry().registerCommand(new SGCommand(this));

        // Quick join command
        this.getCommandRegistry().registerCommand(new JoinCommand(this));

        // Leave command
        this.getCommandRegistry().registerCommand(new LeaveCommand(this));

        // List arenas command
        this.getCommandRegistry().registerCommand(new ListCommand(this));

        getLogger().info("Commands registered successfully");
    }

    /**
     * Register all event listeners using the correct Hytale pattern
     */
    private void registerEventListeners() {
        getLogger().info("Registering event listeners...");

        // Player ready event (when player joins server)
        this.getEventRegistry().registerGlobal(
            PlayerReadyEvent.class,
            PlayerEventListener::onPlayerReady
        );

        // Player disconnect event
        this.getEventRegistry().registerGlobal(
            PlayerDisconnectEvent.class,
            PlayerEventListener::onPlayerDisconnect
        );

        // Player chat event
        this.getEventRegistry().registerGlobal(
            PlayerChatEvent.class,
            PlayerEventListener::onPlayerChat
        );

        getLogger().info("Event listeners registered successfully");
    }

    /**
     * Reload plugin configuration and game data
     */
    public void reload() {
        getLogger().info("Reloading Survival Games configuration...");

        // Stop all active games
        gameManager.stopAllGames();

        // Reload config
        config.reload();

        // Reinitialize game manager
        gameManager.reload();

        getLogger().info("Reload complete!");
    }

    // ========== Getters ==========

    /**
     * Get plugin singleton instance
     *
     * @return Plugin instance
     */
    @Nonnull
    public static SurvivalGamesPlugin getInstance() {
        return instance;
    }

    /**
     * Get plugin configuration
     *
     * @return Config instance
     */
    @Nonnull
    public SGConfig getConfig() {
        return config.get();
    }

    /**
     * Get game manager
     *
     * @return GameManager instance
     */
    @Nonnull
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * Get player data manager
     *
     * @return PlayerDataManager instance
     */
    @Nonnull
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
