package com.github.runicrebirth.spells.element;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;

public class ArcaneElement implements Element {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "arcane");

    /** Extra charges added to any spell cast with this element. */
    public int extraCharges = 2;

    @Override public Identifier id() { return ID; }
    @Override public ParticleOptions particle(float scale) { return new ScaledParticleOption(ModParticles.ARCANE_ELEMENT.get(), scale); }
    @Override public ParticleOptions tinyParticle(float scale) { return new ScaledParticleOption(ModParticles.ARCANE_TINY.get(), scale); }
    @Override public ParticleOptions inkParticle(float scale) { return new ScaledParticleOption(ModParticles.ARCANE_INK.get(), scale); }
    @Override public int displayColor() { return 0xAE78FF; }

    @Override
    public float bonusDamage(MagicDamageType damageType) {
        return damageType == MagicDamageType.SPIRIT ? 3f : 0f;
    }

    @Override
    public void modifyParams(SpellParams params) {
        params.useCharges = true;
        params.chargesBonus += extraCharges;
    }
}
