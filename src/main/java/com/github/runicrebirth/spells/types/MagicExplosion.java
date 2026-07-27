package com.github.runicrebirth.spells.types;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.CastResult;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.entities.spells.MagicExplosionEntity;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.util.RaycastBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagicExplosion extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "magic_explosion");

    public MagicExplosion() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 100; }
    @Override public float baseDamage() { return 7f; }
    @Override public float baseSize() { return 1.0f; }
    @Override public float spellHeight() { return 1.5f * this.baseSize(); }

    @Override public String iconName() { return "explosion"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.BLUNT; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        double range = 16.0 * params.rangeMultiplier;
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

        MagicExplosionEntity explosion = new MagicExplosionEntity(ctx.level(), ctx.caster(), params);
        explosion.setPos(hitPos.x, hitPos.y + 0.6f, hitPos.z);
        ctx.level().addFreshEntity(explosion);
        return CastResult.SUCCESS;
    }

    @Override
    public int circleLevel() {
        return 1;
    }
}
