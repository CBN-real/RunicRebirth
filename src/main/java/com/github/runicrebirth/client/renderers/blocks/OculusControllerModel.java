package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.OculusControllerBlockEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class OculusControllerModel extends GeoModel<OculusControllerBlockEntity> {

    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(
            RunicRebirth.MODID, "block/oculus_controller");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/block/oculus_controller.png");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(
            RunicRebirth.MODID, "block/oculus_controller");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(OculusControllerBlockEntity animatable) {
        return ANIMATION;
    }
}
