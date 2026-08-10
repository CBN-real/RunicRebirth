package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.HammerDroneEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HammerDroneGeoModel extends GeoModel<HammerDroneEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/hammer_drone.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/hammer_drone.animation.json");

    @Override
    public ResourceLocation getModelResource(HammerDroneEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(HammerDroneEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(HammerDroneEntity entity) { return ANIMATIONS; }
}
