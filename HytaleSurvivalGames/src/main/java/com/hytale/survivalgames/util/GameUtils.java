package com.hytale.survivalgames.util;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.items.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.stat.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entity.stat.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Utility methods for Survival Games gameplay
 */
public class GameUtils {

    /**
     * Reset a player's health to maximum
     */
    public static void healPlayer(@Nonnull Player player, @Nonnull World world) {
        if (player.getReference() == null) return;

        world.execute(() -> {
            Ref<EntityStore> playerRef = player.getReference();
            Store<EntityStore> store = playerRef.getStore();

            EntityStatMap statMap = store.getComponent(playerRef, EntityStatMap.getComponentType());
            if (statMap != null) {
                statMap.maximizeStatValue(DefaultEntityStatTypes.getHealth());
            }
        });
    }

    /**
     * Send a notification to a player
     */
    public static void sendNotification(@Nonnull UUID playerId,
                                        @Nonnull String primaryText,
                                        @Nonnull String secondaryText,
                                        @Nonnull String iconItemId) {
        PlayerRef playerRef = Universe.get().getPlayer(playerId);
        if (playerRef == null) return;

        Message primaryMessage = Message.raw(primaryText);
        Message secondaryMessage = Message.raw(secondaryText);
        ItemStack icon = new ItemStack(iconItemId, 1);

        NotificationUtil.sendNotification(
            playerRef.getPacketHandler(),
            primaryMessage,
            secondaryMessage,
            (ItemWithAllMetadata) icon.toPacket()
        );
    }

    /**
     * Send a notification to a player with custom colors
     */
    public static void sendNotification(@Nonnull UUID playerId,
                                        @Nonnull Message primaryMessage,
                                        @Nonnull Message secondaryMessage,
                                        @Nonnull String iconItemId) {
        PlayerRef playerRef = Universe.get().getPlayer(playerId);
        if (playerRef == null) return;

        ItemStack icon = new ItemStack(iconItemId, 1);

        NotificationUtil.sendNotification(
            playerRef.getPacketHandler(),
            primaryMessage,
            secondaryMessage,
            (ItemWithAllMetadata) icon.toPacket()
        );
    }

    /**
     * Broadcast a notification to all players in an arena
     */
    public static void broadcastNotification(@Nonnull Iterable<UUID> players,
                                             @Nonnull String primaryText,
                                             @Nonnull String secondaryText,
                                             @Nonnull String iconItemId) {
        for (UUID playerId : players) {
            sendNotification(playerId, primaryText, secondaryText, iconItemId);
        }
    }
}
