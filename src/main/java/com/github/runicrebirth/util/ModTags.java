package com.github.runicrebirth.util;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public final class ModTags {

    private ModTags() {}

    public static TagKey<DamageType> damageType(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, name));
    }
}
