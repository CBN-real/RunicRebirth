package com.github.interactivemagic.spells.types;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.CastResult;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.damage.DamageSources;
import com.github.interactivemagic.damage.SpellDamageSource;
import com.github.interactivemagic.init.ModElements;
import com.github.interactivemagic.util.RaycastBuilder;
import com.github.interactivemagic.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagicExplosion extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "magic_explosion");

    public MagicExplosion() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 100; }
    @Override public float baseDamage() { return 7f; }
    @Override public float baseSize() { return 1.5f; }

    @Override public String iconName() { return "explosion"; }
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

        Vec3 hitPos;
        if (hit instanceof EntityHitResult ehr) {
            hitPos = ehr.getLocation();
        } else if (hit instanceof BlockHitResult bhr) {
            hitPos = Vec3.atCenterOf(bhr.getBlockPos());
        } else {
            return CastResult.FAILED;
        }

        double aoeRadius = 1.5 * params.size;
        SpellDamageSource source = SpellDamageSource.source(ctx.caster(), params.damageCategory, params.element);
        for (LivingEntity target : Utils.entitiesInRange(ctx.level(), hitPos, aoeRadius, ctx.caster())) {
            DamageSources.applyDamage(target, params.damage, source);
        }

        int particleCount = (int) (40 * params.size);
        double spread = 0.5 * params.size;
        ctx.level().sendParticles(params.element.particle(), hitPos.x, hitPos.y, hitPos.z,
            particleCount, spread, spread, spread, 0.1);

        return CastResult.SUCCESS;
    }
}
