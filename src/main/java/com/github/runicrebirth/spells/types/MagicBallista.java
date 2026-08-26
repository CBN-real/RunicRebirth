package com.github.runicrebirth.spells.types;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.CastResult;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.entities.spells.MagicBallistaCircleEntity;
import com.github.runicrebirth.init.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MagicBallista extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "magic_ballista");

    public MagicBallista() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 100; }
    @Override public float baseDamage() { return 16f; }
    @Override public float baseSize() { return 1f; }
    @Override public float spellHeight() { return 0.4375f * this.baseSize(); }
    @Override public float baseSpeed() { return 4f; }
    @Override public float baseAoeRadius() { return 4.0f; }
    @Override public float baseRange() { return 64;}

  @Override public String iconName() { return "ballista"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.SHARP; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        Vec3 dir = ctx.aimDirection().normalize();
        Vec3 circlePos = ctx.aimStart().add(dir.scale(1.0)).add(0, 1, 0);
        MagicBallistaCircleEntity circle = new MagicBallistaCircleEntity(
            ctx.level(), ctx.caster(), params, dir, ctx.xRot(), ctx.yRot(), ctx.entityTarget());
        circle.setPos(circlePos.x, circlePos.y, circlePos.z);
        circle.setYRot(ctx.yRot());
        circle.setXRot(ctx.xRot());
        ctx.level().addFreshEntity(circle);
        return CastResult.SUCCESS;
    }

    @Override
    public int circleLevel() {
        return 2;
    }
}
