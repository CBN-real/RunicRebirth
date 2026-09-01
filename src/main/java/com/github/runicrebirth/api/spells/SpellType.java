package com.github.runicrebirth.api.spells;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.AdvancedCircleEntity;
import com.github.runicrebirth.entities.spells.BasicCircleEntity;
import com.github.runicrebirth.entities.spells.IntermediateCircleEntity;
import com.github.runicrebirth.entities.spells.AbstractCircleEntity;
import com.github.runicrebirth.util.RaycastTarget;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract non-sealed class SpellType implements SpellComponent {

    private final Identifier id;

    protected SpellType(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    @Override
    public Identifier iconTexture() {
        return Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/gui/spell/type/" + id.getPath() + ".png");
    }

    public abstract Element defaultElement();

    public abstract MagicDamageType damageCategory();

    @Override
    public Identifier getOverlaySlotPath() {
        Element e = defaultElement();
        String elemPath = e == null ? "arcane" : e.id().getPath();
        return Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "hud/" + elemPath + "_overlay_slot_border");
    }

    public abstract CastResult onCast(SpellCastContext ctx, SpellParams params);

    public int circleLevel() {
        return 0;
    }

    public AbstractCircleEntity buildCircle(Level level, ServerPlayer caster, SpellParams params,
                                         Vec3 aimDirection, ItemStack wandItem,
                                         int totalCasts, int castingDelayTicks,
                                         float xRot, float yRot, RaycastTarget target) {
        return switch (circleLevel()) {
            case 1 -> new IntermediateCircleEntity(level, caster, this, params, aimDirection, wandItem,
                totalCasts, castingDelayTicks, defaultCircleLifespan() + 20, xRot, yRot, target);
            case 2 -> new AdvancedCircleEntity(level, caster, this, params, aimDirection, wandItem,
                totalCasts, castingDelayTicks, defaultCircleLifespan() + 40, xRot, yRot, target);
            default -> new BasicCircleEntity(level, caster, this, params, aimDirection, wandItem,
                totalCasts, castingDelayTicks, defaultCircleLifespan(), xRot, yRot, target);
        };
    }

    public int defaultCircleLifespan() {
        return 20;
    }

    public int cooldownTicks() {
        return 100;
    }

    public int castingDelayTicks() { return 6; }

    public int multiCastDelay() { return 10; }

    public float baseDamage() {
        return 4f;
    }

    public float baseSize() {
        return 1f;
    }

    public float spellHeight() { return 1f * this.baseSize();}

    public float baseSpeed() {
        return 1.0f;
    }

    public int baseDuration() {
        return 40;
    }

    public float baseRange() {
        return 32.0f;
    }

    /** Projectile spells return true — they can always be cast regardless of target distance. */
    public boolean bypassesRangeCheck() {
        return false;
    }

    /** AOE radius in blocks at base size=1. 0 means no AOE indicator shown. */
    public float baseAoeRadius() { return 0f; }
}
