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
        context.sendMessage(Message.raw("=== Survival Games ==="));
        context.sendMessage(Message.raw("/sg join [arena] - Join a game"));
        context.sendMessage(Message.raw("/sg leave - Leave current game"));
        context.sendMessage(Message.raw("/sg list - List all arenas"));
        context.sendMessage(Message.raw("/sg create <name> - Create new arena"));
        context.sendMessage(Message.raw("/sg addspawn <arena> - Add spawn point at your location"));
        context.sendMessage(Message.raw("/sg addchest <arena> - Add chest location at your position"));
        context.sendMessage(Message.raw("/sg forcestart <arena> - Force start a game"));
        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("Arenas loaded: " + plugin.getGameManager().getArenaCount()));
    }
}
