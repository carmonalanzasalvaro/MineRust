package com.minerust.blockhealth;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public enum ProtectionTier {
    STRAW(50, Items.WHEAT),
    WOOD(100, Items.OAK_PLANKS),
    STONE(200, Items.COBBLESTONE),
    METAL(400, Items.IRON_INGOT),
    HQ(800, Items.DIAMOND);

    private final int maxHealth;
    private final Item materialItem;

    ProtectionTier(int maxHealth, Item materialItem) {
        this.maxHealth = maxHealth;
        this.materialItem = materialItem;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public Item getMaterialItem() {
        return materialItem;
    }

    public ProtectionTier next() {
        ProtectionTier[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static ProtectionTier fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STRAW;
        }
    }
}
