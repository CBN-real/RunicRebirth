package com.github.runicrebirth.spells.types;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.CastResult;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.entities.spells.MagicBlastEntity;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MagicBlast extends SpellType {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "magic_blast");

    public MagicBlast() { super(ID); }

    @Override public int cooldownTicks() { return 120; }
    @Override public float baseDamage() { return 2.5f; }
    @Override public float baseSize() { return 2f; }

    @Override public String iconName() { return "circle"; }
    @Override public Element defaultElement() { return ModElements.ARCANE.get(); }
    @Override public MagicDamageType damageCategory() { return MagicDamageType.BLUNT; }

    @Override
    public CastResult onCast(SpellCastContext ctx, SpellParams params) {
        Vec3 dir = ctx.aimDirection().normalize();
        Vec3 spawnPos = ctx.aimStart().add(dir.scale(1.0));

        MagicBlastEntity blast = new MagicBlastEntity(
            ModEntities.MAGIC_BLAST.get(), ctx.level());
        blast.init(ctx.caster(), spawnPos, dir, params);
        ctx.level().addFreshEntity(blast);
        return CastResult.SUCCESS;
    }
}
