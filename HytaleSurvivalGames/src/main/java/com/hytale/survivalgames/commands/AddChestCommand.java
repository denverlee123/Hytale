package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;

/**
 * /sgaddchest <arena> command - Adds a chest location to an arena
 *
 * NOTE: This command is a stub and requires Hytale API information to complete.
 */
public class AddChestCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public AddChestCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgaddchest", "Add a chest loot location to an arena");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("ERROR: Add chest requires Hytale API methods not yet implemented."));
        context.sendMessage(Message.raw("Missing:  1) Command argument parsing  2) Player TransformComponent access"));
    }
}
