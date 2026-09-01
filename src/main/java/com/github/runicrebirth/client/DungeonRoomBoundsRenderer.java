package com.github.runicrebirth.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class DungeonRoomBoundsRenderer {

    public static boolean showing = false;
    @Nullable
    public static AABB currentBounds = null;

    public static void showBounds(AABB bounds) {
        currentBounds = bounds;
        showing = true;
    }

    public static void hideBounds() {
        currentBounds = null;
        showing = false;
    }

    public static void onRenderLevelStage(RenderLevelStageEvent.AfterTranslucentFeatures event) {
        if (!showing || currentBounds == null) return;

        Minecraft mc = Minecraft.getInstance();
        Vec3 camPos = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack poseStack = event.getPoseStack();

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        AABB box = currentBounds.inflate(0.002);
        VertexConsumer consumer = bufferSource.getBuffer(RenderTypes.lines());
        Matrix4f mat = poseStack.last().pose();

        float x0 = (float) box.minX, y0 = (float) box.minY, z0 = (float) box.minZ;
        float x1 = (float) box.maxX, y1 = (float) box.maxY, z1 = (float) box.maxZ;

        // Bottom face
        addLine(consumer, mat, x0, y0, z0, x1, y0, z0);
        addLine(consumer, mat, x1, y0, z0, x1, y0, z1);
        addLine(consumer, mat, x1, y0, z1, x0, y0, z1);
        addLine(consumer, mat, x0, y0, z1, x0, y0, z0);
        // Top face
        addLine(consumer, mat, x0, y1, z0, x1, y1, z0);
        addLine(consumer, mat, x1, y1, z0, x1, y1, z1);
        addLine(consumer, mat, x1, y1, z1, x0, y1, z1);
        addLine(consumer, mat, x0, y1, z1, x0, y1, z0);
        // Vertical edges
        addLine(consumer, mat, x0, y0, z0, x0, y1, z0);
        addLine(consumer, mat, x1, y0, z0, x1, y1, z0);
        addLine(consumer, mat, x1, y0, z1, x1, y1, z1);
        addLine(consumer, mat, x0, y0, z1, x0, y1, z1);

        bufferSource.endBatch(RenderTypes.lines());
        poseStack.popPose();
    }

    private static void addLine(VertexConsumer consumer, Matrix4f mat,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2) {
        consumer.addVertex(mat, x1, y1, z1).setColor(0f, 1f, 0f, 0.8f);
        consumer.addVertex(mat, x2, y2, z2).setColor(0f, 1f, 0f, 0.8f);
    }
}
