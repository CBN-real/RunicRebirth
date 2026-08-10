package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.CrumblingPlatformBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrumblingPlatformRenderer implements BlockEntityRenderer<CrumblingPlatformBlockEntity> {

    public CrumblingPlatformRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(CrumblingPlatformBlockEntity be, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        BlockState mimickedState = be.getMimickedState();
        if (mimickedState == null || mimickedState.isAir()) return;
        Minecraft.getInstance().getBlockRenderer()
                .renderSingleBlock(mimickedState, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
