package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;

/**
 * /sgaddchest <arena> command - Adds a chest location to an arena
 *
 * Currently a stub - needs proper Hytale API documentation to implement
 */
public class AddChestCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public AddChestCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgaddchest", "Add a chest loot location to an arena");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Check if command sender is a player
        Player player;
        try {
            player = context.senderAs(Player.class);
        } catch (Exception e) {
            context.sendMessage(Message.raw("Only players can add chest locations!"));
            return;
        }

        context.sendMessage(Message.raw("Add chest command is not yet implemented."));
        context.sendMessage(Message.raw("Waiting for proper Hytale API documentation."));
    }
}
