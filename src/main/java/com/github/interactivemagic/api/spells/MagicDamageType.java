package com.github.interactivemagic.api.spells;

import com.github.interactivemagic.damage.IMDamageTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public enum MagicDamageType {
    BLUNT(IMDamageTypes.BLUNT_MAGIC),
    SHARP(IMDamageTypes.SHARP_MAGIC),
    SPIRIT(IMDamageTypes.SPIRIT_MAGIC);

    private final ResourceKey<DamageType> damageTypeKey;

    MagicDamageType(ResourceKey<DamageType> damageTypeKey) {
        this.damageTypeKey = damageTypeKey;
    }

    public ResourceKey<DamageType> damageTypeKey() {
        return damageTypeKey;
    }
}
