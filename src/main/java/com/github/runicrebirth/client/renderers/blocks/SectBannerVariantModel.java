package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.SectBannerVariantBlockEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class SectBannerVariantModel extends GeoModel<SectBannerVariantBlockEntity> {

    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(
            RunicRebirth.MODID, "block/sect_banner");

    private static final Identifier FALLBACK_MODEL = Identifier.fromNamespaceAndPath(
            com.github.runicrebirth.RunicRebirth.MODID, "block/sect_banner");
    private static final Identifier FALLBACK_TEXTURE = Identifier.fromNamespaceAndPath(
            com.github.runicrebirth.RunicRebirth.MODID, "textures/block/sect_banner_mage.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        // TODO GeckoLib 5: migrate variant selection to render state data ticket
        return FALLBACK_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        // TODO GeckoLib 5: migrate variant selection to render state data ticket
        return FALLBACK_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(SectBannerVariantBlockEntity animatable) {
        return ANIMATION;
    }
}
