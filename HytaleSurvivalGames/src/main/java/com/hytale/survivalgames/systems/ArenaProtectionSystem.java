package com.hytale.survivalgames.systems;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.block.events.BreakBlockEvent;
import com.hypixel.hytale.server.core.modules.block.events.PlaceBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Prevents block breaking in Survival Games arenas
 */
public class ArenaProtectionSystem {

    /**
     * Prevents block breaking in active arenas
     */
    public static class BlockBreakProtection extends EntityEventSystem<EntityStore, BreakBlockEvent.Pre> {

        private final SurvivalGamesPlugin plugin;

        public BlockBreakProtection(@Nonnull SurvivalGamesPlugin plugin) {
            super(BreakBlockEvent.Pre.class);
            this.plugin = plugin;
        }

        @Nonnull
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType());
        }

        @Override
        public void handle(@Nonnull Ref<EntityStore> ref,
                          @Nonnull BreakBlockEvent.Pre event,
                          @Nonnull ComponentAccessor<EntityStore> accessor) {

            Player player = accessor.getComponent(ref, Player.getComponentType());
            if (player == null) return;

            UUID playerId = player.getUuid();
            Arena arena = plugin.getGameManager().getPlayerArena(playerId);

            // Cancel block breaking if player is in an active arena
            if (arena != null && arena.getGameState().isRunning()) {
                event.setCancelled(true);
                player.sendMessage(Message.raw("You cannot break blocks during Survival Games!"));
            }
        }
    }

    /**
     * Prevents block placing in active arenas
     */
    public static class BlockPlaceProtection extends EntityEventSystem<EntityStore, PlaceBlockEvent.Pre> {

        private final SurvivalGamesPlugin plugin;

        public BlockPlaceProtection(@Nonnull SurvivalGamesPlugin plugin) {
            super(PlaceBlockEvent.Pre.class);
            this.plugin = plugin;
        }

        @Nonnull
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType());
        }

        @Override
        public void handle(@Nonnull Ref<EntityStore> ref,
                          @Nonnull PlaceBlockEvent.Pre event,
                          @Nonnull ComponentAccessor<EntityStore> accessor) {

            Player player = accessor.getComponent(ref, Player.getComponentType());
            if (player == null) return;

            UUID playerId = player.getUuid();
            Arena arena = plugin.getGameManager().getPlayerArena(playerId);

            // Cancel block placing if player is in an active arena
            if (arena != null && arena.getGameState().isRunning()) {
                event.setCancelled(true);
                player.sendMessage(Message.raw("You cannot place blocks during Survival Games!"));
            }
        }
    }
}
