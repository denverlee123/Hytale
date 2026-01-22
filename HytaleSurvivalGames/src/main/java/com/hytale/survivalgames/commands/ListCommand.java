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
        context.sendMessage(Message.raw("§e§l=== Survival Games Arenas ==="));

        if (plugin.getGameManager().getArenaCount() == 0) {
            context.sendMessage(Message.raw("§7No arenas configured yet."));
            context.sendMessage(Message.raw("§7Ask an admin to create an arena."));
            return;
        }

        for (Arena arena : plugin.getGameManager().getArenas()) {
            String status = getArenaStatus(arena);
            context.sendMessage(Message.raw(
                "§7- §e" + arena.getDisplayName() +
                " §8(" + arena.getName() + ") " +
                status +
                " §7[" + arena.getPlayerCount() + "/" + arena.getMaxPlayers() + "]"
            ));
        }
    }

    /**
     * Get colored status string for arena
     */
    private String getArenaStatus(Arena arena) {
        switch (arena.getGameState()) {
            case WAITING:
                return "§aWaiting";
            case STARTING:
                return "§eStarting";
            case IN_GAME:
                return "§6In Progress";
            case DEATHMATCH:
                return "§cDeathmatch";
            case ENDING:
                return "§7Ending";
            case DISABLED:
                return "§8Disabled";
            default:
                return "§7Unknown";
        }
    }
}
