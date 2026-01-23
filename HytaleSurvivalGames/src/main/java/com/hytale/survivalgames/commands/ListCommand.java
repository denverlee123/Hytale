package com.hytale.survivalgames.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;

/**
 * /sglist command
 *
 * Lists all available Survival Games arenas
 */
public class ListCommand extends CommandBase {

    private final SurvivalGamesPlugin plugin;

    public ListCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sglist", "List all Survival Games arenas");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("=== Survival Games Arenas ==="));

        if (plugin.getGameManager().getArenaCount() == 0) {
            context.sendMessage(Message.raw("No arenas configured yet."));
            context.sendMessage(Message.raw("Ask an admin to create an arena."));
            return;
        }

        for (Arena arena : plugin.getGameManager().getArenas()) {
            String status = getArenaStatus(arena);
            context.sendMessage(Message.raw(
                "- " + arena.getDisplayName() +
                " (" + arena.getName() + ") " +
                status +
                " [" + arena.getPlayerCount() + "/" + arena.getMaxPlayers() + "]"
            ));
        }
    }

    /**
     * Get colored status string for arena
     */
    private String getArenaStatus(Arena arena) {
        switch (arena.getGameState()) {
            case WAITING:
                return "Waiting";
            case STARTING:
                return "Starting";
            case IN_GAME:
                return "In Progress";
            case DEATHMATCH:
                return "Deathmatch";
            case ENDING:
                return "Ending";
            case DISABLED:
                return "Disabled";
            default:
                return "Unknown";
        }
    }
}
