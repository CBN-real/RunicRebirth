package com.github.interactivemagic.spells.types;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.CastResult;
import com.github.interactivemagic.api.spells.Element;
import com.github.interactivemagic.api.spells.MagicDamageType;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.entities.spells.MagicBallistaEntity;
import com.github.interactivemagic.init.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MagicBallista extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "magic_ballista");

    public MagicBallista() {
        super(ID);
    }

    @Override public int cooldownTicks() { return 120; }
    @Override public float baseDamage() { return 8f; }
    @Override public float baseSize() { return 1f; }
    @Override public float baseSpeed() { return 1.5f; }

    @Override public String iconName() { return "ballista"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.SHARP; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        Vec3 dir = ctx.aimDirection().normalize();
        MagicBallistaEntity ballista = new MagicBallistaEntity(ctx.level(), ctx.caster(), params, dir, params.speed);
        Vec3 spawn = ctx.aimStart().add(dir.scale(1.5));
        ballista.setPos(spawn.x, spawn.y, spawn.z);
        ctx.level().addFreshEntity(ballista);
        return CastResult.SUCCESS;
    }
}
