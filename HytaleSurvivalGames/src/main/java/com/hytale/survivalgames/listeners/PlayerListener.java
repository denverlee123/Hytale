package com.hytale.survivalgames.listeners;

import com.hytale.api.Player;
import com.hytale.api.event.EventHandler;
import com.hytale.api.event.Listener;
import com.hytale.api.event.player.*;
import com.hytale.api.event.entity.EntityDamageByEntityEvent;
import com.hytale.api.event.entity.PlayerDeathEvent;
import com.hytale.api.event.inventory.InventoryClickEvent;
import com.hytale.api.event.block.BlockBreakEvent;
import com.hytale.api.event.block.BlockPlaceEvent;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;
import com.hytale.survivalgames.game.GameState;
import com.hytale.survivalgames.player.SGPlayer;

/**
 * Handles all player-related events during Survival Games matches
 */
public class PlayerListener implements Listener {

    private final SurvivalGamesPlugin plugin;

    public PlayerListener(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle player death in arena
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena == null) {
            return; // Not in an arena
        }

        // Don't drop items in arena
        event.setKeepInventory(true);
        event.getDrops().clear();

        // Custom death message
        event.setDeathMessage(null);

        // Handle elimination
        plugin.getGameManager().handlePlayerDeath(player, arena);

        // Track killer if PvP death
        Player killer = player.getKiller();
        if (killer != null && arena.getPlayers().contains(killer.getUniqueId())) {
            SGPlayer sgKiller = plugin.getPlayerManager().getSGPlayer(killer.getUniqueId());
            if (sgKiller != null) {
                sgKiller.addKill();
                killer.sendMessage("§a+1 Kill! §7(Total: " + sgKiller.getKills() + ")");
            }
        }
    }

    /**
     * Handle player respawn
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena == null) {
            return;
        }

        // Spectators respawn at lobby
        if (arena.getSpectators().contains(player.getUniqueId())) {
            event.setRespawnLocation(arena.getLobbySpawn());
        }
    }

    /**
     * Handle player quit
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena != null) {
            // Remove player from arena
            plugin.getGameManager().leaveArena(player);
        }

        // Clean up player data
        plugin.getPlayerManager().removeSGPlayer(player.getUniqueId());
    }

    /**
     * Handle player join
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Clean up any leftover data
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());
        if (arena != null) {
            arena.removePlayer(player.getUniqueId());
        }
    }

    /**
     * Prevent PvP damage during countdown
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        Arena arena = plugin.getGameManager().getPlayerArena(victim.getUniqueId());

        if (arena == null) {
            return;
        }

        // Prevent damage during waiting/starting states
        if (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING) {
            event.setCancelled(true);
            attacker.sendMessage("§cYou cannot attack players yet!");
        }

        // Prevent spectator damage
        if (arena.getSpectators().contains(victim.getUniqueId()) ||
            arena.getSpectators().contains(attacker.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent item drops in lobby
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena == null) {
            return;
        }

        // Prevent item drops during waiting/starting
        if (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING) {
            event.setCancelled(true);
        }

        // Prevent spectator item drops
        if (arena.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent item pickups during countdown
     */
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena == null) {
            return;
        }

        // Prevent item pickup during waiting/starting
        if (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING) {
            event.setCancelled(true);
        }

        // Prevent spectator item pickup
        if (arena.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent inventory clicks during countdown
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena == null) {
            return;
        }

        // Prevent inventory changes during waiting/starting
        if (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle block breaking in arena
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena == null) {
            return;
        }

        // Only allow block breaking during active game
        if (!arena.getGameState().isActive()) {
            event.setCancelled(true);
            return;
        }

        // Prevent spectators from breaking blocks
        if (arena.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }

        // Check if block is within arena boundaries
        if (!arena.isInArena(event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot break blocks outside the arena!");
        }
    }

    /**
     * Handle block placing in arena
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena == null) {
            return;
        }

        // Only allow block placing during active game
        if (!arena.getGameState().isActive()) {
            event.setCancelled(true);
            return;
        }

        // Prevent spectators from placing blocks
        if (arena.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }

        // Check if block is within arena boundaries
        if (!arena.isInArena(event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot place blocks outside the arena!");
        }
    }

    /**
     * Handle player movement (prevent leaving arena during game)
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena == null) {
            return;
        }

        // During countdown, prevent movement
        if (arena.getGameState() == GameState.STARTING) {
            // Check if player moved more than just looking around
            if (event.getFrom().getX() != event.getTo().getX() ||
                event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
            }
        }

        // During active game, prevent leaving arena
        if (arena.getGameState().isActive()) {
            if (!arena.isInArena(event.getTo())) {
                // Teleport back or damage player
                player.sendMessage("§cYou cannot leave the arena during the game!");
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handle food level change (prevent hunger in lobby)
     */
    @EventHandler
    public void onFoodLevelChange(PlayerFoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena == null) {
            return;
        }

        // Prevent hunger loss during waiting/starting
        if (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING) {
            event.setCancelled(true);
        }

        // Prevent spectator hunger
        if (arena.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle command execution (prevent certain commands in game)
     */
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getGameManager().getPlayerArena(player.getUniqueId());

        if (arena == null) {
            return;
        }

        String command = event.getMessage().toLowerCase();

        // Block certain commands during active games
        if (arena.getGameState().isActive()) {
            // Allow sg/sgleave commands
            if (command.startsWith("/sg") || command.startsWith("/sgleave")) {
                return;
            }

            // Block teleport commands
            if (command.startsWith("/tp") || command.startsWith("/teleport") ||
                command.startsWith("/home") || command.startsWith("/spawn") ||
                command.startsWith("/warp")) {

                event.setCancelled(true);
                player.sendMessage("§cYou cannot use that command during a game!");
            }
        }
    }
}
