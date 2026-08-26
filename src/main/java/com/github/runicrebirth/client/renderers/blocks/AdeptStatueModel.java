package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.AdeptStatueBlockEntity;
import com.github.runicrebirth.init.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AdeptStatueModel extends GeoModel<AdeptStatueBlockEntity> {

    private static final ResourceLocation GEO_MAGE      = rl("geo/block/adept_mage_set_statue.geo.json");
    private static final ResourceLocation GEO_WIZARD    = rl("geo/block/adept_wizard_set_statue.geo.json");
    private static final ResourceLocation GEO_RUNEBLADE = rl("geo/block/adept_runeblade_armor_statue.geo.json");
    private static final ResourceLocation GEO_ARTIFICER = rl("geo/block/adept_artificer_set_statue.geo.json");

    private static final ResourceLocation TEX_SHARED = rl("textures/block/adept_armor_statue_texture.png");

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, path);
    }

    @Override
    public ResourceLocation getModelResource(AdeptStatueBlockEntity animatable) {
        var block = animatable.getBlockState().getBlock();
        if (block == ModBlocks.ADEPT_WIZARD_STATUE.get())    return GEO_WIZARD;
        if (block == ModBlocks.ADEPT_RUNEBLADE_STATUE.get()) return GEO_RUNEBLADE;
        if (block == ModBlocks.ADEPT_ARTIFICER_STATUE.get()) return GEO_ARTIFICER;
        return GEO_MAGE;
    }

    @Override
    public ResourceLocation getTextureResource(AdeptStatueBlockEntity animatable) {
        return TEX_SHARED;
    }

    @Override
    public ResourceLocation getAnimationResource(AdeptStatueBlockEntity animatable) {
        return null;
    }
}
