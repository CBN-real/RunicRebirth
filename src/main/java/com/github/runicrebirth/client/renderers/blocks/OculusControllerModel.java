package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.OculusControllerBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class OculusControllerModel extends GeoModel<OculusControllerBlockEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/oculus_controller.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/block/oculus_controller.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "animations/block/oculus_controller.animation.json");

    @Override
    public ResourceLocation getModelResource(OculusControllerBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(OculusControllerBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(OculusControllerBlockEntity animatable) {
        return ANIMATION;
    }
}
