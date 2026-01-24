package com.hytale.survivalgames.commands.admin;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;

/**
 * /sgarenainfo <arena> - Show detailed information about an arena
 */
public class ArenaInfoCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;
    private final RequiredArg<String> arenaNameArg;

    public ArenaInfoCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgarenainfo", "Show detailed information about an arena");
        this.plugin = plugin;
        this.arenaNameArg = this.withRequiredArg("arena", "Arena name", ArgTypes.STRING);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Get arena name
        String arenaName = arenaNameArg.get(context);
        Arena arena = plugin.getGameManager().getArena(arenaName);

        if (arena == null) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' not found!"));
            context.sendMessage(Message.raw("Use /sglist to see all arenas."));
            return;
        }

        // Display arena information
        context.sendMessage(Message.raw("=== Arena: " + arena.getDisplayName() + " ==="));
        context.sendMessage(Message.raw("ID: " + arena.getName()));
        context.sendMessage(Message.raw("State: " + arena.getGameState()));
        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("Players: " + arena.getPlayerCount() + "/" + arena.getMaxPlayers()));
        context.sendMessage(Message.raw("Min Players: " + arena.getMinPlayers()));
        context.sendMessage(Message.raw("Spectators: " + arena.getSpectators().size()));
        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("Spawn Points: " + arena.getSpawnPoints().size()));
        context.sendMessage(Message.raw("Chest Locations: " + arena.getChestLocations().size()));
        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("Game Time: " + arena.getGameTime() + "s"));
        context.sendMessage(Message.raw("Deathmatch Time: " + arena.getDeathmatchTime() + "s"));
        context.sendMessage(Message.raw("Border Radius: " + arena.getBorderRadius()));
    }
}
