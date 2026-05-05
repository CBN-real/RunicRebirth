package com.github.interactivemagic.client.renderers.models;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.entities.spells.MagicArrowEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicArrowGeoModel extends GeoModel<MagicArrowEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "geo/spell/magic_arrow.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "textures/spell_boundary/arcane_boundary_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "animations/spell/magic_arrow.animation.json");

    @Override
    public ResourceLocation getModelResource(MagicArrowEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MagicArrowEntity entity) {
        if (entity != null) {
            String elementId = entity.getElementId();
            ResourceLocation parsed = ResourceLocation.tryParse(elementId);
            if (parsed != null) {
                return ResourceLocation.fromNamespaceAndPath(
                    InteractiveMagic.MODID,
                    "textures/spell_boundary/" + parsed.getPath() + "_boundary_texture.png"
                );
            }
        }
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MagicArrowEntity entity) {
        return ANIMATIONS;
    }
}
