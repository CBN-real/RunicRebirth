package com.github.interactivemagic.util;

import com.github.interactivemagic.InteractiveMagic;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public final class ModTags {

    private ModTags() {}

    public static TagKey<DamageType> damageType(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, name));
    }
}
