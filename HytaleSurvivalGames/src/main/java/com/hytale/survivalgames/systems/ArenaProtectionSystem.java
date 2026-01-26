package com.hytale.survivalgames.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Prevents block breaking and placing in Survival Games arenas
 */
public class ArenaProtectionSystem {

    /**
     * Prevents block breaking in active arenas
     */
    public static class BlockBreakProtection extends EntityEventSystem<EntityStore, BreakBlockEvent> {

        private final SurvivalGamesPlugin plugin;

        public BlockBreakProtection(@Nonnull SurvivalGamesPlugin plugin) {
            super(BreakBlockEvent.class);
            this.plugin = plugin;
        }

        @Nonnull
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType());
        }

        @Override
        public void handle(int index,
                          @Nonnull ArchetypeChunk<EntityStore> chunk,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull CommandBuffer<EntityStore> commandBuffer,
                          @Nonnull BreakBlockEvent event) {

            Player player = chunk.getComponent(index, Player.getComponentType());
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
    public static class BlockPlaceProtection extends EntityEventSystem<EntityStore, PlaceBlockEvent> {

        private final SurvivalGamesPlugin plugin;

        public BlockPlaceProtection(@Nonnull SurvivalGamesPlugin plugin) {
            super(PlaceBlockEvent.class);
            this.plugin = plugin;
        }

        @Nonnull
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType());
        }

        @Override
        public void handle(int index,
                          @Nonnull ArchetypeChunk<EntityStore> chunk,
                          @Nonnull Store<EntityStore> store,
                          @Nonnull CommandBuffer<EntityStore> commandBuffer,
                          @Nonnull PlaceBlockEvent event) {

            Player player = chunk.getComponent(index, Player.getComponentType());
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
