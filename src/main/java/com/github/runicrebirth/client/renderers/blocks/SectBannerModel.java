package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.SectBannerBlockEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class SectBannerModel extends GeoModel<SectBannerBlockEntity> {

    private static final Identifier MODEL_FLOOR   = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/sect_banner");
    private static final Identifier MODEL_WALL    = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/sect_banner_wall");
    private static final Identifier MODEL_CEILING = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/sect_banner_ceiling");
    private static final Identifier TEXTURE   = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "textures/block/sect_banner.png");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "block/sect_banner");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        // TODO GeckoLib 5: migrate block-state face selection to render state data ticket
        return MODEL_FLOOR;
    }

    @Override public Identifier getTextureResource(GeoRenderState renderState) { return TEXTURE; }
    @Override public Identifier getAnimationResource(SectBannerBlockEntity animatable) { return ANIMATION; }
}
