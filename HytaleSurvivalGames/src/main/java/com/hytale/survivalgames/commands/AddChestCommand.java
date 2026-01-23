package com.hytale.survivalgames.commands;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;

/**
 * /sgaddchest <arena> command - Adds a chest location to an arena
 *
 * Adds a chest loot spawn location at the player's current position.
 * When the game starts, these locations will have loot spawned in them.
 */
public class AddChestCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public AddChestCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgaddchest", "Add a chest loot location to an arena");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Verify the executor is a player
        if (!(context.getExecutor() instanceof Player)) {
            context.sendMessage(Message.raw("Only players can add chest locations!"));
            return;
        }

        Player player = (Player) context.getExecutor();

        // Get arena name from arguments
        String[] args = context.getArguments();
        if (args.length < 1) {
            context.sendMessage(Message.raw("Usage: /sgaddchest <arena-name>"));
            return;
        }

        String arenaName = args[0];

        // Get the arena
        Arena arena = plugin.getGameManager().getArena(arenaName);
        if (arena == null) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' does not exist!"));
            context.sendMessage(Message.raw("Use /sgcreate " + arenaName + " to create it first."));
            return;
        }

        // Add chest location at player's current position
        Vector3d chestLocation = player.getPosition();
        arena.addChestLocation(chestLocation);

        int chestCount = arena.getChestLocations().size();
        context.sendMessage(Message.raw("Added chest location #" + chestCount + " to arena '" + arenaName + "'"));
        context.sendMessage(Message.raw("Location: " +
            String.format("X: %.1f, Y: %.1f, Z: %.1f", chestLocation.x, chestLocation.y, chestLocation.z)));
        context.sendMessage(Message.raw("NOTE: Place a chest block at this location for it to work!"));
    }
}
