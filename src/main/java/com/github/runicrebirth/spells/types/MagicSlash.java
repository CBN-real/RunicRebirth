package com.github.runicrebirth.spells.types;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.CastResult;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.entities.spells.MagicSlashCircleEntity;
import com.github.runicrebirth.init.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MagicSlash extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "magic_slash");

    public MagicSlash() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 40; }
    @Override public float baseDamage() { return 4f; }
    @Override public float baseSize() { return 1.0f; }
    @Override public float spellHeight() { return 0.125f * baseSize(); }
    @Override public float baseSpeed() { return 1.25f; }
    @Override public float baseRange() { return 20f; }


    @Override public String iconName() { return "slash"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.SHARP; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        Vec3 dir = ctx.aimDirection().normalize();
        Vec3 circlePos = ctx.aimStart().add(dir.scale(1.0));
        MagicSlashCircleEntity circle = new MagicSlashCircleEntity(
            ctx.level(), ctx.caster(), params, dir, params.speed, ctx.entityTarget());
        circle.setPos(circlePos.x, circlePos.y, circlePos.z);
        circle.setYRot(ctx.yRot());
        circle.setXRot(ctx.xRot());
        ctx.level().addFreshEntity(circle);
        return CastResult.SUCCESS;
    }

    @Override
    public int circleLevel() {
        return 1;
    }
}
