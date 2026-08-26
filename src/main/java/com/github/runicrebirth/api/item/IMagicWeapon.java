package com.github.runicrebirth.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface IMagicWeapon {
    ResourceLocation getWeaponCooldownId();
    default void activate(ServerPlayer player) {}
}
