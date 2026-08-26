package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.DungeonSwingingAxeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.cache.object.GeoBone;

public class DungeonSwingingAxeRenderer extends AbstractRunicBlockRenderer<DungeonSwingingAxeBlockEntity> {

    public DungeonSwingingAxeRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DungeonSwingingAxeModel());
    }

    @Override
    public void renderRecursively(PoseStack poseStack, DungeonSwingingAxeBlockEntity animatable, GeoBone bone,
            RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
            boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        if ("gear_assembly".equals(bone.getName())) {
            Level level = animatable.getLevel();
            if (level != null) {
                double gameTick = (level.getGameTime() % DungeonSwingingAxeBlockEntity.FULL_CYCLE) + partialTick;
                float rotZ = (float) Math.toRadians(
                        Math.sin(Math.PI * gameTick / DungeonSwingingAxeBlockEntity.HALF_CYCLE)
                        * DungeonSwingingAxeBlockEntity.MAX_ANGLE_DEG);
                bone.setRotZ(rotZ);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
