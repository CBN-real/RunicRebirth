package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.AbstractSectBannerBlock;
import com.github.runicrebirth.blocks.SectBannerVariantBlock;
import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class SectBannerVariantBlockEntity extends AbstractSectBannerBlockEntity {

    private static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "textures/block/sect_banner_mage.png");
    private static final ResourceLocation FALLBACK_MODEL = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "geo/block/sect_banner.geo.json");

    public SectBannerVariantBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SECT_BANNER_VARIANT.get(), pos, state);
    }

    public ResourceLocation getVariantTexture() {
        if (level != null && getBlockState().getBlock() instanceof SectBannerVariantBlock b) {
            return b.getGeoTexture();
        }
        return FALLBACK_TEXTURE;
    }

    public ResourceLocation getVariantGeoModel() {
        if (level != null && getBlockState().getBlock() instanceof SectBannerVariantBlock b) {
            String baseName = (b.getBannerType() == SectBannerVariantBlock.BannerType.TATTERED)
                    ? "tattered_sect_banner" : "sect_banner";
            AttachFace face = getBlockState().getValue(AbstractSectBannerBlock.FACE);
            String suffix = switch (face) {
                case WALL    -> "_wall";
                case CEILING -> "_ceiling";
                default      -> "";
            };
            return ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID,
                    "geo/block/" + baseName + suffix + ".geo.json");
        }
        return FALLBACK_MODEL;
    }
}
