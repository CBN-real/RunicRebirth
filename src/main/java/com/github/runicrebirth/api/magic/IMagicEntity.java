package com.github.runicrebirth.api.magic;

import com.github.runicrebirth.capabilities.magic.CastingEntityAimingData;
import net.minecraft.world.entity.LivingEntity;

public interface IMagicEntity {
    LivingEntity asLivingEntity();

    CastingEntityAimingData aimingData();
}
