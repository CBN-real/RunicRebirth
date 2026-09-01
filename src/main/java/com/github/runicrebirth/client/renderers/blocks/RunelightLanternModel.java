package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.RunelightLanternBlock;
import com.github.runicrebirth.blocks.entity.RunelightLanternBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RunelightLanternModel extends GeoModel<RunelightLanternBlockEntity> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/runelight_lantern.geo.json");
    private static final ResourceLocation MODEL_CEIL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/runelight_lantern_ceil.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "animations/block/runelight_lantern.animation.json");

    @Override
    public ResourceLocation getModelResource(RunelightLanternBlockEntity animatable) {
        return animatable.getBlockState().getValue(RunelightLanternBlock.HANGING) ? MODEL_CEIL : MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RunelightLanternBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RunelightLanternBlockEntity animatable) {
        return ANIMATION;
    }
}
