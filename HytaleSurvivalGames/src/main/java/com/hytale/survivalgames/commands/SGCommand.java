package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;

/**
 * Main /sg command
 *
 * Provides information and admin commands for Survival Games
 */
public class SGCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public SGCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sg", "Survival Games main command");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Send help/info message
        context.sendMessage(Message.raw("§e§l=== Survival Games ==="));
        context.sendMessage(Message.raw("§7/sg join [arena] §f- Join a game"));
        context.sendMessage(Message.raw("§7/sg leave §f- Leave current game"));
        context.sendMessage(Message.raw("§7/sg list §f- List all arenas"));
        context.sendMessage(Message.raw("§7/sg stats §f- View your statistics"));
        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("§7Arenas loaded: §e" + plugin.getGameManager().getArenaCount()));
    }
}
