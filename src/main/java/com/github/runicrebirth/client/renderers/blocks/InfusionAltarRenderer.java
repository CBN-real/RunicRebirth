package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.InfusionAltarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.geckolib.renderer.base.GeoRenderState;

public class InfusionAltarRenderer<R extends BlockEntityRenderState & GeoRenderState>
        extends AbstractRunicBlockRenderer<InfusionAltarBlockEntity, R> {

    public InfusionAltarRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new InfusionAltarModel());
    }

    // TODO GeckoLib 5: floating item rendering (was in render() after super.render()) must move to
    // postRenderPass(RenderPassInfo<R>, SubmitNodeCollector). Requires:
    //   1. Custom InfusionAltarRenderState extends BlockEntityRenderState & GeoRenderState
    //      capturing hasResult, items list, craftingStartTime, gameTime in extractRenderState().
    //   2. Override postRenderPass() to call renderResultItem / renderFloatingItems using renderState data.
    // The private helpers below are preserved for reference during migration.

    private static final float LERP_TICKS = 20.0f;

    private void renderFloatingItems(InfusionAltarBlockEntity entity, float partialTick,
                                     PoseStack poseStack, int packedLight, int packedOverlay) {
        NonNullList<ItemStack> items = entity.getItems();
        int count = entity.getItemCount();
        if (count == 0) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        float gameTime = mc.level.getGameTime() + partialTick;
        float t = 0.0f;
        if (entity.isCrafting()) {
            float elapsed = (mc.level.getGameTime() - entity.getCraftingStartTime()) + partialTick;
            t = Math.min(elapsed / LERP_TICKS, 1.0f);
        }

        float radius = 0.7f * (1.0f - t);
        float scale = 0.4f * (1.0f - t);
        if (scale < 0.01f) return;

        int rendered = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) continue;

            float angle = (gameTime * 0.05f) + (rendered * (2.0f * (float) Math.PI / count));
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            float bob = (float) Math.sin(gameTime * 0.08f + rendered * 1.3f) * 0.06f * (1.0f - t);

            poseStack.pushPose();
            poseStack.translate(0.5f + x, 2.25f + bob, 0.5f + z);
            poseStack.mulPose(Axis.YP.rotationDegrees(gameTime * 2.0f + rendered * 45.0f));
            poseStack.scale(scale, scale, scale);

            // TODO GeckoLib 5: replace getItemRenderer().renderStatic() with SubmitNodeCollector submission
            poseStack.popPose();
            rendered++;
        }
    }

    private void renderResultItem(InfusionAltarBlockEntity entity, float partialTick,
                                  PoseStack poseStack, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        float gameTime = mc.level.getGameTime() + partialTick;
        float bob = (float) Math.sin(gameTime * 0.08f) * 0.06f;

        poseStack.pushPose();
        poseStack.translate(0.5f, 2.25f + bob, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(gameTime * 1.5f));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        // TODO GeckoLib 5: replace getItemRenderer().renderStatic() with SubmitNodeCollector submission
        poseStack.popPose();
    }
}
