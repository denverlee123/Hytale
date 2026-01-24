package com.hytale.survivalgames.commands.admin;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;

/**
 * /sgaddspawn <arena> - Add a spawn point to an arena
 */
public class AddSpawnCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;
    private final RequiredArg<String> arenaNameArg;

    public AddSpawnCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgaddspawn", "Add a spawn point to an arena at your location");
        this.plugin = plugin;
        this.arenaNameArg = this.withRequiredArg("arena", "Arena name", ArgTypes.STRING);
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

        // Get arena name
        String arenaName = arenaNameArg.get(context);
        Arena arena = plugin.getGameManager().getArena(arenaName);

        if (arena == null) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' not found!"));
            context.sendMessage(Message.raw("Use /sglist to see all arenas."));
            return;
        }

        // Get player position
        Vector3d position = getPlayerPosition(player);

        // Add spawn point
        arena.addSpawnPoint(position);

        context.sendMessage(Message.raw("Added spawn point #" + arena.getSpawnPoints().size() + " to arena '" + arenaName + "'"));
        context.sendMessage(Message.raw("Position: " + formatVector(position)));
    }

    private Vector3d getPlayerPosition(@Nonnull Player player) {
        if (player.getReference() == null) return new Vector3d(0, 0, 0);

        TransformComponent transform = player.getReference().getStore()
            .getComponent(player.getReference(), TransformComponent.getComponentType());

        if (transform == null) return new Vector3d(0, 0, 0);

        return transform.getPosition();
    }

    private String formatVector(@Nonnull Vector3d vec) {
        return String.format("%.1f, %.1f, %.1f", vec.getX(), vec.getY(), vec.getZ());
    }
}
