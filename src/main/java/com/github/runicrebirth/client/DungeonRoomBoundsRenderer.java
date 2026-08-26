package com.github.runicrebirth.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;

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

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (!showing || currentBounds == null) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        // Inflate slightly to avoid z-fighting with adjacent blocks
        AABB box = currentBounds.inflate(0.002);
        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()),
            box, 0.0f, 1.0f, 0.0f, 0.8f);
        bufferSource.endBatch(RenderType.lines());

        poseStack.popPose();
    }
}
