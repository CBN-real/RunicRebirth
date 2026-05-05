package com.github.interactivemagic.api.spells;

import com.github.interactivemagic.InteractiveMagic;
import net.minecraft.resources.ResourceLocation;

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
        return ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "hud/" + elemPath + "_overlay_slot_border");
    }

    public abstract CastResult onCast(SpellCastContext ctx, SpellParams params);

    public int cooldownTicks() {
        return 100;
    }

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
