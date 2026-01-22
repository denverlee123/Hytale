package com.hytale.survivalgames.player;

import com.hytale.api.Player;
import com.hytale.api.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Wrapper class for a player in a Survival Games match
 * Tracks kills, deaths, and other game-specific stats
 */
public class SGPlayer {

    private final UUID playerId;
    private final Player player;

    // Game statistics
    private int kills;
    private int deaths;
    private int gamesPlayed;
    private int wins;

    // Current match data
    private long matchStartTime;
    private Map<String, ItemStack> savedInventory;
    private boolean alive;

    /**
     * Constructor
     */
    public SGPlayer(Player player) {
        this.playerId = player.getUniqueId();
        this.player = player;
        this.kills = 0;
        this.deaths = 0;
        this.gamesPlayed = 0;
        this.wins = 0;
        this.alive = true;
        this.savedInventory = new HashMap<>();
    }

    /**
     * Called when match starts
     */
    public void onMatchStart() {
        matchStartTime = System.currentTimeMillis();
        alive = true;
        gamesPlayed++;
    }

    /**
     * Called when player gets a kill
     */
    public void addKill() {
        kills++;
    }

    /**
     * Called when player dies
     */
    public void onDeath() {
        deaths++;
        alive = false;
    }

    /**
     * Called when player wins
     */
    public void onWin() {
        wins++;
    }

    /**
     * Save player's current inventory (for restoration after game)
     */
    public void saveInventory() {
        savedInventory.clear();
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null) {
                savedInventory.put(String.valueOf(i), item.clone());
            }
        }
    }

    /**
     * Restore player's saved inventory
     */
    public void restoreInventory() {
        player.getInventory().clear();
        for (Map.Entry<String, ItemStack> entry : savedInventory.entrySet()) {
            int slot = Integer.parseInt(entry.getKey());
            player.getInventory().setItem(slot, entry.getValue());
        }
        savedInventory.clear();
    }

    /**
     * Clear all current match data
     */
    public void reset() {
        kills = 0;
        alive = true;
        savedInventory.clear();
    }

    /**
     * Get time survived in current match (in seconds)
     */
    public long getTimeSurvived() {
        if (matchStartTime == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - matchStartTime) / 1000;
    }

    // Getters and setters

    public UUID getPlayerId() {
        return playerId;
    }

    public Player getPlayer() {
        return player;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Get kill/death ratio
     */
    public double getKDRatio() {
        if (deaths == 0) {
            return kills;
        }
        return (double) kills / deaths;
    }

    /**
     * Get win rate percentage
     */
    public double getWinRate() {
        if (gamesPlayed == 0) {
            return 0;
        }
        return ((double) wins / gamesPlayed) * 100;
    }
}
