package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.mobs.RunesteelGolemEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RunesteelGolemGeoModel extends GeoModel<RunesteelGolemEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/runesteel_golem.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/mob/runesteel_golem.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/runesteel_golem.animation.json");

    @Override
    public ResourceLocation getModelResource(RunesteelGolemEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(RunesteelGolemEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(RunesteelGolemEntity entity) { return ANIMATIONS; }
}
