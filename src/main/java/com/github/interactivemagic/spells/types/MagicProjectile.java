package com.github.interactivemagic.spells.types;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.CastResult;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.entities.spells.MagicProjectileEntity;
import com.github.interactivemagic.init.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MagicProjectile extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "magic_projectile");

    public MagicProjectile() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 40; }
    @Override public float baseDamage() { return 5f; }
    @Override public float baseSize() { return 1f; }
    @Override public float baseSpeed() { return 0.75f; }

    @Override public String iconName() { return "line"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.BLUNT; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        Vec3 dir = ctx.aimDirection().normalize();
        MagicProjectileEntity bolt = new MagicProjectileEntity(ctx.level(), ctx.caster(), params, dir);
        Vec3 spawn = ctx.aimStart().add(dir.scale(0.5));
        bolt.setPos(spawn.x, spawn.y, spawn.z);
        //bolt.shoot(dir.x, dir.y, dir.z, params.speed, 0.0F);
        ctx.level().addFreshEntity(bolt);
        return CastResult.SUCCESS;
    }
}
