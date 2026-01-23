package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;

/**
 * /sgcreate <arena> command - Creates a new survival games arena
 *
 * Currently a stub - needs proper Hytale API documentation to implement
 */
public class CreateArenaCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public CreateArenaCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgcreate", "Create a new Survival Games arena");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Check if command sender is a player
        Player player;
        try {
            player = context.senderAs(Player.class);
        } catch (Exception e) {
            context.sendMessage(Message.raw("Only players can create arenas!"));
            return;
        }

        context.sendMessage(Message.raw("Arena creation command is not yet implemented."));
        context.sendMessage(Message.raw("Waiting for proper Hytale API documentation."));
        context.sendMessage(Message.raw("Need to determine how to access player position in ECS."));
    }
}
