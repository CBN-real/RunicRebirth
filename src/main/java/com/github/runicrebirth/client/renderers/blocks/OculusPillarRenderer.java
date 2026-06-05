package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.OculusPillarBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class OculusPillarRenderer extends AbstractRunicBlockRenderer<OculusPillarBlockEntity> {

    public OculusPillarRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new OculusPillarModel());
    }

    @Override
    protected int getPackedLight(OculusPillarBlockEntity animatable, int packedLight) {
        return LightTexture.pack(
            Math.max(LightTexture.block(packedLight), 7),
            LightTexture.sky(packedLight)
        );
    }
}
