package com.github.interactivemagic.spells.element;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.init.ModParticles;
import com.github.interactivemagic.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class IceElement implements Element {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "ice");

    @Override public ResourceLocation id() { return ID; }
    @Override public ParticleOptions particle() { return ParticleHelper.ICE_ELEMENT; }
    @Override public int displayColor() { return 0x99CCFF; }

    @Override
    public void applyStatusEffects(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0));
    }
}
