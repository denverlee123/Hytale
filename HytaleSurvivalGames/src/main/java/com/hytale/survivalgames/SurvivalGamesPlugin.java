package com.hytale.survivalgames;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hytale.survivalgames.commands.*;
import com.hytale.survivalgames.commands.admin.*;
import com.hytale.survivalgames.commands.admin.*;
import com.hytale.survivalgames.config.SGConfig;
import com.hytale.survivalgames.game.GameManager;
import com.hytale.survivalgames.game.LootManager;
import com.hytale.survivalgames.listeners.PlayerEventListener;
import com.hytale.survivalgames.player.PlayerDataManager;
import com.hytale.survivalgames.systems.ArenaProtectionSystem;
import com.hytale.survivalgames.systems.PlayerDeathSystem;

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

    // Logger
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    // Singleton instance
    private static SurvivalGamesPlugin instance;

    // Configuration
    private final Config<SGConfig> config;

    // Core managers
    private GameManager gameManager;
    private PlayerDataManager playerDataManager;
    private LootManager lootManager;

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

        LOGGER.atInfo().log("===========================================");
        LOGGER.atInfo().log("  Hytale Survival Games - Initializing");
        LOGGER.atInfo().log("===========================================");
    }

    /**
     * Setup method - called after plugin is constructed
     * This is where we register commands, events, and initialize managers
     */
    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up Survival Games plugin...");

        // Initialize managers
        initializeManagers();

        // Register commands
        registerCommands();

        // Register event listeners
        registerEventListeners();

        // Register ECS systems
        registerECSSystems();

        LOGGER.atInfo().log("Survival Games plugin setup complete!");
        LOGGER.atInfo().log("Loaded %d arena(s)", gameManager.getArenaCount());
    }

    /**
     * Initialize all manager classes
     */
    private void initializeManagers() {
        LOGGER.atInfo().log("Initializing managers...");

        // Player data manager
        this.playerDataManager = new PlayerDataManager(this);

        // Loot manager - handles chest loot
        this.lootManager = new LootManager(this);

        // Game manager - handles all game logic
        this.gameManager = new GameManager(this);

        LOGGER.atInfo().log("Managers initialized successfully");
    }

    /**
     * Register all plugin commands
     */
    private void registerCommands() {
        LOGGER.atInfo().log("Registering commands...");

        // Player commands
        this.getCommandRegistry().registerCommand(new SGCommand(this));
        this.getCommandRegistry().registerCommand(new JoinCommand(this));
        this.getCommandRegistry().registerCommand(new LeaveCommand(this));
        this.getCommandRegistry().registerCommand(new ListCommand(this));

        // Admin commands
        this.getCommandRegistry().registerCommand(new CreateArenaCommand(this));
        this.getCommandRegistry().registerCommand(new AddSpawnCommand(this));
        this.getCommandRegistry().registerCommand(new AddChestCommand(this));
        this.getCommandRegistry().registerCommand(new ForceStartCommand(this));
        this.getCommandRegistry().registerCommand(new ArenaInfoCommand(this));

        LOGGER.atInfo().log("Commands registered successfully");
    }

    /**
     * Register all event listeners using the correct Hytale pattern
     */
    private void registerEventListeners() {
        LOGGER.atInfo().log("Registering event listeners...");

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

        LOGGER.atInfo().log("Event listeners registered successfully");
    }

    /**
     * Register ECS systems for death detection and arena protection
     */
    private void registerECSSystems() {
        LOGGER.atInfo().log("Registering ECS systems...");

        // Death detection system
        this.getEntityStoreRegistry().registerSystem(new PlayerDeathSystem(this));

        // Arena protection systems
        this.getEntityStoreRegistry().registerSystem(new ArenaProtectionSystem.BlockBreakProtection(this));
        this.getEntityStoreRegistry().registerSystem(new ArenaProtectionSystem.BlockPlaceProtection(this));

        LOGGER.atInfo().log("ECS systems registered successfully");
    }

    /**
     * Reload plugin configuration and game data
     */
    public void reload() {
        LOGGER.atInfo().log("Reloading Survival Games configuration...");

        // Stop all active games
        gameManager.stopAllGames();

        // TODO: Reload config (Config API doesn't expose reload method)
        // config.reload();

        // Reinitialize game manager
        gameManager.reload();

        LOGGER.atInfo().log("Reload complete!");
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

    /**
     * Get loot manager
     *
     * @return LootManager instance
     */
    @Nonnull
    public LootManager getLootManager() {
        return lootManager;
    }
}
