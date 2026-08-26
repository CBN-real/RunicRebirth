package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.EarthVeinCircleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EarthVeinCircleGeoModel extends GeoModel<EarthVeinCircleEntity> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/earth_vein_circle.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/earth_vein_circle.animation.json");

    @Override
    public ResourceLocation getModelResource(EarthVeinCircleEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(EarthVeinCircleEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(EarthVeinCircleEntity entity) { return ANIMATIONS; }
}
