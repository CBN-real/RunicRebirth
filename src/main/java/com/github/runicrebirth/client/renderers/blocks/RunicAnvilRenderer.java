package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.RunicAnvilBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RunicAnvilRenderer<R extends BlockEntityRenderState & GeoRenderState> extends AbstractRunicBlockRenderer<RunicAnvilBlockEntity, R> {

    private final ItemModelResolver itemModelResolver;

    public RunicAnvilRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new RunicAnvilModel());
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public void postRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.postRenderPass(renderPassInfo, renderTasks);

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        BlockPos pos = renderPassInfo.renderState().blockPos;
        if (!(level.getBlockEntity(pos) instanceof RunicAnvilBlockEntity entity)) return;

        PoseStack poseStack = renderPassInfo.poseStack();
        float partialTick = renderPassInfo.renderState().getPartialTick();
        int packedLight = renderPassInfo.packedLight();
        int packedOverlay = renderPassInfo.packedOverlay();

        if (entity.hasResult()) {
            renderResultItem(entity, level, partialTick, poseStack, renderTasks, packedLight, packedOverlay);
        } else if (entity.getItemCount() > 0) {
            renderFloatingItems(entity, level, partialTick, poseStack, renderTasks, packedLight, packedOverlay);
        }
    }

    private void renderFloatingItems(RunicAnvilBlockEntity entity, Level level, float partialTick,
                                     PoseStack poseStack, SubmitNodeCollector renderTasks,
                                     int packedLight, int packedOverlay) {
        NonNullList<ItemStack> items = entity.getItems();
        int count = entity.getItemCount();
        if (count == 0) return;

        float gameTime = level.getGameTime() + partialTick;
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

            submitItem(items.get(i), level, poseStack, renderTasks, packedLight, packedOverlay, rendered);

            poseStack.popPose();
            rendered++;
        }
    }

    private void renderResultItem(RunicAnvilBlockEntity entity, Level level, float partialTick,
                                  PoseStack poseStack, SubmitNodeCollector renderTasks,
                                  int packedLight, int packedOverlay) {
        float gameTime = level.getGameTime() + partialTick;
        float bob = (float) Math.sin(gameTime * 0.08f) * 0.06f;

        poseStack.pushPose();
        poseStack.translate(0.5f, 1.25f + bob, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(gameTime * 1.5f));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        submitItem(entity.getResultItem(), level, poseStack, renderTasks, packedLight, packedOverlay, 0);

        poseStack.popPose();
    }

    private void submitItem(ItemStack stack, Level level, PoseStack poseStack, SubmitNodeCollector renderTasks,
                            int packedLight, int packedOverlay, int seed) {
        ItemStackRenderState renderState = new ItemStackRenderState();
        itemModelResolver.updateForTopItem(renderState, stack, ItemDisplayContext.GROUND, level, null, seed);
        renderState.submit(poseStack, renderTasks, packedLight, packedOverlay, 0);
    }
}
