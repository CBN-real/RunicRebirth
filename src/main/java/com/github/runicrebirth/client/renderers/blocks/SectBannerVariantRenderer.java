package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.SectBannerVariantBlock;
import com.github.runicrebirth.blocks.entity.SectBannerVariantBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

public class SectBannerVariantRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<SectBannerVariantBlockEntity, R> {

    public SectBannerVariantRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new SectBannerVariantModel());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        if (Minecraft.getInstance().level == null) return;
        BlockState state = Minecraft.getInstance().level.getBlockState(renderPassInfo.renderState().blockPos);
        SectBannerVariantBlock block = (SectBannerVariantBlock) state.getBlock();
        float baseY = block.getBannerType() == SectBannerVariantBlock.BannerType.TATTERED ? 180f : 0f;
        SectBannerRenderer.applyBannerTransform(renderPassInfo.poseStack(), state, baseY);
    }
    // TODO GeckoLib 5: renderRecursively() banner pattern layer rendering needs to be migrated
    // to postRenderPass() using SubmitNodeCollector.submitCustomGeometry().
}
