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

        plugin.getLogger().info("Player " + player.getDisplayName() + " joined the server");
    }

    /**
     * Called when a player disconnects from the server
     *
     * @param event PlayerDisconnectEvent
     */
    public static void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
        Player player = event.getPlayer();
        SurvivalGamesPlugin plugin = SurvivalGamesPlugin.getInstance();

        // Check if player is in a game
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUUID());
        if (arena != null) {
            // Remove from arena
            plugin.getGameManager().leaveArena(player);
            plugin.getLogger().info("Player " + player.getDisplayName() + " disconnected from game in arena: " + arena.getName());
        }

        // Clean up player data
        plugin.getPlayerDataManager().removePlayerData(player.getUUID());
    }

    /**
     * Called when a player sends a chat message
     *
     * @param event PlayerChatEvent
     */
    public static void onPlayerChat(@Nonnull PlayerChatEvent event) {
        Player player = event.getPlayer();
        SurvivalGamesPlugin plugin = SurvivalGamesPlugin.getInstance();

        // Check if player is in a game
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUUID());
        if (arena != null) {
            // Optional: Format chat differently for players in games
            // This would use event.setFormatter() if needed

            if (plugin.getConfig().isDebug()) {
                plugin.getLogger().info("[Arena: " + arena.getName() + "] " +
                    player.getDisplayName() + ": " + event.getMessage());
            }
        }
    }
}
