package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.mobs.SkeletalMageAcolyteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SkeletalMageAcolyteGeoModel extends GeoModel<SkeletalMageAcolyteEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/skeletal_mage_acolyte.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/mob/skeletal_mage_acolyte.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/skeletal_mage_acolyte.animation.json");

    @Override
    public ResourceLocation getModelResource(SkeletalMageAcolyteEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(SkeletalMageAcolyteEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(SkeletalMageAcolyteEntity entity) { return ANIMATIONS; }
}
