package com.github.runicrebirth.api.spells;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public record SpellCastContext(
    ServerLevel level,
    LivingEntity caster,
    ItemStack item,
    Vec3 aimStart,
    Vec3 aimDirection,
    float xRot,
    float yRot,
    LivingEntity entityTarget
) {}
