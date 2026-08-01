package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.mobs.ZombifiedArtificerAcolyteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ZombifiedArtificerAcolyteGeoModel extends GeoModel<ZombifiedArtificerAcolyteEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/entity/zombified_artificer_acolyte.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/mob/zombified_artificer_acolyte.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/entity/zombified_artificer_acolyte.animation.json");

    @Override
    public ResourceLocation getModelResource(ZombifiedArtificerAcolyteEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(ZombifiedArtificerAcolyteEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(ZombifiedArtificerAcolyteEntity entity) { return ANIMATIONS; }
}
