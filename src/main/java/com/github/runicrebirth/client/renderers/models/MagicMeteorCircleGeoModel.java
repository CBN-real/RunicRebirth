package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.MagicMeteorCircleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicMeteorCircleGeoModel extends GeoModel<MagicMeteorCircleEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/spell/magic_meteor_circle.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/spell/magic_meteor_circle.animation.json");

    @Override
    public ResourceLocation getModelResource(MagicMeteorCircleEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MagicMeteorCircleEntity entity) {
        if (entity != null) {
            String elementId = entity.getElementId();
            ResourceLocation parsed = ResourceLocation.tryParse(elementId);
            if (parsed != null) {
                return ResourceLocation.fromNamespaceAndPath(
                    RunicRebirth.MODID,
                    "textures/entity/runic_templates/" + parsed.getPath() + "_runic_template.png"
                );
            }
        }
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MagicMeteorCircleEntity entity) {
        return ANIMATIONS;
    }
}
