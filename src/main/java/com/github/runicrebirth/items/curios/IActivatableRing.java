package com.github.runicrebirth.items.curios;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface IActivatableRing {
    void activate(ServerPlayer player, ItemStack stack);
}
