package com.github.runicrebirth.util;

import net.minecraft.world.item.Item;

public final class ItemPropertiesHelper {

    private ItemPropertiesHelper() {}

    public static Item.Properties equipment() {
        return new Item.Properties().stacksTo(1);
    }

    public static Item.Properties material() {
        return new Item.Properties();
    }

    public static Item.Properties wand() {
        return new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.UNCOMMON);
    }
}
