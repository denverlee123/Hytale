package com.hytale.survivalgames.commands;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hytale.survivalgames.SurvivalGamesPlugin;

import javax.annotation.Nonnull;

/**
 * /sgcreate <arena> command - Creates a new survival games arena
 *
 * Creates a new arena at the player's current location.
 * The arena will use a default 100x100x100 area centered on the player.
 */
public class CreateArenaCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;
    private final RequiredArg<String> arenaNameArg;

    public CreateArenaCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgcreate", "Create a new Survival Games arena");
        this.plugin = plugin;
        this.arenaNameArg = this.withRequiredArg("name", "Arena name", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef,
                          @Nonnull World world) {
        // Get arena name from command argument
        String arenaName = arenaNameArg.get(context);

        // Check if arena already exists
        if (plugin.getGameManager().getArena(arenaName) != null) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' already exists!"));
            return;
        }

        // Get player's current position via TransformComponent
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            context.sendMessage(Message.raw("Error: Could not get player position!"));
            return;
        }

        Vector3d playerPos = transform.getPosition();

        // Create arena boundaries (100x100x100 area centered on player)
        Vector3d corner1 = new Vector3d(playerPos.x - 50, playerPos.y - 10, playerPos.z - 50);
        Vector3d corner2 = new Vector3d(playerPos.x + 50, playerPos.y + 40, playerPos.z + 50);
        Vector3d lobbySpawn = playerPos;

        // Create the arena
        plugin.getGameManager().createArena(
            arenaName,
            arenaName, // Display name same as internal name
            world,
            corner1,
            corner2,
            lobbySpawn
        );

        context.sendMessage(Message.raw("Created arena '" + arenaName + "' at your location!"));
        context.sendMessage(Message.raw("Arena size: 100x50x100 blocks"));
        context.sendMessage(Message.raw("Next steps:"));
        context.sendMessage(Message.raw("1. Use /sgaddspawn " + arenaName + " to add spawn points"));
        context.sendMessage(Message.raw("2. Use /sgaddchest " + arenaName + " to add chest locations"));
        context.sendMessage(Message.raw("3. Use /sgforcestart " + arenaName + " to test the arena"));
    }
}
