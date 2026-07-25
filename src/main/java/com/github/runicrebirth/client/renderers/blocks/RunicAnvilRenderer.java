package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.RunicAnvilBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RunicAnvilRenderer extends AbstractRunicBlockRenderer<RunicAnvilBlockEntity> {

    public RunicAnvilRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunicAnvilModel());
    }

    @Override
    public void render(RunicAnvilBlockEntity entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(entity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

        if (entity.hasResult()) {
            renderResultItem(entity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        } else if (entity.getItemCount() > 0) {
            renderFloatingItems(entity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private void renderFloatingItems(RunicAnvilBlockEntity entity, float partialTick,
                                     PoseStack poseStack, MultiBufferSource bufferSource,
                                     int packedLight, int packedOverlay) {
        NonNullList<ItemStack> items = entity.getItems();
        int count = entity.getItemCount();
        if (count == 0) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        float gameTime = mc.level.getGameTime() + partialTick;


        float scale = 1.0f;

        int rendered = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) continue;

            float bob = (float) Math.sin(gameTime * 0.08f + rendered * 1.3f) * 0.06f;

            poseStack.pushPose();
            poseStack.translate(0.5f, 1.25f + bob, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(gameTime * 2.0f + rendered * 45.0f));
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.scale(scale, scale, scale);

            mc.getItemRenderer().renderStatic(
                    items.get(i), ItemDisplayContext.GROUND, packedLight, packedOverlay,
                    poseStack, bufferSource, mc.level, 0);

            poseStack.popPose();
            rendered++;
        }
    }

    private void renderResultItem(RunicAnvilBlockEntity entity, float partialTick,
                                  PoseStack poseStack, MultiBufferSource bufferSource,
                                  int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        float gameTime = mc.level.getGameTime() + partialTick;
        float bob = (float) Math.sin(gameTime * 0.08f) * 0.06f;

        poseStack.pushPose();
        poseStack.translate(0.5f, 1.25f + bob, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(gameTime * 1.5f));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        mc.getItemRenderer().renderStatic(
                entity.getResultItem(), ItemDisplayContext.GROUND, packedLight, packedOverlay,
                poseStack, bufferSource, mc.level, 0);

        poseStack.popPose();
    }
}
