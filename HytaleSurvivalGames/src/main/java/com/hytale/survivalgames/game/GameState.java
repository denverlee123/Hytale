package com.hytale.survivalgames.game;

/**
 * Represents the different states a Survival Games match can be in
 */
public enum GameState {
    /**
     * Waiting for players to join in the lobby
     */
    WAITING,

    /**
     * Countdown before match starts (players are frozen at spawn points)
     */
    STARTING,

    /**
     * Active game in progress
     */
    IN_GAME,

    /**
     * Death match phase (border shrinking, forcing players together)
     */
    DEATHMATCH,

    /**
     * Game has ended, winner announced
     */
    ENDING,

    /**
     * Arena is disabled or being reset
     */
    DISABLED;

    /**
     * Check if the game is in an active playing state
     *
     * @return true if state is IN_GAME or DEATHMATCH
     */
    public boolean isActive() {
        return this == IN_GAME || this == DEATHMATCH;
    }

    /**
     * Check if players can join the game
     *
     * @return true if state is WAITING
     */
    public boolean canJoin() {
        return this == WAITING;
    }

    /**
     * Check if the game is running (any state except WAITING and DISABLED)
     *
     * @return true if game is running
     */
    public boolean isRunning() {
        return this != WAITING && this != DISABLED;
    }
}
