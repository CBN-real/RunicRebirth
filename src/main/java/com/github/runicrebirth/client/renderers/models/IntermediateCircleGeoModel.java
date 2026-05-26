package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.IntermediateCircleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IntermediateCircleGeoModel extends GeoModel<IntermediateCircleEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/spell/intermediate_circle.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/spell_circles/arcane_circle_texture.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/spell/intermediate_circle.animation.json");

    @Override
    public ResourceLocation getModelResource(IntermediateCircleEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(IntermediateCircleEntity entity) {
        if (entity != null) {
            String elementId = entity.getElementId();
            ResourceLocation parsed = ResourceLocation.tryParse(elementId);
            if (parsed != null) {
                return ResourceLocation.fromNamespaceAndPath(
                    RunicRebirth.MODID,
                    "textures/entity/spell_circles/" + parsed.getPath() + "_circle_texture.png"
                );
            }
        }
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(IntermediateCircleEntity entity) {
        return ANIMATIONS;
    }
}
