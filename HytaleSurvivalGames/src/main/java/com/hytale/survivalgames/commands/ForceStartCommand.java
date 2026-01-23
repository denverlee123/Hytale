package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;

/**
 * /sgforcestart <arena> command - Force starts a game in an arena
 *
 * NOTE: This command is a stub and requires Hytale API information to complete.
 * Specifically needs command argument parsing to get the arena name.
 */
public class ForceStartCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public ForceStartCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgforcestart", "Force start a Survival Games match");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("ERROR: Force start requires Hytale API methods not yet implemented."));
        context.sendMessage(Message.raw("Missing: Command argument parsing to get arena name"));
    }
}
