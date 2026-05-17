package com.github.interactivemagic.spells.element;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.init.ModParticles;
import com.github.interactivemagic.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;

public class EarthElement implements Element {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "earth");

    @Override public ResourceLocation id() { return ID; }
    @Override public ParticleOptions particle() { return ParticleHelper.EARTH_ELEMENT; }
    @Override public int displayColor() { return 0x8B6914; }

    @Override
    public float bonusDamage(MagicDamageType damageType) {
        return damageType == MagicDamageType.BLUNT ? 3f : 0f;
    }
}
