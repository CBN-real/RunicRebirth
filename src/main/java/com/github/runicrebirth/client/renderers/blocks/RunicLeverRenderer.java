package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.RunicLeverBlock;
import com.github.runicrebirth.blocks.entity.RunicLeverBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class RunicLeverRenderer extends AbstractRunicBlockRenderer<RunicLeverBlockEntity> {

    public RunicLeverRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunicLeverModel());
    }

    @Override
    public void render(RunicLeverBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        Direction facing = state.getValue(RunicLeverBlock.FACING);
        

        float yDeg = switch (facing) {
            case NORTH -> 0f;
            case SOUTH -> 0f;
            case WEST  -> 0f;
            case EAST  -> 0f;
            default    -> 0f;
        };

        if (yDeg == 0f) {
            super.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yDeg));
        poseStack.translate(-0.5, -0.5, -0.5);
        super.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
