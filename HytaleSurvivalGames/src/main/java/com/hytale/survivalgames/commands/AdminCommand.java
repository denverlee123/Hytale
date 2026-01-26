package com.hytale.survivalgames.commands;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;

/**
 * /sgadmin command for arena management
 *
 * Allows admins to create arenas, add spawn points, and configure chests
 */
public class AdminCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public AdminCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgadmin", "Admin commands for Survival Games arena management");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Check if command sender is a player
        Player player;
        try {
            player = context.senderAs(Player.class);
        } catch (Exception e) {
            context.sendMessage(Message.raw("Only players can use admin commands!"));
            return;
        }

        // TODO: Add permission check
        // if (!player.hasPermission("survivalgames.admin")) {
        //     context.sendMessage(Message.raw("You don't have permission!"));
        //     return;
        // }

        // Show help
        context.sendMessage(Message.raw("=== Survival Games Admin ==="));
        context.sendMessage(Message.raw("/sgadmin create <name> - Create arena at your location"));
        context.sendMessage(Message.raw("/sgadmin addspawn <arena> - Add spawn point at your location"));
        context.sendMessage(Message.raw("/sgadmin addchest <arena> - Add chest location at your location"));
        context.sendMessage(Message.raw("/sgadmin info <arena> - Show arena info"));
        context.sendMessage(Message.raw("/sgadmin setlobby <arena> - Set lobby spawn at your location"));
        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("Use your current position for all location-based commands"));
    }

    /**
     * Get player's current position
     */
    private Vector3d getPlayerPosition(@Nonnull Player player) {
        if (player.getReference() == null) return new Vector3d(0, 0, 0);

        TransformComponent transform = player.getReference().getStore()
            .getComponent(player.getReference(), TransformComponent.getComponentType());

        if (transform == null) return new Vector3d(0, 0, 0);

        return transform.getPosition();
    }
}
