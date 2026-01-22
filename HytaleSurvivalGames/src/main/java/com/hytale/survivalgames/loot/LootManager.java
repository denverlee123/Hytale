package com.hytale.survivalgames.loot;

import com.hytale.api.Location;
import com.hytale.api.Material;
import com.hytale.api.block.Block;
import com.hytale.api.block.Chest;
import com.hytale.api.inventory.Inventory;
import com.hytale.api.inventory.ItemStack;
import com.hytale.survivalgames.SurvivalGamesPlugin;
import com.hytale.survivalgames.game.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages loot tables and chest spawning for arenas
 */
public class LootManager {

    private final SurvivalGamesPlugin plugin;
    private final Map<String, LootTable> lootTables;

    public LootManager(SurvivalGamesPlugin plugin) {
        this.plugin = plugin;
        this.lootTables = new HashMap<>();

        // Initialize default loot tables
        initializeDefaultLootTables();
    }

    /**
     * Initialize default loot tables
     */
    private void initializeDefaultLootTables() {
        // Basic loot table
        LootTable basicTable = new LootTable("basic", 3, 7);

        // Weapons - Common
        basicTable.addItem(new LootItem(Material.WOODEN_SWORD, 0.6, LootItem.Rarity.COMMON));
        basicTable.addItem(new LootItem(Material.STONE_SWORD, 0.4, LootItem.Rarity.UNCOMMON));
        basicTable.addItem(new LootItem(Material.IRON_SWORD, 0.2, LootItem.Rarity.RARE));
        basicTable.addItem(new LootItem(Material.DIAMOND_SWORD, 0.05, LootItem.Rarity.LEGENDARY));

        // Armor - Common
        basicTable.addItem(new LootItem(Material.LEATHER_HELMET, 0.5, LootItem.Rarity.COMMON));
        basicTable.addItem(new LootItem(Material.LEATHER_CHESTPLATE, 0.5, LootItem.Rarity.COMMON));
        basicTable.addItem(new LootItem(Material.LEATHER_LEGGINGS, 0.5, LootItem.Rarity.COMMON));
        basicTable.addItem(new LootItem(Material.LEATHER_BOOTS, 0.5, LootItem.Rarity.COMMON));

        // Armor - Uncommon
        basicTable.addItem(new LootItem(Material.CHAINMAIL_HELMET, 0.3, LootItem.Rarity.UNCOMMON));
        basicTable.addItem(new LootItem(Material.CHAINMAIL_CHESTPLATE, 0.3, LootItem.Rarity.UNCOMMON));
        basicTable.addItem(new LootItem(Material.IRON_HELMET, 0.25, LootItem.Rarity.UNCOMMON));
        basicTable.addItem(new LootItem(Material.IRON_CHESTPLATE, 0.25, LootItem.Rarity.UNCOMMON));

        // Armor - Rare/Epic
        basicTable.addItem(new LootItem(Material.DIAMOND_HELMET, 0.1, LootItem.Rarity.RARE));
        basicTable.addItem(new LootItem(Material.DIAMOND_CHESTPLATE, 0.08, LootItem.Rarity.EPIC));

        // Food
        basicTable.addItem(new LootItem(Material.BREAD, 3, 6, 0.7, LootItem.Rarity.COMMON));
        basicTable.addItem(new LootItem(Material.COOKED_BEEF, 2, 5, 0.5, LootItem.Rarity.COMMON));
        basicTable.addItem(new LootItem(Material.GOLDEN_APPLE, 1, 2, 0.2, LootItem.Rarity.RARE));

        // Projectiles
        basicTable.addItem(new LootItem(Material.BOW, 0.4, LootItem.Rarity.UNCOMMON));
        basicTable.addItem(new LootItem(Material.ARROW, 8, 24, 0.6, LootItem.Rarity.COMMON));

        // Consumables
        basicTable.addItem(new LootItem(Material.POTION, 0.3, LootItem.Rarity.UNCOMMON)); // Healing potion
        basicTable.addItem(new LootItem(Material.ENDER_PEARL, 1, 2, 0.15, LootItem.Rarity.RARE));

        // Tools
        basicTable.addItem(new LootItem(Material.FISHING_ROD, 0.25, LootItem.Rarity.UNCOMMON));
        basicTable.addItem(new LootItem(Material.FLINT_AND_STEEL, 0.2, LootItem.Rarity.UNCOMMON));

        // Building blocks
        basicTable.addItem(new LootItem(Material.OAK_PLANKS, 8, 16, 0.4, LootItem.Rarity.COMMON));
        basicTable.addItem(new LootItem(Material.COBBLESTONE, 16, 32, 0.3, LootItem.Rarity.COMMON));

        lootTables.put("basic", basicTable);

        // Tier 2 loot table (better items)
        LootTable tier2Table = new LootTable("tier2", 4, 8);

        // Better weapons
        tier2Table.addItem(new LootItem(Material.IRON_SWORD, 0.5, LootItem.Rarity.UNCOMMON));
        tier2Table.addItem(new LootItem(Material.DIAMOND_SWORD, 0.15, LootItem.Rarity.RARE));

        // Better armor
        tier2Table.addItem(new LootItem(Material.IRON_HELMET, 0.5, LootItem.Rarity.UNCOMMON));
        tier2Table.addItem(new LootItem(Material.IRON_CHESTPLATE, 0.5, LootItem.Rarity.UNCOMMON));
        tier2Table.addItem(new LootItem(Material.IRON_LEGGINGS, 0.5, LootItem.Rarity.UNCOMMON));
        tier2Table.addItem(new LootItem(Material.IRON_BOOTS, 0.5, LootItem.Rarity.UNCOMMON));
        tier2Table.addItem(new LootItem(Material.DIAMOND_CHESTPLATE, 0.2, LootItem.Rarity.EPIC));

        // Better consumables
        tier2Table.addItem(new LootItem(Material.GOLDEN_APPLE, 2, 4, 0.4, LootItem.Rarity.UNCOMMON));
        tier2Table.addItem(new LootItem(Material.ENCHANTED_GOLDEN_APPLE, 0.05, LootItem.Rarity.LEGENDARY));

        // Projectiles
        tier2Table.addItem(new LootItem(Material.BOW, 0.6, LootItem.Rarity.UNCOMMON));
        tier2Table.addItem(new LootItem(Material.ARROW, 16, 48, 0.8, LootItem.Rarity.COMMON));

        lootTables.put("tier2", tier2Table);

        plugin.getLogger().info("Initialized " + lootTables.size() + " loot tables");
    }

    /**
     * Get a loot table by name
     */
    public LootTable getLootTable(String name) {
        return lootTables.get(name);
    }

    /**
     * Add a custom loot table
     */
    public void addLootTable(String name, LootTable table) {
        lootTables.put(name, table);
    }

    /**
     * Populate all chests in an arena with loot
     */
    public void populateChests(Arena arena) {
        List<Location> chestLocations = arena.getChestLocations();

        plugin.getLogger().info("Populating " + chestLocations.size() + " chests for arena " + arena.getName());

        for (Location location : chestLocations) {
            populateChest(location);
        }
    }

    /**
     * Populate a single chest with loot
     */
    private void populateChest(Location location) {
        Block block = location.getBlock();

        // Ensure block is a chest
        if (!(block instanceof Chest)) {
            // Place a chest if there isn't one
            block.setType(Material.CHEST);
        }

        Chest chest = (Chest) block;
        Inventory inventory = chest.getInventory();

        // Clear existing contents
        inventory.clear();

        // Randomly choose a loot table (70% basic, 30% tier2)
        LootTable table = Math.random() < 0.7 ? getLootTable("basic") : getLootTable("tier2");

        if (table == null) {
            plugin.getLogger().warning("Loot table not found!");
            return;
        }

        // Generate loot
        List<ItemStack> loot = table.generateWeightedLoot();

        // Place items in random slots
        List<Integer> availableSlots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            availableSlots.add(i);
        }

        for (ItemStack item : loot) {
            if (availableSlots.isEmpty()) {
                break;
            }

            // Pick random slot
            int slotIndex = (int) (Math.random() * availableSlots.size());
            int slot = availableSlots.remove(slotIndex);

            inventory.setItem(slot, item);
        }
    }

    /**
     * Reload loot tables from configuration
     */
    public void reload() {
        lootTables.clear();
        initializeDefaultLootTables();
        plugin.getLogger().info("Loot tables reloaded");
    }

    /**
     * Get all loot tables
     */
    public Map<String, LootTable> getAllLootTables() {
        return new HashMap<>(lootTables);
    }
}
