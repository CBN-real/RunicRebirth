package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.OculusPortalBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class OculusPortalModel extends GeoModel<OculusPortalBlockEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/oculus_portal.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/block/oculus_portal.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "animations/block/oculus_portal.animation.json");

    @Override
    public ResourceLocation getModelResource(OculusPortalBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(OculusPortalBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(OculusPortalBlockEntity animatable) {
        return ANIMATION;
    }
}
