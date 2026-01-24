package com.hytale.survivalgames.commands.admin;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;

/**
 * /sgcreatearena <name> - Create a new Survival Games arena
 */
public class CreateArenaCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;
    private final RequiredArg<String> arenaNameArg;

    public CreateArenaCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgcreatearena", "Create a new Survival Games arena at your location");
        this.plugin = plugin;
        this.arenaNameArg = this.withRequiredArg("name", "Arena name (no spaces)", ArgTypes.STRING);
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

        // Get arena name
        String arenaName = arenaNameArg.get(context);
        if (arenaName == null || arenaName.isEmpty()) {
            context.sendMessage(Message.raw("Please provide an arena name!"));
            return;
        }

        // Get player position
        Vector3d position = getPlayerPosition(player);
        World world = player.getWorld();

        if (world == null) {
            context.sendMessage(Message.raw("Could not determine your world!"));
            return;
        }

        // Create arena with default boundaries (50 block radius from player position)
        Vector3d corner1 = new Vector3d(
            position.getX() - 50,
            position.getY() - 20,
            position.getZ() - 50
        );
        Vector3d corner2 = new Vector3d(
            position.getX() + 50,
            position.getY() + 50,
            position.getZ() + 50
        );

        plugin.getGameManager().createArena(
            arenaName,
            arenaName, // Display name same as ID for now
            world,
            corner1,
            corner2,
            position  // Lobby spawn at player location
        );

        context.sendMessage(Message.raw("Created arena '" + arenaName + "' at your location!"));
        context.sendMessage(Message.raw("Lobby spawn set to your current position."));
        context.sendMessage(Message.raw("Now add spawn points with: /sgaddspawn " + arenaName));
        context.sendMessage(Message.raw("Add chests with: /sgaddchest " + arenaName));
    }

    private Vector3d getPlayerPosition(@Nonnull Player player) {
        if (player.getReference() == null) return new Vector3d(0, 0, 0);

        TransformComponent transform = player.getReference().getStore()
            .getComponent(player.getReference(), TransformComponent.getComponentType());

        if (transform == null) return new Vector3d(0, 0, 0);

        return transform.getPosition();
    }
}
