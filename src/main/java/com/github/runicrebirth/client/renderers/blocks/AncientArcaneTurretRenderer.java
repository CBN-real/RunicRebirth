package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.AncientArcaneTurretBlock;
import com.github.runicrebirth.blocks.entity.AncientArcaneTurretBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class AncientArcaneTurretRenderer extends AbstractRunicBlockRenderer<AncientArcaneTurretBlockEntity> {

    public AncientArcaneTurretRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new AncientArcaneTurretModel());
    }

    @Override
    public void render(AncientArcaneTurretBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        AttachFace face = state.getValue(AncientArcaneTurretBlock.FACE);

        if (face != AttachFace.WALL) {
            super.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
            return;
        }

        Direction facing = state.getValue(AncientArcaneTurretBlock.FACING);
        float yDeg = switch (facing) {
            case NORTH -> 180f;
            case EAST  -> 180f;
            case WEST  -> 180f;
            default    -> 180f; // SOUTH = base wall model orientation
        };

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yDeg));
        poseStack.translate(-0.5, -0.5, -0.5);
        super.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
