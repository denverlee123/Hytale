package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;
import com.hytale.survivalgames.game.GameState;

import javax.annotation.Nonnull;

/**
 * /sgforcestart <arena> command - Force starts a game in an arena
 *
 * Starts a game immediately without waiting for minimum players or countdown.
 * Useful for testing arenas or starting games manually.
 */
public class ForceStartCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;
    private final RequiredArg<String> arenaNameArg;

    public ForceStartCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgforcestart", "Force start a Survival Games match");
        this.plugin = plugin;
        this.arenaNameArg = this.withRequiredArg("arena", "Arena name", ArgTypes.STRING);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Get arena name from command argument
        String arenaName = arenaNameArg.get(context);

        // Get the arena
        Arena arena = plugin.getGameManager().getArena(arenaName);
        if (arena == null) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' does not exist!"));
            return;
        }

        // Check if arena is already running
        if (arena.getGameState() == GameState.IN_GAME) {
            context.sendMessage(Message.raw("Arena '" + arenaName + "' is already running!"));
            return;
        }

        // Check if there are any players
        if (arena.getPlayerCount() == 0) {
            context.sendMessage(Message.raw("Cannot start arena '" + arenaName + "' - no players!"));
            context.sendMessage(Message.raw("Use /sgjoin " + arenaName + " to join first."));
            return;
        }

        // Force start the game
        plugin.getGameManager().forceStart(arena);

        context.sendMessage(Message.raw("Force started game in arena '" + arenaName + "'"));
        context.sendMessage(Message.raw("Players: " + arena.getPlayerCount()));
    }
}
