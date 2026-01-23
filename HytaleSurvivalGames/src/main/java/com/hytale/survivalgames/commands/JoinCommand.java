package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;

/**
 * /sgjoin command
 *
 * Allows players to join a Survival Games match
 */
public class JoinCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public JoinCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgjoin", "Join a Survival Games match");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Check if command sender is a player
        Player player;
        try {
            player = context.senderAs(Player.class);
        } catch (Exception e) {
            context.sendMessage(Message.raw("§cOnly players can join games!"));
            return;
        }

        // Check if already in a game
        @SuppressWarnings("removal")
        java.util.UUID playerId = player.getUuid();
        if (plugin.getGameManager().getPlayerArena(playerId) != null) {
            context.sendMessage(Message.raw("§cYou are already in a game! Use /sgleave to leave."));
            return;
        }

        // Find an available arena
        Arena arena = findAvailableArena();

        if (arena == null) {
            context.sendMessage(Message.raw("§cNo arenas are currently available!"));
            context.sendMessage(Message.raw("§7Please wait for a game to start or ask an admin to create an arena."));
            return;
        }

        // Try to join
        if (plugin.getGameManager().joinArena(player, arena)) {
            context.sendMessage(Message.raw("§aYou joined the " + arena.getDisplayName() + " arena!"));
        } else {
            context.sendMessage(Message.raw("§cCould not join arena! It may be full or in progress."));
        }
    }

    /**
     * Find first available arena
     */
    private Arena findAvailableArena() {
        for (Arena arena : plugin.getGameManager().getArenas()) {
            if (arena.getGameState().canJoin() && arena.getPlayerCount() < arena.getMaxPlayers()) {
                return arena;
            }
        }
        return null;
    }
}
