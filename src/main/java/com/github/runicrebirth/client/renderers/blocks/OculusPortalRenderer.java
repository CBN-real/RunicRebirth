package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.OculusPortalBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class OculusPortalRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<OculusPortalBlockEntity, R> {

    public OculusPortalRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new OculusPortalModel());
    }
}
