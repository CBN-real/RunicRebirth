package com.github.runicrebirth.items;

import com.github.runicrebirth.api.item.IMagicItem;
import net.minecraft.world.item.Item;

public abstract class MagicItem extends Item implements IMagicItem {

    public MagicItem(Properties properties) {
        super(properties);
    }
}
