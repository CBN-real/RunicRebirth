package com.github.runicrebirth.spells.types;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.CastResult;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.entities.spells.MagicHammerEntity;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.util.RaycastBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagicHammer extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "magic_hammer");

    public MagicHammer() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 100; }
    @Override public float baseDamage() { return 12f; }
    @Override public float baseSize() { return 1f; }
    @Override public float baseAoeRadius() { return 2.5f; }
    @Override public int castingDelayTicks() { return 20; }
    @Override public int multiCastDelay() { return 10; }
    @Override public float baseRange() {return 24f;}

  @Override public String iconName() { return "hammer"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.BLUNT; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        Vec3 start = ctx.aimStart();
        Vec3 end = start.add(ctx.aimDirection().normalize().scale(baseRange()));
        HitResult hit = RaycastBuilder.begin(ctx.level(), ctx.caster())
            .start(start).end(end)
            .checkForBlocks(true)
            .inflate(0.3f)
            .cast();

        if (hit.getType() == HitResult.Type.MISS) {
            return CastResult.FAILED;
        }

        float directDmg = params.damage;
        float splashDmg = params.damage * 0.5f;

        MagicHammerEntity hammer;
        Vec3 spawnPos;
        if (hit instanceof EntityHitResult ehr) {
            Entity target = ehr.getEntity();
            hammer = new MagicHammerEntity(ctx.level(), ctx.caster(), params, directDmg, splashDmg, target);
            spawnPos = target.position().add(0, 1, 0);
        } else if (hit instanceof BlockHitResult bhr) {
            Vec3 targetPos = Vec3.atCenterOf(bhr.getBlockPos().above());
            hammer = new MagicHammerEntity(ctx.level(), ctx.caster(), params, directDmg, splashDmg, targetPos);
            spawnPos = targetPos.add(0, 1, 0);
        } else {
            return CastResult.FAILED;
        }

        hammer.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        ctx.level().addFreshEntity(hammer);
        return CastResult.SUCCESS;
    }

    @Override
    public int circleLevel() {
        return 2;
    }
}
