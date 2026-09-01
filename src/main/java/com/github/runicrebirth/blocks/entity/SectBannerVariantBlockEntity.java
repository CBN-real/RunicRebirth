package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.SectBannerVariantBlock;
import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public class SectBannerVariantBlockEntity extends AbstractSectBannerBlockEntity {

    private static final Identifier FALLBACK_TEXTURE = Identifier.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/block/sect_banner_mage.png");
    private static final Identifier FALLBACK_MODEL = Identifier.fromNamespaceAndPath(
            RunicRebirth.MODID, "block/sect_banner");

    public SectBannerVariantBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SECT_BANNER_VARIANT.get(), pos, state);
    }

    public Identifier getVariantTexture() {
        if (level != null && getBlockState().getBlock() instanceof SectBannerVariantBlock b) {
            return b.getGeoTexture();
        }
        return FALLBACK_TEXTURE;
    }

    public Identifier getVariantGeoModel() {
        if (level != null && getBlockState().getBlock() instanceof SectBannerVariantBlock b) {
            return b.getGeoModelPath();
        }
        return FALLBACK_MODEL;
    }
}
