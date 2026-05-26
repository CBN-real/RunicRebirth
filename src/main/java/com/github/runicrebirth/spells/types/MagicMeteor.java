package com.github.runicrebirth.spells.types;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.CastResult;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.entities.spells.MagicMeteorCircleEntity;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.util.RaycastBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagicMeteor extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "magic_meteor");

    public MagicMeteor() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 160; }
    @Override public float baseDamage() { return 10f; }
    @Override public float baseSize() { return 1f; }

    @Override public String iconName() { return "meteor"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.BLUNT; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        double range = 16.0;
        Vec3 start = ctx.aimStart();
        Vec3 end = start.add(ctx.aimDirection().normalize().scale(range));
        HitResult hit = RaycastBuilder.begin(ctx.level(), ctx.caster())
            .start(start).end(end)
            .checkForBlocks(true)
            .inflate(0.3f)
            .cast();

        if (hit.getType() == HitResult.Type.MISS) {
            return CastResult.FAILED;
        }

        Vec3 targetPos;
        if (hit instanceof EntityHitResult ehr) {
            targetPos = ehr.getLocation();
        } else if (hit instanceof BlockHitResult bhr) {
            targetPos = Vec3.atCenterOf(bhr.getBlockPos().above());
        } else {
            return CastResult.FAILED;
        }

        float directDmg = params.damage;
        float splashDmg = params.damage * 0.5f;

        Vec3 dir = ctx.aimDirection();
        Vec3 right = new Vec3(-dir.z, 0, dir.x).normalize();
        Vec3 circlePos = targetPos.add(0, 10, 0).add(right.scale(5));

        Vec3 toTarget = targetPos.subtract(circlePos).normalize();
        float yRot = (float) Math.toDegrees(Math.atan2(-toTarget.x, toTarget.z));
        double hDist = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        float xRot = (float) Math.toDegrees(Math.atan2(-toTarget.y, hDist));

        MagicMeteorCircleEntity circle = new MagicMeteorCircleEntity(
            ctx.level(), ctx.caster(), params, directDmg, splashDmg, targetPos);
        circle.setPos(circlePos.x, circlePos.y, circlePos.z);
        circle.setYRot(yRot);
        circle.setXRot(xRot);
        ctx.level().addFreshEntity(circle);
        return CastResult.SUCCESS;
    }

    @Override
    public int circleLevel() {
        return 1;
    }
}
