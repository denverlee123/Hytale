package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;

/**
 * /sgcreate <arena> command - Creates a new survival games arena
 *
 * NOTE: This command is a stub and requires Hytale API information to complete.
 * Specifically needs: command argument parsing and player position access via TransformComponent.
 */
public class CreateArenaCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public CreateArenaCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgcreate", "Create a new Survival Games arena");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("ERROR: Arena creation requires Hytale API methods not yet implemented."));
        context.sendMessage(Message.raw("Missing:  1) Command argument parsing  2) Player TransformComponent access"));
    }
}
