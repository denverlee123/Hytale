package com.hytale.survivalgames.loot;

import com.hytale.api.Material;
import com.hytale.api.inventory.ItemStack;

/**
 * Represents a loot item with rarity and spawn chance
 */
public class LootItem {

    private final Material material;
    private final int minAmount;
    private final int maxAmount;
    private final double chance; // 0.0 to 1.0
    private final Rarity rarity;

    /**
     * Loot rarity tiers
     */
    public enum Rarity {
        COMMON(0.5),      // 50% base chance
        UNCOMMON(0.3),    // 30% base chance
        RARE(0.15),       // 15% base chance
        EPIC(0.04),       // 4% base chance
        LEGENDARY(0.01);  // 1% base chance

        private final double baseChance;

        Rarity(double baseChance) {
            this.baseChance = baseChance;
        }

        public double getBaseChance() {
            return baseChance;
        }
    }

    /**
     * Constructor
     */
    public LootItem(Material material, int minAmount, int maxAmount, double chance, Rarity rarity) {
        this.material = material;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.chance = chance;
        this.rarity = rarity;
    }

    /**
     * Constructor with default amount (1)
     */
    public LootItem(Material material, double chance, Rarity rarity) {
        this(material, 1, 1, chance, rarity);
    }

    /**
     * Generate an ItemStack from this loot item
     */
    public ItemStack generateItem() {
        int amount = minAmount;
        if (maxAmount > minAmount) {
            amount = minAmount + (int) (Math.random() * (maxAmount - minAmount + 1));
        }
        return new ItemStack(material, amount);
    }

    /**
     * Check if this item should spawn based on its chance
     */
    public boolean shouldSpawn() {
        return Math.random() < chance;
    }

    // Getters

    public Material getMaterial() {
        return material;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public double getChance() {
        return chance;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
