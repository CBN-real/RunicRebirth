package com.github.interactivemagic.spells.types;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.CastResult;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.entities.spells.MagicShieldEntity;
import com.github.interactivemagic.init.ModElements;
import net.minecraft.resources.ResourceLocation;

public class MagicShield extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "magic_shield");

    public MagicShield() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 200; }
    @Override public float baseDamage() { return 0f; }
    @Override public float baseSize() { return 1f; }
    @Override public int baseDuration() { return 600; }

    @Override public String iconName() { return "shield"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.SPIRIT; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        float shieldHealth = 10f * params.size;
        int duration = params.duration;
        MagicShieldEntity shield = new MagicShieldEntity(ctx.level(), ctx.caster(), params, shieldHealth, duration);
        ctx.level().addFreshEntity(shield);
        return CastResult.SUCCESS;
    }
}
