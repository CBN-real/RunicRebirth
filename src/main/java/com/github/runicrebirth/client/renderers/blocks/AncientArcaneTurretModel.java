package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.AncientArcaneTurretBlock;
import com.github.runicrebirth.blocks.entity.AncientArcaneTurretBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.AttachFace;
import software.bernie.geckolib.model.GeoModel;

public class AncientArcaneTurretModel extends GeoModel<AncientArcaneTurretBlockEntity> {

    private static final ResourceLocation FLOOR_MODEL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/ancient_arcane_turret_floor.geo.json");
    private static final ResourceLocation CEILING_MODEL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/ancient_arcane_turret_ceiling.geo.json");
    private static final ResourceLocation WALL_MODEL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/ancient_arcane_turret_wall.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "animations/block/ancient_arcane_turret.animation.json");

    @Override
    public ResourceLocation getModelResource(AncientArcaneTurretBlockEntity animatable) {
        AttachFace face = animatable.getBlockState().getValue(AncientArcaneTurretBlock.FACE);
        return switch (face) {
            case CEILING -> CEILING_MODEL;
            case WALL    -> WALL_MODEL;
            default      -> FLOOR_MODEL;
        };
    }

    @Override
    public ResourceLocation getTextureResource(AncientArcaneTurretBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(AncientArcaneTurretBlockEntity animatable) {
        return ANIMATION;
    }
}
