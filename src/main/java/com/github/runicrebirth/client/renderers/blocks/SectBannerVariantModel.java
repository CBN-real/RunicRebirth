package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.SectBannerVariantBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SectBannerVariantModel extends GeoModel<SectBannerVariantBlockEntity> {

    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
            RunicRebirth.MODID, "animations/block/sect_banner.animation.json");

    @Override
    public ResourceLocation getModelResource(SectBannerVariantBlockEntity a) {
        return a.getVariantGeoModel();
    }

    @Override
    public ResourceLocation getTextureResource(SectBannerVariantBlockEntity a) {
        return a.getVariantTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(SectBannerVariantBlockEntity a) {
        return ANIMATION;
    }
}
