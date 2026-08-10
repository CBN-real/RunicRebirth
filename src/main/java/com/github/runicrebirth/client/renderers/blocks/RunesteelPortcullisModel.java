package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.RunesteelPortcullisBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RunesteelPortcullisModel extends GeoModel<RunesteelPortcullisBlockEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
        RunicRebirth.MODID, "geo/block/runesteel_portcullis.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
        RunicRebirth.MODID, "animations/block/runesteel_portcullis.animation.json");

    @Override
    public ResourceLocation getModelResource(RunesteelPortcullisBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RunesteelPortcullisBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RunesteelPortcullisBlockEntity animatable) {
        return ANIMATION;
    }
}
