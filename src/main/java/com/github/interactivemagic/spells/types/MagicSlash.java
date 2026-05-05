package com.github.interactivemagic.spells.types;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.CastResult;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.entities.spells.MagicSlashEntity;
import com.github.interactivemagic.init.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MagicSlash extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "magic_slash");

    public MagicSlash() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 80; }
    @Override public float baseDamage() { return 4f; }
    @Override public float baseSize() { return 1f; }
    @Override public float baseSpeed() { return 1.0f; }

    @Override public String iconName() { return "slash"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.SHARP; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        Vec3 dir = ctx.aimDirection().normalize();
        MagicSlashEntity slash = new MagicSlashEntity(ctx.level(), ctx.caster(), params);
        Vec3 spawn = ctx.aimStart().add(dir.scale(0.5));
        slash.setPos(spawn.x, spawn.y, spawn.z);
        slash.shoot(dir.x, dir.y, dir.z, params.speed, 0.0F);
        ctx.level().addFreshEntity(slash);
        return CastResult.SUCCESS;
    }
}
