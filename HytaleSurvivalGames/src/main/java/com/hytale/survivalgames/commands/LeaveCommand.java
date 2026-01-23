package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;

/**
 * /sgleave command
 *
 * Allows players to leave their current Survival Games match
 */
public class LeaveCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public LeaveCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgleave", "Leave your current Survival Games match");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Check if command sender is a player
        Player player;
        try {
            player = context.senderAs(Player.class);
        } catch (Exception e) {
            context.sendMessage(Message.raw("§cOnly players can leave games!"));
            return;
        }

        // Check if in a game
        Arena arena = plugin.getGameManager().getPlayerArena(player.getId());
        if (arena == null) {
            context.sendMessage(Message.raw("§cYou are not in a game!"));
            return;
        }

        // Leave the arena
        plugin.getGameManager().leaveArena(player);
        context.sendMessage(Message.raw("§aYou left the game!"));
    }
}
