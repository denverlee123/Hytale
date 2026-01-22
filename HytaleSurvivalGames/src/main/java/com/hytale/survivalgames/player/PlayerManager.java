package com.hytale.survivalgames.player;

import com.hytale.api.Player;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages all SGPlayer instances and player data
 */
public class PlayerManager {

    private final SurvivalGamesPlugin plugin;
    private final Map<UUID, SGPlayer> players;

    public PlayerManager(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.players = new HashMap<>();
    }

    /**
     * Create or get an SGPlayer wrapper for a player
     */
    public SGPlayer createSGPlayer(Player player) {
        UUID playerId = player.getUniqueId();

        if (players.containsKey(playerId)) {
            return players.get(playerId);
        }

        SGPlayer sgPlayer = new SGPlayer(player);
        players.put(playerId, sgPlayer);
        return sgPlayer;
    }

    /**
     * Get an SGPlayer by UUID
     */
    public SGPlayer getSGPlayer(UUID playerId) {
        return players.get(playerId);
    }

    /**
     * Get an SGPlayer by Player object
     */
    public SGPlayer getSGPlayer(Player player) {
        return getSGPlayer(player.getUniqueId());
    }

    /**
     * Remove an SGPlayer (cleanup after game)
     */
    public void removeSGPlayer(UUID playerId) {
        SGPlayer sgPlayer = players.remove(playerId);
        if (sgPlayer != null) {
            sgPlayer.reset();
        }
    }

    /**
     * Check if a player has an SGPlayer instance
     */
    public boolean hasSGPlayer(UUID playerId) {
        return players.containsKey(playerId);
    }

    /**
     * Get all active SGPlayers
     */
    public Map<UUID, SGPlayer> getAllPlayers() {
        return new HashMap<>(players);
    }

    /**
     * Clear all player data (used on plugin disable)
     */
    public void clearAll() {
        players.clear();
    }
}
