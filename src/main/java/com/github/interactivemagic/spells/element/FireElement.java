package com.github.interactivemagic.spells.element;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.init.ModParticles;
import com.github.interactivemagic.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class FireElement implements Element {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "fire");

    @Override public ResourceLocation id() { return ID; }
    @Override public ParticleOptions particle() { return ParticleHelper.FIRE_ELEMENT; }
    @Override public int displayColor() { return 0xFF6600; }

    @Override
    public void applyStatusEffects(LivingEntity target) {
        target.igniteForTicks(40);
    }
}
