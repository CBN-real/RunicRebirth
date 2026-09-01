package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.AbstractSectBannerBlock;
import com.github.runicrebirth.blocks.entity.SectBannerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class SectBannerRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<SectBannerBlockEntity, R> {

    public SectBannerRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new SectBannerModel());
    }

    protected float getModelBaseY() { return 0f; }

    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        if (Minecraft.getInstance().level == null) return;
        BlockState state = Minecraft.getInstance().level.getBlockState(renderPassInfo.renderState().blockPos);
        applyBannerTransform(renderPassInfo.poseStack(), state, getModelBaseY());
    }

    static void applyBannerTransform(PoseStack poseStack, BlockState state, float baseY) {
        AttachFace face = state.getValue(AbstractSectBannerBlock.FACE);
        Direction facing = state.getValue(AbstractSectBannerBlock.FACING);
        float yDeg = facingToYDeg(facing) + baseY;

        if (face == AttachFace.CEILING) {
            poseStack.translate(0.5, 1.0, 0.5);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(-yDeg));
        } else {
            poseStack.translate(0.5, 0.0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(yDeg));
            poseStack.translate(-0.5, 0.0, -0.5);
        }
    }

    // sect_banner geo has fabric at z≈-2, facing north by default
    static float facingToYDeg(Direction facing) {
        return switch (facing) {
            case NORTH -> 180f;
            case WEST  -> 180f;
            case SOUTH -> 180f;
            case EAST  -> 180f;
            default    -> 180f;
        };
    }
    // TODO GeckoLib 5: banner pattern layers (renderPatternLayers/renderColoredQuad) need to be
    // migrated to postRenderPass() using SubmitNodeCollector.submitCustomGeometry() when
    // the BannerPatternLayers rendering is re-enabled.
}
