package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.RunicLeverBlock;
import com.github.runicrebirth.blocks.entity.RunicLeverBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.AttachFace;
import software.bernie.geckolib.model.GeoModel;

public class RunicLeverModel extends GeoModel<RunicLeverBlockEntity> {

    private static final ResourceLocation MODEL_FLOOR = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/runic_lever.geo.json");
    private static final ResourceLocation MODEL_WALL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/runic_lever_wall.geo.json");
    private static final ResourceLocation MODEL_CEILING = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/runic_lever_ceiling.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/entity/runic_templates/earth_runic_template.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "animations/block/runic_lever.animation.json");

    @Override
    public ResourceLocation getModelResource(RunicLeverBlockEntity animatable) {
        AttachFace face = animatable.getBlockState().getValue(RunicLeverBlock.FACE);
        return switch (face) {
            case WALL    -> MODEL_WALL;
            case CEILING -> MODEL_CEILING;
            default      -> MODEL_FLOOR;
        };
    }

    @Override
    public ResourceLocation getTextureResource(RunicLeverBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RunicLeverBlockEntity animatable) {
        return ANIMATION;
    }
}
