package com.github.interactivemagic.client.renderers.models;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.entities.spells.MagicHammerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicHammerGeoModel extends GeoModel<MagicHammerEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "geo/spell/magic_hammer.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "textures/spell_boundary/arcane_boundary_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "animations/spell/magic_hammer.animation.json");

    @Override
    public ResourceLocation getModelResource(MagicHammerEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MagicHammerEntity entity) {
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
    public ResourceLocation getAnimationResource(MagicHammerEntity entity) {
        return ANIMATIONS;
    }
}
