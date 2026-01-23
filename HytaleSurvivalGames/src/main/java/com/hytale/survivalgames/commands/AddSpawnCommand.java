package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;

/**
 * /sgaddspawn <arena> command - Adds a spawn point to an arena
 *
 * Currently a stub - needs proper Hytale API documentation to implement
 */
public class AddSpawnCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public AddSpawnCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgaddspawn", "Add a spawn point to an arena");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Check if command sender is a player
        Player player;
        try {
            player = context.senderAs(Player.class);
        } catch (Exception e) {
            context.sendMessage(Message.raw("Only players can add spawn points!"));
            return;
        }

        context.sendMessage(Message.raw("Add spawn command is not yet implemented."));
        context.sendMessage(Message.raw("Waiting for proper Hytale API documentation."));
    }
}
