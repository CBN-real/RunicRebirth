package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.RunesteelPortcullisBlock;
import com.github.runicrebirth.blocks.entity.RunesteelPortcullisBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.GeoBone;

public class RunesteelPortcullisRenderer extends AbstractRunicBlockRenderer<RunesteelPortcullisBlockEntity> {

    public RunesteelPortcullisRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunesteelPortcullisModel());
    }

    @Override
    public void render(RunesteelPortcullisBlockEntity blockEntity, float partialTick,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction facing = blockEntity.getBlockState().getValue(RunesteelPortcullisBlock.FACING);
        float yRot = switch (facing) {
            case SOUTH -> 180f;
            case WEST  -> 90f;
            case EAST  -> 270f;
            default    -> 0f;
        };

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.translate(-0.5, 0, -0.5);
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, RunesteelPortcullisBlockEntity animatable,
            GeoBone bone, RenderType renderType, MultiBufferSource bufferSource,
            VertexConsumer buffer, boolean isReRender, float partialTick,
            int packedLight, int packedOverlay, int colour) {

        String name = bone.getName();
        if (name.startsWith("runesteel_portcullis_middle")) {
            BlockState state = animatable.getBlockState();
            boolean open = state.getValue(RunesteelPortcullisBlock.OPEN);
            if (open) {
                bone.setHidden(true);
            } else {
                int height = state.getValue(RunesteelPortcullisBlock.HEIGHT);
                String suffix = name.substring("runesteel_portcullis_middle".length());
                try {
                    int idx = Integer.parseInt(suffix);
                    bone.setHidden(idx > height);
                } catch (NumberFormatException ignored) {
                    // bone name without numeric suffix — treat as index 1
                    bone.setHidden(height < 1);
                }
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
