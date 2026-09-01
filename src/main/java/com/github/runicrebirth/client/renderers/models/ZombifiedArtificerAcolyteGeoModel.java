package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.github.runicrebirth.entities.mobs.ZombifiedArtificerAcolyteEntity;
import com.geckolib.renderer.base.GeoRenderState;

public class ZombifiedArtificerAcolyteGeoModel extends GeoModel<ZombifiedArtificerAcolyteEntity> {

    private static final Identifier MODEL =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "entity/zombified_artificer_acolyte");
    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/mob/zombified_acolyte.png");
    private static final Identifier ANIMATIONS =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "entity/zombified_artificer_acolyte");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) { return MODEL; }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) { return TEXTURE; }

    @Override
    public Identifier getAnimationResource(ZombifiedArtificerAcolyteEntity animatable) { return ANIMATIONS; }

    // TODO GeckoLib 5: setCustomAnimations removed. Migrate head bone rotation (xRot/yHeadRot) to
    // renderer's adjustModelBonesForRender(RenderPassInfo, BoneSnapshots) via a custom GeoRenderState.
}
