package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.DungeonDoorBlock;
import com.github.runicrebirth.blocks.entity.DungeonDoorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class DungeonDoorRenderer extends AbstractRunicBlockRenderer<DungeonDoorBlockEntity> {

    public DungeonDoorRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DungeonDoorModel());
    }

    @Override
    public void preRender(PoseStack poseStack, DungeonDoorBlockEntity animatable, BakedGeoModel model,
            @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
            boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if (!isReRender) {
            Direction facing = animatable.getBlockState().getValue(DungeonDoorBlock.FACING);
            float yRot = switch (facing) {
                case SOUTH -> 0f;
                case WEST  -> 90f;
                case EAST  -> 270f;
                default    -> 180f;
            };
            poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
            rotateBlock(facing, poseStack);
        }
    }
}
