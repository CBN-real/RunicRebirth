package com.github.interactivemagic.client.renderers.models;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.entities.spells.MagicBeamEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicBeamGeoModel extends GeoModel<MagicBeamEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "geo/spell/magic_beam.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "textures/entity/magic_beam/magic_beam_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "animations/spell/magic_beam.animation.json");

    @Override
    public ResourceLocation getModelResource(MagicBeamEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MagicBeamEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MagicBeamEntity entity) {
        return ANIMATIONS;
    }
}
