package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.mobs.ZombifiedRunebladeAcolyteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ZombifiedRunebladeAcolyteGeoModel extends GeoModel<ZombifiedRunebladeAcolyteEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/zombified_runeblade_acolyte.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/mob/zombified_runeblade_acolyte.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/zombified_runeblade_acolyte.animation.json");

    @Override
    public ResourceLocation getModelResource(ZombifiedRunebladeAcolyteEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(ZombifiedRunebladeAcolyteEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(ZombifiedRunebladeAcolyteEntity entity) { return ANIMATIONS; }
}
