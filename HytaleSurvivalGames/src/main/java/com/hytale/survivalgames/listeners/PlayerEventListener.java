package com.hytale.survivalgames.listeners;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;

/**
 * Event listener for player-related events
 *
 * Uses static methods as required by Hytale's event registration system
 */
public class PlayerEventListener {

    /**
     * Called when a player is ready (finished joining the server)
     *
     * @param event PlayerReadyEvent
     */
    public static void onPlayerReady(@Nonnull PlayerReadyEvent event) {
        Player player = event.getPlayer();
        SurvivalGamesPlugin plugin = SurvivalGamesPlugin.getInstance();

        // Send welcome message
        String welcomeMsg = plugin.getConfig().getWelcomeMessage();
        player.sendMessage(Message.raw(welcomeMsg));

        plugin.getLogger().log("Player " + player.getDisplayName() + " joined the server");
    }

    /**
     * Called when a player disconnects from the server
     *
     * @param event PlayerDisconnectEvent
     */
    public static void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
        SurvivalGamesPlugin plugin = SurvivalGamesPlugin.getInstance();

        // Get player UUID from event
        java.util.UUID playerId = event.getPlayerRef().getUuid();

        // Check if player is in a game
        Arena arena = plugin.getGameManager().getPlayerArena(playerId);
        if (arena != null) {
            // Get Player object to leave arena
            Player player = plugin.getServer().getPlayer(playerId).orElse(null);
            if (player != null) {
                plugin.getGameManager().leaveArena(player);
                plugin.getLogger().log("Player disconnected from game in arena: " + arena.getName());
            }
        }

        // Clean up player data
        plugin.getPlayerDataManager().removePlayerData(playerId);
    }

    /**
     * Called when a player sends a chat message
     *
     * @param event PlayerChatEvent
     */
    public static void onPlayerChat(@Nonnull PlayerChatEvent event) {
        SurvivalGamesPlugin plugin = SurvivalGamesPlugin.getInstance();

        // Get player UUID from sender
        java.util.UUID playerId = event.getSender().getUuid();

        // Check if player is in a game
        Arena arena = plugin.getGameManager().getPlayerArena(playerId);
        if (arena != null) {
            // Optional: Format chat differently for players in games
            // This would use event.setFormatter() if needed

            if (plugin.getConfig().isDebug()) {
                plugin.getLogger().log("[Arena: " + arena.getName() + "] " +
                    event.getSender().getUsername() + ": " + event.getContent());
            }
        }
    }
}
