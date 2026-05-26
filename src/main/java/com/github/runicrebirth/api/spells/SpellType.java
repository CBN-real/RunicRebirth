package com.github.runicrebirth.api.spells;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.AdvancedCircleEntity;
import com.github.runicrebirth.entities.spells.BasicCircleEntity;
import com.github.runicrebirth.entities.spells.IntermediateCircleEntity;
import com.github.runicrebirth.entities.spells.AbstractCircleEntity;
import com.github.runicrebirth.util.RaycastTarget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract non-sealed class SpellType implements SpellComponent {

    private final ResourceLocation id;

    protected SpellType(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public ResourceLocation iconTexture() {
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "textures/gui/spell/type/" + id.getPath() + ".png");
    }

    public abstract Element defaultElement();

    public abstract MagicDamageType damageCategory();

    @Override
    public ResourceLocation getOverlaySlotPath() {
        Element e = defaultElement();
        String elemPath = e == null ? "arcane" : e.id().getPath();
        return ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "hud/" + elemPath + "_overlay_slot_border");
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
                totalCasts, castingDelayTicks, defaultCircleLifespan(), xRot, yRot, target);
            case 2 -> new AdvancedCircleEntity(level, caster, this, params, aimDirection, wandItem,
                totalCasts, castingDelayTicks, defaultCircleLifespan(), xRot, yRot, target);
            default -> new BasicCircleEntity(level, caster, this, params, aimDirection, wandItem,
                totalCasts, castingDelayTicks, defaultCircleLifespan(), xRot, yRot, target);
        };
    }

    public int defaultCircleLifespan() {
        return 60;
    }

    public int cooldownTicks() {
        return 100;
    }

    public int castingDelayTicks() { return 15; }

    public int multiCastDelay() { return 15; }

    public float baseDamage() {
        return 4f;
    }

    public float baseSize() {
        return 1f;
    }

    public float baseSpeed() {
        return 1.0f;
    }

    public int baseDuration() {
        return 40;
    }
}
