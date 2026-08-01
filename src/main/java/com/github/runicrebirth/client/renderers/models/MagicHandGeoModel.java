package com.github.runicrebirth.client.renderers.models;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.MagicHandEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicHandGeoModel extends GeoModel<MagicHandEntity> {

    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "geo/spell/magic_hand.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATIONS =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "animations/spell/magic_hand.animation.json");

    @Override
    public ResourceLocation getModelResource(MagicHandEntity entity) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(MagicHandEntity entity) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(MagicHandEntity entity) { return ANIMATIONS; }
}
