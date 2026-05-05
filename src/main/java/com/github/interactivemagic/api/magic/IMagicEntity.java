package com.github.interactivemagic.api.magic;

import com.github.interactivemagic.capabilities.magic.CastingEntityAimingData;
import net.minecraft.world.entity.LivingEntity;

public interface IMagicEntity {
    LivingEntity asLivingEntity();

    CastingEntityAimingData aimingData();
}
