package com.hytale.survivalgames.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

/**
 * Main configuration for Survival Games plugin
 *
 * Saved under: plugins/com.hytale_HytaleSurvivalGames/SurvivalGames.example.json
 *
 * This uses Hytale's Codec system for serialization/deserialization
 */
public class SGConfig {

    /**
     * Codec for serializing and deserializing the SGConfig class
     */
    public static final BuilderCodec<SGConfig> CODEC = BuilderCodec
            .builder(SGConfig.class, SGConfig::new)
            // Minimum players to start a game
            .append(new KeyedCodec<>("minPlayers", Codec.INTEGER),
                    (config, value, info) -> config.minPlayers = value,
                    (config, info) -> config.minPlayers)
            .add()
            // Maximum players per arena
            .append(new KeyedCodec<>("maxPlayers", Codec.INTEGER),
                    (config, value, info) -> config.maxPlayers = value,
                    (config, info) -> config.maxPlayers)
            .add()
            // Game duration in seconds
            .append(new KeyedCodec<>("gameTime", Codec.INTEGER),
                    (config, value, info) -> config.gameTime = value,
                    (config, info) -> config.gameTime)
            .add()
            // Countdown before game starts
            .append(new KeyedCodec<>("countdownTime", Codec.INTEGER),
                    (config, value, info) -> config.countdownTime = value,
                    (config, info) -> config.countdownTime)
            .add()
            // Time until deathmatch starts
            .append(new KeyedCodec<>("deathmatchTime", Codec.INTEGER),
                    (config, value, info) -> config.deathmatchTime = value,
                    (config, info) -> config.deathmatchTime)
            .add()
            // Border shrink radius for deathmatch
            .append(new KeyedCodec<>("borderRadius", Codec.INTEGER),
                    (config, value, info) -> config.borderRadius = value,
                    (config, info) -> config.borderRadius)
            .add()
            // Enable debug messages
            .append(new KeyedCodec<>("debug", Codec.BOOLEAN),
                    (config, value, info) -> config.debug = value,
                    (config, info) -> config.debug)
            .add()
            // Broadcast game start to server
            .append(new KeyedCodec<>("broadcastStart", Codec.BOOLEAN),
                    (config, value, info) -> config.broadcastStart = value,
                    (config, info) -> config.broadcastStart)
            .add()
            // Broadcast game end to server
            .append(new KeyedCodec<>("broadcastEnd", Codec.BOOLEAN),
                    (config, value, info) -> config.broadcastEnd = value,
                    (config, info) -> config.broadcastEnd)
            .add()
            // Welcome message
            .append(new KeyedCodec<>("welcomeMessage", Codec.STRING),
                    (config, value, info) -> config.welcomeMessage = value,
                    (config, info) -> config.welcomeMessage)
            .add()
            .build();

    // ========== Configuration Fields ==========

    // Game settings
    private int minPlayers = 2;
    private int maxPlayers = 24;
    private int gameTime = 600; // 10 minutes
    private int countdownTime = 10; // 10 seconds
    private int deathmatchTime = 300; // 5 minutes
    private int borderRadius = 50;

    // Plugin settings
    private boolean debug = false;
    private boolean broadcastStart = true;
    private boolean broadcastEnd = true;

    // Messages
    private String welcomeMessage = "Welcome to Survival Games! Use /sg join to play.";

    /**
     * Default constructor required by Codec
     */
    public SGConfig() {
    }

    // ========== Getters ==========

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getGameTime() {
        return gameTime;
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public int getDeathmatchTime() {
        return deathmatchTime;
    }

    public int getBorderRadius() {
        return borderRadius;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isBroadcastStart() {
        return broadcastStart;
    }

    public boolean isBroadcastEnd() {
        return broadcastEnd;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }
}
