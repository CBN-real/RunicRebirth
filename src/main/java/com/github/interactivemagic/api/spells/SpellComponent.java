package com.github.interactivemagic.api.spells;

import com.github.interactivemagic.InteractiveMagic;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public sealed interface SpellComponent permits SpellType, SpellModifier {

    ResourceLocation id();

    /** Legacy icon resource (preexisting callers). Prefer getSpellIconPath for HUD rendering. */
    ResourceLocation iconTexture();

    default Component displayName() {
        return Component.translatable("spell_component." + id().getNamespace() + "." + id().getPath());
    }

    /** Short icon name used by HUD rendering. Subclasses override to map to their sprite (e.g. "line", "circle", "plus"). */
    default String iconName() {
        return id().getPath();
    }

    /** Icon filename suffix. SpellType defaults "_icon", SpellModifier overrides to "_icon_small". */
    default String iconSuffix() {
        return "_icon";
    }

    /** Full texture path for the HUD shape icon. Resolves to assets/&lt;ns&gt;/textures/gui/shape_icons/&lt;iconName&gt;&lt;iconSuffix&gt;.png */
    default ResourceLocation getSpellIconPath() {
        return ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID,
            "textures/gui/shape_icons/" + iconName() + iconSuffix() + ".png");
    }

    /** Sprite id for the HUD slot background. Resolves under textures/gui/sprites/. SpellType overrides with element-based big slot. */
    default ResourceLocation getOverlaySlotPath() {
        return ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "hud/overlay_slot_border_small");
    }
}
