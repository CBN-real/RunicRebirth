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
import com.github.interactivemagic.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class MagicCone extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "magic_cone");

    public MagicCone() { super(ID); }

    @Override public int cooldownTicks() { return 120; }
    @Override public float baseDamage() { return 2.5f; }
    @Override public float baseSize() { return 6f; }

    @Override public String iconName() { return "circle"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.BLUNT; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        double range = params.size;
        double halfAngle = 35.0;
        Vec3 apex = ctx.aimStart();
        Vec3 dir = ctx.aimDirection().normalize();
        for (LivingEntity target : Utils.entitiesInCone(ctx.level(), apex, dir, range, halfAngle, ctx.caster())) {
            SpellDamageSource src = SpellDamageSource.source(ctx.caster(), params.damageCategory, params.element);
            DamageSources.applyDamage(target, params.damage, src);
        }
        int particleCount = (int) (range * 8);
        for (int i = 0; i < particleCount; i++) {
            double t = i / (double) particleCount;
            double spread = halfAngle * (Math.random() * 2 - 1);
            double spreadRad = Math.toRadians(spread);
            double yaw = Math.random() * Math.PI * 2;
            Vec3 perp = dir.yRot(0).cross(new Vec3(0, 1, 0)).normalize();
            Vec3 offset = perp.scale(Math.sin(spreadRad) * Math.cos(yaw))
                .add(0, Math.sin(spreadRad) * Math.sin(yaw), 0);
            Vec3 p = apex.add(dir.add(offset).normalize().scale(t * range));
            ctx.level().sendParticles(params.element.particle(), p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
        return CastResult.SUCCESS;
    }
}
