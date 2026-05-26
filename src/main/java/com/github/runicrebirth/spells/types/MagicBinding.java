package com.github.runicrebirth.spells.types;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.CastResult;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.entities.spells.MagicBindingEntity;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.util.RaycastBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagicBinding extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "magic_binding");

    public MagicBinding() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 160; }
    @Override public float baseDamage() { return 6f; }
    @Override public float baseSize() { return 1f; }

    @Override public String iconName() { return "binding"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.SPIRIT; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        double range = 24.0;
        Vec3 start = ctx.aimStart();
        Vec3 end = start.add(ctx.aimDirection().normalize().scale(range));
        HitResult hit = RaycastBuilder.begin(ctx.level(), ctx.caster())
            .start(start).end(end)
            .checkForBlocks(true)
            .inflate(0.3f)
            .cast();

        if (!(hit instanceof EntityHitResult ehr) || !(ehr.getEntity() instanceof LivingEntity target)) {
            return CastResult.FAILED;
        }

        MagicBindingEntity binding = new MagicBindingEntity(ctx.level(), ctx.caster(), target, params);
        ctx.level().addFreshEntity(binding);
        return CastResult.SUCCESS;
    }

    @Override
    public int circleLevel() {
        return 2;
    }
}
