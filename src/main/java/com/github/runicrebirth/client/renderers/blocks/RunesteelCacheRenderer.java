package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.RunesteelCacheBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RunesteelCacheRenderer extends GeoBlockRenderer<RunesteelCacheBlockEntity> {

    public RunesteelCacheRenderer(BlockEntityRendererProvider.Context context) {
        super(new RunesteelCacheModel());
    }
}
