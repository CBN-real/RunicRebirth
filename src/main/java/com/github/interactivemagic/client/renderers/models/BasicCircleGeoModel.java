package com.github.interactivemagic.client.renderers.models;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.entities.spells.BasicCircleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BasicCircleGeoModel extends GeoModel<BasicCircleEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "geo/spell/basic_circle.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "textures/entity/basic_circle/basic_circle_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "animations/spell/basic_circle.animation.json");

    @Override
    public ResourceLocation getModelResource(BasicCircleEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BasicCircleEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BasicCircleEntity entity) {
        return ANIMATIONS;
    }
}
