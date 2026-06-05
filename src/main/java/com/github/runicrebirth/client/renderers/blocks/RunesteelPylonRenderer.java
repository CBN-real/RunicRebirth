package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.RunesteelPylonBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RunesteelPylonRenderer extends AbstractRunicBlockRenderer<RunesteelPylonBlockEntity> {

    public RunesteelPylonRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunesteelPylonModel());
    }

    @Override
    protected int getPackedLight(RunesteelPylonBlockEntity animatable, int packedLight) {
        return LightTexture.pack(
            Math.max(LightTexture.block(packedLight), 7),
            LightTexture.sky(packedLight)
        );
    }
}
