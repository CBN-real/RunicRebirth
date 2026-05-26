package com.github.runicrebirth.client.effects;

import com.github.runicrebirth.RunicRebirth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class CrackManager {

    private static final int DURATION_TICKS = 200;
    private static final int FADE_TICKS = 60;
    private static final ResourceLocation CRACK_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "textures/block/crack_texture.png");

    private static final List<CrackGroup> CRACKS = new ArrayList<>();

    private record CrackGroup(List<BlockPos> positions, int color, int startTick) {
        float alpha(int currentTick) {
            int elapsed = currentTick - startTick;
            if (elapsed >= DURATION_TICKS) return 0f;
            int fadeStart = DURATION_TICKS - FADE_TICKS;
            if (elapsed >= fadeStart) {
                return 1f - (float) (elapsed - fadeStart) / FADE_TICKS;
            }
            return 1f;
        }

        boolean isExpired(int currentTick) {
            return currentTick - startTick >= DURATION_TICKS;
        }
    }

    private CrackManager() {}

    public static void addCracks(Vec3 center, float radius, int color) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Level level = mc.level;
        List<BlockPos> positions = new ArrayList<>();
        int r = (int) Math.ceil(radius);
        BlockPos centerPos = BlockPos.containing(center);

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist > radius) continue;
                double chance = 1.0 - dist / radius;
                if (chance < 1.0 && level.random.nextFloat() > chance) continue;
                for (int y = 3; y >= -3; y--) {
                    BlockPos check = centerPos.offset(x, y, z);
                    if (!level.getBlockState(check).isAir() && level.getBlockState(check.above()).isAir()) {
                        positions.add(check);
                        break;
                    }
                }
            }
        }

        if (!positions.isEmpty()) {
            CRACKS.add(new CrackGroup(positions, color, mc.player.tickCount));
        }
    }

    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (CRACKS.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        int tick = mc.player.tickCount;

        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        RenderType renderType = RenderType.entityTranslucentEmissive(CRACK_TEXTURE);
        VertexConsumer consumer = bufferSource.getBuffer(renderType);

        int r = (CRACKS.get(0).color >> 16) & 0xFF;
        int g = (CRACKS.get(0).color >> 8) & 0xFF;
        int b = CRACKS.get(0).color & 0xFF;

        for (CrackGroup group : CRACKS) {
            float alpha = group.alpha(tick);
            if (alpha <= 0f) continue;

            int cr = (group.color >> 16) & 0xFF;
            int cg = (group.color >> 8) & 0xFF;
            int cb = group.color & 0xFF;
            int ca = (int) (alpha * 255);

            for (BlockPos pos : group.positions) {
                float x = (float) (pos.getX() - camPos.x);
                float y = (float) (pos.getY() + 1.002f - camPos.y);
                float z = (float) (pos.getZ() - camPos.z);

                poseStack.pushPose();
                poseStack.translate(x, y, z);

                Matrix4f pose = poseStack.last().pose();
                Pose lastPose = poseStack.last();

                consumer.addVertex(pose, 0f, 0f, 0f).setColor(cr, cg, cb, ca)
                    .setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(LightTexture.FULL_BRIGHT).setNormal(lastPose, 0f, 1f, 0f);
                consumer.addVertex(pose, 0f, 0f, 1f).setColor(cr, cg, cb, ca)
                    .setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(LightTexture.FULL_BRIGHT).setNormal(lastPose, 0f, 1f, 0f);
                consumer.addVertex(pose, 1f, 0f, 1f).setColor(cr, cg, cb, ca)
                    .setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(LightTexture.FULL_BRIGHT).setNormal(lastPose, 0f, 1f, 0f);
                consumer.addVertex(pose, 1f, 0f, 0f).setColor(cr, cg, cb, ca)
                    .setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(LightTexture.FULL_BRIGHT).setNormal(lastPose, 0f, 1f, 0f);

                poseStack.popPose();
            }
        }

        bufferSource.endBatch(renderType);
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        int tick = mc.player.tickCount;
        CRACKS.removeIf(g -> g.isExpired(tick));
    }

    public static void clear() {
        CRACKS.clear();
    }
}
