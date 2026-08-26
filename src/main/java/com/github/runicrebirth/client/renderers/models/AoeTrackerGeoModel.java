package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.AoeTrackerEntity;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.model.GeoModel;

@OnlyIn(Dist.CLIENT)
public class AoeTrackerGeoModel extends GeoModel<AoeTrackerEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/spell/aoe_tracker.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/spell/aoe_tracker.animation.json");

    @Override
    public ResourceLocation getModelResource(AoeTrackerEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(AoeTrackerEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(AoeTrackerEntity entity) { return ANIMATIONS; }
}
