package com.hytale.survivalgames.loot;

import com.hytale.api.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of loot items that can be generated
 */
public class LootTable {

    private final String name;
    private final List<LootItem> items;
    private final int minItems; // Minimum items to generate
    private final int maxItems; // Maximum items to generate

    /**
     * Constructor
     */
    public LootTable(String name, int minItems, int maxItems) {
        this.name = name;
        this.items = new ArrayList<>();
        this.minItems = minItems;
        this.maxItems = maxItems;
    }

    /**
     * Add a loot item to this table
     */
    public void addItem(LootItem item) {
        items.add(item);
    }

    /**
     * Generate loot from this table
     * @return List of ItemStacks to spawn
     */
    public List<ItemStack> generateLoot() {
        List<ItemStack> loot = new ArrayList<>();

        // Determine how many items to generate
        int itemCount = minItems;
        if (maxItems > minItems) {
            itemCount = minItems + (int) (Math.random() * (maxItems - minItems + 1));
        }

        // Generate items
        List<LootItem> availableItems = new ArrayList<>(items);
        for (int i = 0; i < itemCount && !availableItems.isEmpty(); i++) {
            // Pick a random item
            int index = (int) (Math.random() * availableItems.size());
            LootItem item = availableItems.get(index);

            // Check if it should spawn
            if (item.shouldSpawn()) {
                loot.add(item.generateItem());
            }

            // Remove from available items to avoid duplicates (optional)
            // Comment out the next line if you want items to potentially appear multiple times
            availableItems.remove(index);
        }

        return loot;
    }

    /**
     * Generate loot with rarity weighting
     * Higher rarity items have lower spawn chance
     */
    public List<ItemStack> generateWeightedLoot() {
        List<ItemStack> loot = new ArrayList<>();

        // Generate common items first
        for (LootItem item : items) {
            if (item.getRarity() == LootItem.Rarity.COMMON && item.shouldSpawn()) {
                loot.add(item.generateItem());
            }
        }

        // Then uncommon
        for (LootItem item : items) {
            if (item.getRarity() == LootItem.Rarity.UNCOMMON && item.shouldSpawn()) {
                loot.add(item.generateItem());
            }
        }

        // Rare items
        for (LootItem item : items) {
            if (item.getRarity() == LootItem.Rarity.RARE && item.shouldSpawn()) {
                loot.add(item.generateItem());
            }
        }

        // Epic items
        for (LootItem item : items) {
            if (item.getRarity() == LootItem.Rarity.EPIC && item.shouldSpawn()) {
                loot.add(item.generateItem());
            }
        }

        // Legendary items
        for (LootItem item : items) {
            if (item.getRarity() == LootItem.Rarity.LEGENDARY && item.shouldSpawn()) {
                loot.add(item.generateItem());
            }
        }

        // Limit to max items
        if (loot.size() > maxItems) {
            loot = loot.subList(0, maxItems);
        }

        return loot;
    }

    // Getters

    public String getName() {
        return name;
    }

    public List<LootItem> getItems() {
        return new ArrayList<>(items);
    }

    public int getMinItems() {
        return minItems;
    }

    public int getMaxItems() {
        return maxItems;
    }
}
