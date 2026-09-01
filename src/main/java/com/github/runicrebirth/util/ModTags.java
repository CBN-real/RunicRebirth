package com.github.runicrebirth.util;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public final class ModTags {

    private ModTags() {}

    public static TagKey<DamageType> damageType(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, name));
    }
}
