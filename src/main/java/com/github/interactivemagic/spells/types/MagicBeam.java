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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagicBeam extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "magic_beam");

    public MagicBeam() { super(ID); }

    @Override public int cooldownTicks() { return 80; }
    @Override public float baseDamage() { return 3f; }

    @Override public String iconName() { return "v"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.SPIRIT; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        double range = 12.0 * params.size;
        Vec3 start = ctx.aimStart();
        Vec3 end = start.add(ctx.aimDirection().normalize().scale(range));
        HitResult hit = RaycastBuilder.begin(ctx.level(), ctx.caster())
            .start(start).end(end)
            .checkForBlocks(true)
            .inflate(0.3f)
            .cast();
        if (hit instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity target) {
            SpellDamageSource src = SpellDamageSource.source(ctx.caster(), params.damageCategory, params.element);
            DamageSources.applyDamage(target, params.damage, src);
        }
        for (int i = 0; i < 20 * params.size; i++) {
            double t = i / (20.0 * params.size);
            Vec3 p = start.lerp(end, t);
            ctx.level().sendParticles(params.element.particle(), p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
        return CastResult.SUCCESS;
    }
}
