package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.OculusControllerBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class OculusControllerRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<OculusControllerBlockEntity, R> {

    public OculusControllerRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new OculusControllerModel());
    }
}
