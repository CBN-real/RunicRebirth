package com.github.runicrebirth.spells.types;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.CastResult;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.entities.spells.InfusionCircleEntity;
import com.github.runicrebirth.init.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Infusion extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "infusion");

    public Infusion() {
        super(ID);
    }

    @Override
    public int cooldownTicks() { return 60; }

    @Override
    public float baseDamage() { return 0f; }

    @Override
    public float baseSize() { return 1.0f; }

    @Override
    public float spellHeight() { return 0.1f; }

    @Override
    public Element defaultElement() { return ModElements.ARCANE.get(); }

    @Override
    public MagicDamageType damageCategory() { return MagicDamageType.SPIRIT; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        Vec3 start = ctx.aimStart();
        Vec3 end = start.add(ctx.aimDirection().normalize().scale(16.0));
        BlockHitResult hit = ctx.level().clip(new ClipContext(start, end,
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, ctx.caster()));

        Vec3 spawnPos;
        if (hit.getType() == HitResult.Type.BLOCK) {
            spawnPos = Vec3.atBottomCenterOf(hit.getBlockPos().above());
        } else {
            spawnPos = ctx.caster().position();
        }

        InfusionCircleEntity circle = new InfusionCircleEntity(ctx.level(), ctx.caster(), params);
        circle.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        ctx.level().addFreshEntity(circle);
        return CastResult.SUCCESS;
    }
}
