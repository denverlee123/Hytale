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
 * /sgforcestart <arena> - Force start a game immediately
 */
public class ForceStartCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;
    private final RequiredArg<String> arenaNameArg;

    public ForceStartCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgforcestart", "Force start a Survival Games match immediately");
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

        // Check if arena can be started
        if (!arena.getGameState().canStart()) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' cannot be started!"));
            context.sendMessage(Message.raw("Current state: " + arena.getGameState()));
            return;
        }

        if (arena.getPlayerCount() < 1) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' has no players!"));
            return;
        }

        if (arena.getSpawnPoints().isEmpty()) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' has no spawn points!"));
            context.sendMessage(Message.raw("Add spawn points with: /sgaddspawn " + arenaName));
            return;
        }

        // Force start the game
        plugin.getGameManager().forceStartGame(arena);

        context.sendMessage(Message.raw("Force starting game in arena '" + arenaName + "'!"));
        context.sendMessage(Message.raw("Players: " + arena.getPlayerCount()));
        context.sendMessage(Message.raw("Spawn points: " + arena.getSpawnPoints().size()));
    }
}
