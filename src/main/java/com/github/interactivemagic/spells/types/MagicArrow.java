package com.github.interactivemagic.spells.types;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.CastResult;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.entities.spells.MagicArrowEntity;
import com.github.interactivemagic.init.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MagicArrow extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "magic_arrow");

    public MagicArrow() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 60; }
    @Override public float baseDamage() { return 6f; }
    @Override public float baseSize() { return 1f; }
    @Override public float baseSpeed() { return 1.5f; }

    @Override public String iconName() { return "arrow"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.SHARP; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        Vec3 dir = ctx.aimDirection().normalize();
        MagicArrowEntity arrow = new MagicArrowEntity(ctx.level(), ctx.caster(), params);
        Vec3 spawn = ctx.aimStart().add(dir.scale(0.5));
        arrow.setPos(spawn.x, spawn.y, spawn.z);
        arrow.shoot(dir.x, dir.y, dir.z, params.speed, 0.0F);
        ctx.level().addFreshEntity(arrow);
        return CastResult.SUCCESS;
    }
}
