package com.hytale.survivalgames.systems;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Detects player deaths and handles elimination in Survival Games
 */
public class PlayerDeathSystem extends DeathSystems.OnDeathSystem {

    private final SurvivalGamesPlugin plugin;

    public PlayerDeathSystem(@Nonnull SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        // Only process player deaths
        return Query.and(Player.getComponentType());
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref,
                                  @Nonnull DeathComponent component,
                                  @Nonnull Store<EntityStore> store,
                                  @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        // Get the player component
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        UUID playerId = player.getUuid();

        // Check if player is in an active arena
        Arena arena = plugin.getGameManager().getPlayerArena(playerId);
        if (arena == null) return;

        // Only process deaths during active game
        if (!arena.getGameState().isRunning()) return;

        // Handle player elimination
        plugin.getGameManager().handlePlayerElimination(playerId, arena);
    }
}
