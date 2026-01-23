package com.hytale.survivalgames.commands;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;

/**
 * /sgaddspawn <arena> command - Adds a spawn point to an arena
 *
 * Adds a player spawn point at the player's current location.
 * Players will be randomly teleported to one of these points when the game starts.
 */
public class AddSpawnCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;
    private final RequiredArg<String> arenaNameArg;

    public AddSpawnCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgaddspawn", "Add a spawn point to an arena");
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

        // Get arena name from command argument
        String arenaName = arenaNameArg.get(context);

        if (arenaName == null) {
            context.sendMessage(Message.raw("Usage: /sgaddspawn <arena>"));
            return;
        }

        // Get the arena
        Arena arena = plugin.getGameManager().getArena(arenaName);
        if (arena == null) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' does not exist!"));
            context.sendMessage(Message.raw("Use /sgcreate " + arenaName + " to create it first."));
            return;
        }

        // Get player's current position
        Vector3d spawnPoint = player.getPosition();
        arena.addSpawnPoint(spawnPoint);

        int spawnCount = arena.getSpawnPoints().size();
        context.sendMessage(Message.raw("Added spawn point #" + spawnCount + " to arena '" + arenaName + "'"));
        context.sendMessage(Message.raw("Location: " +
            String.format("X: %.1f, Y: %.1f, Z: %.1f", spawnPoint.x, spawnPoint.y, spawnPoint.z)));

        // Recommend minimum spawn points
        if (spawnCount < arena.getMaxPlayers()) {
            context.sendMessage(Message.raw("TIP: Add " + (arena.getMaxPlayers() - spawnCount) +
                " more spawn points for max capacity"));
        }
    }
}
