package com.hytale.survivalgames.commands;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.component.transform.TransformComponent;
import com.hypixel.hytale.server.core.entity.EntityStore;
import com.hypixel.hytale.server.core.entity.Ref;
import com.hypixel.hytale.server.core.entity.Store;
import com.hypixel.hytale.server.core.entity.player.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;

/**
 * /sgaddchest <arena> command - Adds a chest location to an arena
 *
 * Adds a chest loot spawn location at the player's current position.
 * When the game starts, these locations will have loot spawned in them.
 */
public class AddChestCommand extends AbstractPlayerCommand {

    private final SurvivalGamesPlugin plugin;
    private final RequiredArg<String> arenaNameArg;

    public AddChestCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgaddchest", "Add a chest loot location to an arena");
        this.plugin = plugin;
        this.arenaNameArg = this.withRequiredArg("arena", "Arena name", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                          @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef,
                          @Nonnull World world) {
        // Get arena name from command argument
        String arenaName = arenaNameArg.get(context);

        // Get the arena
        Arena arena = plugin.getGameManager().getArena(arenaName);
        if (arena == null) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' does not exist!"));
            context.sendMessage(Message.raw("Use /sgcreate " + arenaName + " to create it first."));
            return;
        }

        // Get player's current position via TransformComponent
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            context.sendMessage(Message.raw("Error: Could not get player position!"));
            return;
        }

        Vector3d chestLocation = transform.getPosition();
        arena.addChestLocation(chestLocation);

        int chestCount = arena.getChestLocations().size();
        context.sendMessage(Message.raw("Added chest location #" + chestCount + " to arena '" + arenaName + "'"));
        context.sendMessage(Message.raw("Location: " +
            String.format("X: %.1f, Y: %.1f, Z: %.1f", chestLocation.x, chestLocation.y, chestLocation.z)));
        context.sendMessage(Message.raw("NOTE: Place a chest block at this location for it to work!"));
    }
}
