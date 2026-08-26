package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.RunesteelCacheBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RunesteelCacheModel extends GeoModel<RunesteelCacheBlockEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/runesteel_cache.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "animations/block/runesteel_cache.animation.json");

    @Override
    public ResourceLocation getModelResource(RunesteelCacheBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RunesteelCacheBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RunesteelCacheBlockEntity animatable) {
        return ANIMATION;
    }
}
