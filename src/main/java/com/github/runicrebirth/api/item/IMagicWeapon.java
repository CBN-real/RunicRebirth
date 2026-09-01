package com.github.runicrebirth.api.item;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public interface IMagicWeapon {
    Identifier getWeaponCooldownId();
    default void activate(ServerPlayer player) {}
}
