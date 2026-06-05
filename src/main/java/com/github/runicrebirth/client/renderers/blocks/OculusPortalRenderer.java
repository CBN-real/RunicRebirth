package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.OculusPortalBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class OculusPortalRenderer extends AbstractRunicBlockRenderer<OculusPortalBlockEntity> {

    public OculusPortalRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new OculusPortalModel());
    }
}
