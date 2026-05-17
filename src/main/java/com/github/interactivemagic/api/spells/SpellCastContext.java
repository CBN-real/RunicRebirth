package com.github.interactivemagic.api.spells;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public record SpellCastContext(
    ServerLevel level,
    ServerPlayer caster,
    ItemStack item,
    Vec3 aimStart,
    Vec3 aimDirection,
    float xRot,
    float yRot
) {}
