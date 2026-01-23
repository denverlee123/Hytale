package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;

/**
 * /sgaddspawn <arena> command - Adds a spawn point to an arena
 *
 * NOTE: This command is a stub and requires Hytale API information to complete.
 */
public class AddSpawnCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public AddSpawnCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgaddspawn", "Add a spawn point to an arena");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("ERROR: Add spawn requires Hytale API methods not yet implemented."));
        context.sendMessage(Message.raw("Missing:  1) Command argument parsing  2) Player TransformComponent access"));
    }
}
