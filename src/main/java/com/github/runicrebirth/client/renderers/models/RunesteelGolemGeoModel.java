package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.github.runicrebirth.entities.mobs.RunesteelGolemEntity;
import com.geckolib.renderer.base.GeoRenderState;

public class RunesteelGolemGeoModel extends GeoModel<RunesteelGolemEntity> {

    private static final Identifier MODEL =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "entity/runesteel_golem");
    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/mob/runesteel_golem.png");
    private static final Identifier ANIMATIONS =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "entity/runesteel_golem");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) { return MODEL; }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) { return TEXTURE; }

    @Override
    public Identifier getAnimationResource(RunesteelGolemEntity animatable) { return ANIMATIONS; }

    // TODO GeckoLib 5: setCustomAnimations removed. Migrate Head bone rotation (via EntityModelData/DataTickets)
    // to renderer's adjustModelBonesForRender(RenderPassInfo, BoneSnapshots) via a custom GeoRenderState.
}
