package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.entities.spells.ArcaneTetherEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ArcaneTetherRenderer extends EntityRenderer<ArcaneTetherEntity, ArcaneTetherRenderer.State> {

    private static final float R = 0xAE / 255f;
    private static final float G = 0x78 / 255f;
    private static final float B = 0xFF / 255f;

    private static final int ROPE_SEGMENTS = 12;
    private static final float WOBBLE_AMP   = 0.06f;
    private static final float WOBBLE_SPEED = 1.4f;

    public static class State extends EntityRenderState {
        public int ownerEntityId = -1;
        public float ownerRelX, ownerRelY, ownerRelZ;
        public int entityId;
        public int tickCount;
        public float partialTick;
    }

    public ArcaneTetherRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ArcaneTetherEntity entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.ownerEntityId = entity.getOwnerEntityId();
        state.entityId = entity.getId();
        state.tickCount = entity.tickCount;
        state.partialTick = partialTicks;
        if (state.ownerEntityId != -1) {
            Entity owner = entity.level().getEntity(state.ownerEntityId);
            if (owner != null) {
                double entityX = Mth.lerp(partialTicks, entity.xOld, entity.getX());
                double entityY = Mth.lerp(partialTicks, entity.yOld, entity.getY());
                double entityZ = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
                double ownerX = Mth.lerp(partialTicks, owner.xOld, owner.getX());
                double ownerY = Mth.lerp(partialTicks, owner.yOld, owner.getY()) + owner.getEyeHeight() * 0.6;
                double ownerZ = Mth.lerp(partialTicks, owner.zOld, owner.getZ());
                state.ownerRelX = (float)(ownerX - entityX);
                state.ownerRelY = (float)(ownerY - entityY);
                state.ownerRelZ = (float)(ownerZ - entityZ);
            } else {
                state.ownerEntityId = -1;
            }
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.ownerEntityId == -1) {
            super.submit(state, poseStack, submitNodeCollector, camera);
            return;
        }
        float dx = state.ownerRelX, dy = state.ownerRelY, dz = state.ownerRelZ;
        if (dx * dx + dy * dy + dz * dz < 0.01f) {
            super.submit(state, poseStack, submitNodeCollector, camera);
            return;
        }
        float time = (state.tickCount + state.partialTick) / 20.0f;
        int entityId = state.entityId;
        Vec3 anchorEnd = new Vec3(0, 0.5, 0);
        Vec3 ownerEnd = new Vec3(dx, dy, dz);
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lightning(), (pose, consumer) ->
            drawRopeBolt(consumer, pose.pose(), anchorEnd, ownerEnd, time, entityId));
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public boolean shouldRender(ArcaneTetherEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    private static void drawRopeBolt(VertexConsumer consumer, Matrix4f pose,
                                      Vec3 from, Vec3 to, float time, int entityId) {
        Vec3 delta = to.subtract(from);
        Vec3 dir = delta.normalize();
        Vec3 up = Math.abs(dir.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 perp1 = dir.cross(up).normalize();
        Vec3 perp2 = dir.cross(perp1).normalize();

        Vec3[] points = new Vec3[ROPE_SEGMENTS + 1];
        points[0] = from;
        for (int i = 1; i < ROPE_SEGMENTS; i++) {
            double t = (double) i / ROPE_SEGMENTS;
            double envelope = Math.sin(t * Math.PI);
            float phase1 = time * WOBBLE_SPEED + (float) (i * 0.7 + entityId * 0.31);
            float phase2 = time * WOBBLE_SPEED + (float) (i * 1.3 + entityId * 0.17) + 1.2f;
            double w1 = WOBBLE_AMP * envelope * Math.sin(phase1);
            double w2 = WOBBLE_AMP * envelope * Math.cos(phase2);
            Vec3 straight = from.add(delta.scale(t));
            points[i] = straight.add(perp1.scale(w1)).add(perp2.scale(w2));
        }
        points[ROPE_SEGMENTS] = to;

        for (int i = 0; i < ROPE_SEGMENTS; i++) {
            Vec3 segFrom = points[i];
            Vec3 segTo   = points[i + 1];
            drawTube(consumer, pose, segFrom, segTo, 0.018f, 1f, 1f, 1f, 0.9f);
            drawTube(consumer, pose, segFrom, segTo, 0.04f,  R,  G,  B, 0.5f);
            drawTube(consumer, pose, segFrom, segTo, 0.09f,  R,  G,  B, 0.2f);
        }
    }

    private static void drawTube(VertexConsumer consumer, Matrix4f pose,
                                  Vec3 from, Vec3 to, float width,
                                  float r, float g, float b, float a) {
        float fx = (float) from.x, fy = (float) from.y, fz = (float) from.z;
        float tx = (float) to.x,   ty = (float) to.y,   tz = (float) to.z;
        float ddx = tx - fx, ddy = ty - fy, ddz = tz - fz;
        if (ddx * ddx + ddy * ddy + ddz * ddz < 1e-8f) return;

        float yRot = (float) Mth.atan2(-ddx, ddz);
        float h    = width * 0.5f;
        float cosY = Mth.cos(yRot);
        float sinY = Mth.sin(yRot);

        addQuad(consumer, pose,
            -h * cosY + fx, -h + fy, fz - h * sinY,
            -h * cosY + fx,  h + fy, fz - h * sinY,
            -h * cosY + tx,  h + ty, tz - h * sinY,
            -h * cosY + tx, -h + ty, tz - h * sinY,
            r, g, b, a);
        addQuad(consumer, pose,
             h * cosY + tx, -h + ty, tz + h * sinY,
             h * cosY + tx,  h + ty, tz + h * sinY,
             h * cosY + fx,  h + fy, fz + h * sinY,
             h * cosY + fx, -h + fy, fz + h * sinY,
            r, g, b, a);
        addQuad(consumer, pose,
             h * cosY + fx, -h + fy, fz + h * sinY,
            -h * cosY + fx, -h + fy, fz - h * sinY,
            -h * cosY + tx, -h + ty, tz - h * sinY,
             h * cosY + tx, -h + ty, tz + h * sinY,
            r, g, b, a);
        addQuad(consumer, pose,
             h * cosY + tx,  h + ty, tz + h * sinY,
            -h * cosY + tx,  h + ty, tz - h * sinY,
            -h * cosY + fx,  h + fy, fz - h * sinY,
             h * cosY + fx,  h + fy, fz + h * sinY,
            r, g, b, a);
    }

    private static void addQuad(VertexConsumer consumer, Matrix4f pose,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  float x3, float y3, float z3,
                                  float x4, float y4, float z4,
                                  float r, float g, float b, float a) {
        consumer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a);
        consumer.addVertex(pose, x2, y2, z2).setColor(r, g, b, a);
        consumer.addVertex(pose, x3, y3, z3).setColor(r, g, b, a);
        consumer.addVertex(pose, x4, y4, z4).setColor(r, g, b, a);
    }
}
