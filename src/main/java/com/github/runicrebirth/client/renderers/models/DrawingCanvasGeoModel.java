package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.DrawingCanvasEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DrawingCanvasGeoModel extends GeoModel<DrawingCanvasEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/gui/canvas_screen.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/spell_circles/circle_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/gui/drawing_canvas.animation.json");

    @Override
    public ResourceLocation getModelResource(DrawingCanvasEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DrawingCanvasEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(DrawingCanvasEntity entity) {
        return ANIMATIONS;
    }
}
