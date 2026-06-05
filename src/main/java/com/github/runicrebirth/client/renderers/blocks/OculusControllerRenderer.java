package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.OculusControllerBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class OculusControllerRenderer extends AbstractRunicBlockRenderer<OculusControllerBlockEntity> {

    public OculusControllerRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new OculusControllerModel());
    }
}
