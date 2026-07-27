package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.spells.TargetCircleEntity;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.model.GeoModel;

@OnlyIn(Dist.CLIENT)
public class TargetCircleGeoModel extends GeoModel<TargetCircleEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/spell/target_circle.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/spell/target_circle.animation.json");

    @Override
    public ResourceLocation getModelResource(TargetCircleEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(TargetCircleEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(TargetCircleEntity entity) { return ANIMATIONS; }
}
