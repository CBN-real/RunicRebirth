package com.github.interactivemagic.spells.types;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.CastResult;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.entities.spells.MagicMeteorEntity;
import com.github.interactivemagic.init.ModElements;
import com.github.interactivemagic.util.RaycastBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagicMeteor extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "magic_meteor");

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
        MagicMeteorEntity meteor = new MagicMeteorEntity(ctx.level(), ctx.caster(), params, directDmg, splashDmg);
        Vec3 spawnPos = targetPos.add(0, 10, 0);
        meteor.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        meteor.setDeltaMovement(0, -0.5, 0);
        ctx.level().addFreshEntity(meteor);
        return CastResult.SUCCESS;
    }
}
