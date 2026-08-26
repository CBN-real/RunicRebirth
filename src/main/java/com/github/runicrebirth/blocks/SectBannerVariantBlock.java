package com.github.runicrebirth.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.SectBannerVariantBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SectBannerVariantBlock extends AbstractSectBannerBlock {

    public enum BannerType { SECT, TATTERED }

    private static final MapCodec<SectBannerVariantBlock> CODEC = MapCodec.unit(
            () -> new SectBannerVariantBlock(Properties.of(), "sect_banner_mage", BannerType.SECT));

    private final String textureName;
    private final BannerType bannerType;

    public SectBannerVariantBlock(Properties props, String textureName, BannerType bannerType) {
        super(props);
        this.textureName = textureName;
        this.bannerType  = bannerType;
    }

    public ResourceLocation getGeoTexture() {
        return ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID,
                "textures/block/" + textureName + ".png");
    }

    public ResourceLocation getGeoModelPath() {
        String name = (bannerType == BannerType.TATTERED) ? "tattered_sect_banner" : "sect_banner";
        return ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID,
                "geo/block/" + name + ".geo.json");
    }

    public String getTextureName() { return textureName; }

    public BannerType getBannerType() { return bannerType; }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SectBannerVariantBlockEntity(pos, state);
    }
}
