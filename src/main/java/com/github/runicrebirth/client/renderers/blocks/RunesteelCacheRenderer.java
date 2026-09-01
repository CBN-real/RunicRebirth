package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.RunesteelCacheBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class RunesteelCacheRenderer<R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<RunesteelCacheBlockEntity, R> {

    public RunesteelCacheRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunesteelCacheModel());
    }
}
