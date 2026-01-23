package com.hytale.survivalgames.commands;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * /sgaddchest <arena> command - Adds a chest location to an arena
 *
 * Adds a chest loot spawn location at the player's current position.
 * When the game starts, these locations will have loot spawned in them.
 */
public class AddChestCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;
    private final RequiredArg<String> arenaNameArg;

    public AddChestCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgaddchest", "Add a chest loot location to an arena");
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
            context.sendMessage(Message.raw("Only players can add chest locations!"));
            return;
        }

        // Get arena name from command argument
        String arenaName = arenaNameArg.get(context);

        if (arenaName == null) {
            context.sendMessage(Message.raw("Usage: /sgaddchest <arena>"));
            return;
        }

        // Get the arena
        Arena arena = plugin.getGameManager().getArena(arenaName);
        if (arena == null) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' does not exist!"));
            context.sendMessage(Message.raw("Use /sgcreate " + arenaName + " to create it first."));
            return;
        }

        // Get player's position using PlayerRef
        UUID playerUUID = player.getUuid();
        PlayerRef playerRef = Universe.get().getPlayer(playerUUID);
        Transform playerTransform = playerRef.getTransform();
        Vector3d chestLocation = playerTransform.getPosition();

        arena.addChestLocation(chestLocation);

        int chestCount = arena.getChestLocations().size();
        context.sendMessage(Message.raw("Added chest location #" + chestCount + " to arena '" + arenaName + "'"));
        context.sendMessage(Message.raw("Location: " +
            String.format("X: %.1f, Y: %.1f, Z: %.1f", chestLocation.x, chestLocation.y, chestLocation.z)));
        context.sendMessage(Message.raw("NOTE: Place a chest block at this location for it to work!"));
    }
}
