package com.hytale.survivalgames.commands;

import com.hytale.api.Player;
import com.hytale.api.command.Command;
import com.hytale.api.command.CommandSender;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import java.util.ArrayList;
import java.util.List;

/**
 * Main command handler for Survival Games
 * Handles /sg, /sgjoin, and /sgleave commands
 */
public class SGCommand implements Command {

    private final SurvivalGamesPlugin plugin;

    public SGCommand(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Handle different command labels
        if (commandLabel.equalsIgnoreCase("sgjoin")) {
            return handleJoin(sender, args);
        } else if (commandLabel.equalsIgnoreCase("sgleave")) {
            return handleLeave(sender, args);
        }

        // Main /sg command
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "join":
                return handleJoin(sender, getSubArgs(args));

            case "leave":
                return handleLeave(sender, getSubArgs(args));

            case "start":
                return handleStart(sender, getSubArgs(args));

            case "stop":
                return handleStop(sender, getSubArgs(args));

            case "list":
                return handleList(sender, getSubArgs(args));

            case "info":
                return handleInfo(sender, getSubArgs(args));

            case "reload":
                return handleReload(sender, getSubArgs(args));

            case "setspawn":
                return handleSetSpawn(sender, getSubArgs(args));

            default:
                sendHelp(sender);
                return true;
        }
    }

    /**
     * Handle join command
     */
    private boolean handleJoin(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can join games!");
            return true;
        }

        Player player = (Player) sender;

        // Check if already in a game
        if (plugin.getGameManager().getPlayerArena(player.getUniqueId()) != null) {
            player.sendMessage("§cYou are already in a game! Use /sgleave to leave.");
            return true;
        }

        // Find arena
        Arena arena;
        if (args.length > 0) {
            arena = plugin.getGameManager().getArena(args[0]);
            if (arena == null) {
                player.sendMessage("§cArena '" + args[0] + "' not found!");
                return true;
            }
        } else {
            // Find first available arena
            arena = findAvailableArena();
            if (arena == null) {
                player.sendMessage("§cNo arenas are currently available!");
                return true;
            }
        }

        // Join arena
        if (plugin.getGameManager().joinArena(player, arena)) {
            player.sendMessage("§aYou joined the " + arena.getDisplayName() + " arena!");
        } else {
            player.sendMessage("§cCould not join arena! It may be full or in progress.");
        }

        return true;
    }

    /**
     * Handle leave command
     */
    private boolean handleLeave(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can leave games!");
            return true;
        }

        Player player = (Player) sender;

        // Check if in a game
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());
        if (arena == null) {
            player.sendMessage("§cYou are not in a game!");
            return true;
        }

        plugin.getGameManager().leaveArena(player);
        player.sendMessage("§aYou left the game!");

        return true;
    }

    /**
     * Handle start command (admin only)
     */
    private boolean handleStart(CommandSender sender, String[] args) {
        if (!sender.hasPermission("survivalgames.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /sg start <arena>");
            return true;
        }

        Arena arena = plugin.getGameManager().getArena(args[0]);
        if (arena == null) {
            sender.sendMessage("§cArena '" + args[0] + "' not found!");
            return true;
        }

        // Force start would be implemented in GameManager
        sender.sendMessage("§aForce starting arena " + arena.getDisplayName() + "...");

        return true;
    }

    /**
     * Handle stop command (admin only)
     */
    private boolean handleStop(CommandSender sender, String[] args) {
        if (!sender.hasPermission("survivalgames.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /sg stop <arena>");
            return true;
        }

        Arena arena = plugin.getGameManager().getArena(args[0]);
        if (arena == null) {
            sender.sendMessage("§cArena '" + args[0] + "' not found!");
            return true;
        }

        plugin.getGameManager().stopGame(arena);
        sender.sendMessage("§aStopped arena " + arena.getDisplayName());

        return true;
    }

    /**
     * Handle list command
     */
    private boolean handleList(CommandSender sender, String[] args) {
        sender.sendMessage("§e§l=== Survival Games Arenas ===");

        for (Arena arena : plugin.getGameManager().getArenas()) {
            String status = getArenaStatus(arena);
            sender.sendMessage("§7- §e" + arena.getDisplayName() + " §8(" + arena.getName() + ") " +
                    status + " §7[" + arena.getPlayerCount() + "/" + arena.getMaxPlayers() + "]");
        }

        return true;
    }

    /**
     * Handle info command
     */
    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /sg info <arena>");
            return true;
        }

        Arena arena = plugin.getGameManager().getArena(args[0]);
        if (arena == null) {
            sender.sendMessage("§cArena '" + args[0] + "' not found!");
            return true;
        }

        sender.sendMessage("§e§l=== " + arena.getDisplayName() + " ===");
        sender.sendMessage("§7Name: §f" + arena.getName());
        sender.sendMessage("§7World: §f" + arena.getWorld().getName());
        sender.sendMessage("§7Status: " + getArenaStatus(arena));
        sender.sendMessage("§7Players: §f" + arena.getPlayerCount() + "/" + arena.getMaxPlayers());
        sender.sendMessage("§7Min Players: §f" + arena.getMinPlayers());
        sender.sendMessage("§7Game Time: §f" + arena.getGameTime() + "s");
        sender.sendMessage("§7Spawn Points: §f" + arena.getSpawnPoints().size());
        sender.sendMessage("§7Chest Locations: §f" + arena.getChestLocations().size());

        return true;
    }

    /**
     * Handle reload command (admin only)
     */
    private boolean handleReload(CommandSender sender, String[] args) {
        if (!sender.hasPermission("survivalgames.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        sender.sendMessage("§eReloading Survival Games configuration...");
        plugin.reload();
        sender.sendMessage("§aReload complete!");

        return true;
    }

    /**
     * Handle setspawn command (admin only)
     */
    private boolean handleSetSpawn(CommandSender sender, String[] args) {
        if (!sender.hasPermission("survivalgames.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can set spawn points!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /sg setspawn <arena>");
            return true;
        }

        Arena arena = plugin.getGameManager().getArena(args[0]);
        if (arena == null) {
            sender.sendMessage("§cArena '" + args[0] + "' not found!");
            return true;
        }

        // Add spawn point at player's location
        arena.addSpawnPoint(player.getLocation());
        sender.sendMessage("§aSpawn point added to arena " + arena.getDisplayName() +
                " (Total: " + arena.getSpawnPoints().size() + ")");

        return true;
    }

    /**
     * Send help message
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§e§l=== Survival Games Commands ===");
        sender.sendMessage("§7/sg join [arena] §f- Join a game");
        sender.sendMessage("§7/sg leave §f- Leave current game");
        sender.sendMessage("§7/sg list §f- List all arenas");
        sender.sendMessage("§7/sg info <arena> §f- View arena info");

        if (sender.hasPermission("survivalgames.admin")) {
            sender.sendMessage("§c§lAdmin Commands:");
            sender.sendMessage("§7/sg start <arena> §f- Force start a game");
            sender.sendMessage("§7/sg stop <arena> §f- Stop a game");
            sender.sendMessage("§7/sg reload §f- Reload configuration");
            sender.sendMessage("§7/sg setspawn <arena> §f- Add spawn point");
        }
    }

    /**
     * Get sub-arguments (remove first arg)
     */
    private String[] getSubArgs(String[] args) {
        if (args.length <= 1) {
            return new String[0];
        }
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
        return subArgs;
    }

    /**
     * Find first available arena
     */
    private Arena findAvailableArena() {
        for (Arena arena : plugin.getGameManager().getArenas()) {
            if (arena.getGameState().canJoin() && arena.getPlayerCount() < arena.getMaxPlayers()) {
                return arena;
            }
        }
        return null;
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

    @Override
    public List<String> tabComplete(CommandSender sender, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument - subcommands
            completions.add("join");
            completions.add("leave");
            completions.add("list");
            completions.add("info");

            if (sender.hasPermission("survivalgames.admin")) {
                completions.add("start");
                completions.add("stop");
                completions.add("reload");
                completions.add("setspawn");
            }
        } else if (args.length == 2) {
            // Second argument - arena names for certain commands
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("join") || subCommand.equals("start") ||
                subCommand.equals("stop") || subCommand.equals("info") ||
                subCommand.equals("setspawn")) {

                for (Arena arena : plugin.getGameManager().getArenas()) {
                    completions.add(arena.getName());
                }
            }
        }

        return completions;
    }
}
