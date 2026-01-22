package com.hytale.survivalgames.player;

import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player statistics and data
 *
 * Tracks kills, deaths, wins, and other player-specific data
 */
public class PlayerDataManager {

    private final SurvivalGamesPlugin plugin;
    private final Map<UUID, PlayerData> playerData;

    public PlayerDataManager(@Nonnull SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.playerData = new ConcurrentHashMap<>();
    }

    /**
     * Get or create player data
     */
    @Nonnull
    public PlayerData getPlayerData(@Nonnull UUID playerId) {
        return playerData.computeIfAbsent(playerId, PlayerData::new);
    }

    /**
     * Remove player data (cleanup)
     */
    public void removePlayerData(@Nonnull UUID playerId) {
        playerData.remove(playerId);
    }

    /**
     * Check if player has data
     */
    public boolean hasPlayerData(@Nonnull UUID playerId) {
        return playerData.containsKey(playerId);
    }

    /**
     * Player data class
     */
    public static class PlayerData {
        private final UUID playerId;
        private int kills;
        private int deaths;
        private int wins;
        private int gamesPlayed;

        public PlayerData(@Nonnull UUID playerId) {
            this.playerId = playerId;
            this.kills = 0;
            this.deaths = 0;
            this.wins = 0;
            this.gamesPlayed = 0;
        }

        public UUID getPlayerId() {
            return playerId;
        }

        public int getKills() {
            return kills;
        }

        public void addKill() {
            this.kills++;
        }

        public int getDeaths() {
            return deaths;
        }

        public void addDeath() {
            this.deaths++;
        }

        public int getWins() {
            return wins;
        }

        public void addWin() {
            this.wins++;
        }

        public int getGamesPlayed() {
            return gamesPlayed;
        }

        public void incrementGamesPlayed() {
            this.gamesPlayed++;
        }

        public double getKDRatio() {
            if (deaths == 0) {
                return kills;
            }
            return (double) kills / deaths;
        }

        public double getWinRate() {
            if (gamesPlayed == 0) {
                return 0;
            }
            return ((double) wins / gamesPlayed) * 100;
        }
    }
}
