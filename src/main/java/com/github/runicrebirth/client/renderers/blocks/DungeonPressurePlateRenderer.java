package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.DungeonPressurePlateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class DungeonPressurePlateRenderer implements BlockEntityRenderer<DungeonPressurePlateBlockEntity> {

    public DungeonPressurePlateRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(DungeonPressurePlateBlockEntity be, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        BlockState mimickedState = be.getMimickedState();
        if (mimickedState == null || mimickedState.isAir()) return;

        Minecraft mc = Minecraft.getInstance();
        BakedModel model = mc.getBlockRenderer().getBlockModelShaper().getBlockModel(mimickedState);
        TextureAtlasSprite sprite = model.getParticleIcon();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutoutMipped());
        PoseStack.Pose pose = poseStack.last();
        Matrix4f mat = pose.pose();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        float y = 0.0625f;

        consumer.addVertex(mat, 0, y, 0).setColor(255, 255, 255, 255)
                .setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        consumer.addVertex(mat, 0, y, 1).setColor(255, 255, 255, 255)
                .setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        consumer.addVertex(mat, 1, y, 1).setColor(255, 255, 255, 255)
                .setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        consumer.addVertex(mat, 1, y, 0).setColor(255, 255, 255, 255)
                .setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, 1, 0);

        consumer.addVertex(mat, 1, y, 0).setColor(255, 255, 255, 255)
                .setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, -1, 0);
        consumer.addVertex(mat, 1, y, 1).setColor(255, 255, 255, 255)
                .setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, -1, 0);
        consumer.addVertex(mat, 0, y, 1).setColor(255, 255, 255, 255)
                .setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, -1, 0);
        consumer.addVertex(mat, 0, y, 0).setColor(255, 255, 255, 255)
                .setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, -1, 0);
    }
}
