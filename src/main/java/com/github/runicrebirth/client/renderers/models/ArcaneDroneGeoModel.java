package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.ArcaneDroneEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ArcaneDroneGeoModel extends GeoModel<ArcaneDroneEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/arcane_drone.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/arcane_drone.animation.json");

    @Override
    public ResourceLocation getModelResource(ArcaneDroneEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(ArcaneDroneEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(ArcaneDroneEntity entity) { return ANIMATIONS; }
}
