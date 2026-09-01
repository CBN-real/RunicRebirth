package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.RunelightWallTorchBlock;
import com.github.runicrebirth.blocks.entity.RunelightTorchBlockEntity;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class RunelightTorchRenderer<R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<RunelightTorchBlockEntity, R> {

    public RunelightTorchRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunelightTorchModel());
    }

    @Override
    public AABB getRenderBoundingBox(RunelightTorchBlockEntity blockEntity) {
        return AABB.INFINITE;
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        if (Minecraft.getInstance().level == null) return;
        BlockState state = Minecraft.getInstance().level.getBlockState(renderPassInfo.renderState().blockPos);
        if (state.hasProperty(RunelightWallTorchBlock.FACING)) {
            applyWallTransform(renderPassInfo.poseStack(), state.getValue(RunelightWallTorchBlock.FACING));
        }
    }

    // Rotates and tilts the model to lean against the given wall face.
    private void applyWallTransform(com.mojang.blaze3d.vertex.PoseStack poseStack, Direction facing) {
        float yRot = switch (facing) {
            case NORTH -> 0f;
            case SOUTH -> 180f;
            case WEST  -> 90f;
            case EAST  -> 270f;
            default    -> 0f;
        };
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.translate(0.0, 0.0, -0.5 + (2.0 / 16.0));
        poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
        poseStack.translate(-0.5, -0.1, 0.39);
    }
    // TODO GeckoLib 5: preRender() bone-hiding logic needs to move to addRenderData() / adjustModelBonesForRender().
    // Original body preserved for reference:
    // for (int i = 1; i <=8; i++) {
    //     Optional<GeoBone> bone = model.getBone("rune_" + i + "_s1");
    //     if (i != animatable.getSelectedRune()) {
    //         bone.ifPresent(geoBone -> geoBone.setHidden(true));
    //     } else {
    //         bone.ifPresent(geoBone -> geoBone.setHidden(false));
    //     }
    // }
}
