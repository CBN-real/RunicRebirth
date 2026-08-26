package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.SectBannerVariantBlock;
import com.github.runicrebirth.blocks.entity.SectBannerVariantBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;

public class SectBannerVariantRenderer extends AbstractRunicBlockRenderer<SectBannerVariantBlockEntity> {

    public SectBannerVariantRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new SectBannerVariantModel());
    }

    @Override
    public void render(SectBannerVariantBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        SectBannerVariantBlock block = (SectBannerVariantBlock) be.getBlockState().getBlock();
        float baseY = block.getBannerType() == SectBannerVariantBlock.BannerType.TATTERED ? 180f : 0f;
        SectBannerRenderer.applyBannerPose(poseStack, be.getBlockState(), baseY);
        super.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SectBannerVariantBlockEntity animatable, GeoBone bone,
            RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
            boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if ("banner_fabric".equals(bone.getName()) && !isReRender) {
            SectBannerRenderer.renderPatternLayers(poseStack, bufferSource, animatable, bone, packedLight, packedOverlay);
        }
    }
}
