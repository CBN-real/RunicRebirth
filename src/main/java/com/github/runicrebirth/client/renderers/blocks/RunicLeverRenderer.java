package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.RunicLeverBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class RunicLeverRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<RunicLeverBlockEntity, R> {

    public RunicLeverRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunicLeverModel());
    }
}
