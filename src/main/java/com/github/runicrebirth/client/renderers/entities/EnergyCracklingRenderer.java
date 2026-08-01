package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.entities.spells.EnergyCracklingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class EnergyCracklingRenderer extends EntityRenderer<EnergyCracklingEntity> {

    private static final int BASE_BOLT_COUNT = 7;
    private static final float BASE_TICKS_PER_UPDATE = 2f;

    public EnergyCracklingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(EnergyCracklingEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(EnergyCracklingEntity entity, float yaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float radius    = entity.getCrackleRadius();
        float density   = entity.getDensity();
        float speed     = entity.getSpeed();
        float thickness = entity.getThickness();

        int colorRgb = entity.getColor();
        float r = ((colorRgb >> 16) & 0xFF) / 255f;
        float g = ((colorRgb >> 8)  & 0xFF) / 255f;
        float b = (colorRgb         & 0xFF) / 255f;

        int ticksPerUpdate = Math.max(1, Math.round(BASE_TICKS_PER_UPDATE / speed));
        long seed = ((long) (entity.tickCount / ticksPerUpdate)) * 0x9E3779B97F4A7C15L ^ (long) entity.getId();
        RandomSource rand = RandomSource.create(seed);

        int boltCount = Math.max(1, Math.round(BASE_BOLT_COUNT * density));
        float length = entity.getCrackleLength();

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f pose = poseStack.last().pose();

        if (length > 0) {
            float lateralSpread = radius * 0.25f;
            for (int i = 0; i < boltCount; i++) {
                if (rand.nextFloat() < 0.25f) continue;
                float anchorAngle = rand.nextFloat() * Mth.TWO_PI;
                float anchorR = (float) Math.sqrt(rand.nextFloat()) * radius;
                float ax = anchorR * Mth.cos(anchorAngle);
                float az = anchorR * Mth.sin(anchorAngle);
                Vec3 start = new Vec3(
                    ax + (rand.nextDouble() - 0.5) * lateralSpread,
                    (rand.nextDouble() - 0.5) * length,
                    az + (rand.nextDouble() - 0.5) * lateralSpread
                );
                Vec3 end = new Vec3(
                    ax + (rand.nextDouble() - 0.5) * lateralSpread,
                    (rand.nextDouble() - 0.5) * length,
                    az + (rand.nextDouble() - 0.5) * lateralSpread
                );
                drawBoltCylinder(consumer, pose, start, end, r, g, b, rand, thickness, lateralSpread);
            }
        } else {
            for (int i = 0; i < boltCount; i++) {
                if (rand.nextFloat() < 0.25f) continue;
                Vec3 start = randomInSphere(rand, radius);
                Vec3 end   = randomInSphere(rand, radius);
                drawBolt(consumer, pose, start, end, r, g, b, rand, thickness);
            }
        }

        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    private static Vec3 randomInSphere(RandomSource rand, float radius) {
        for (int i = 0; i < 8; i++) {
            float x = (rand.nextFloat() * 2f - 1f) * radius;
            float y = (rand.nextFloat() * 2f - 1f) * radius;
            float z = (rand.nextFloat() * 2f - 1f) * radius;
            if (x * x + y * y + z * z <= radius * radius) return new Vec3(x, y, z);
        }
        float theta = rand.nextFloat() * Mth.TWO_PI;
        float phi   = (float) Math.acos(2f * rand.nextFloat() - 1f);
        return new Vec3(
            radius * Mth.sin(phi) * Mth.cos(theta),
            radius * Mth.cos(phi),
            radius * Mth.sin(phi) * Mth.sin(theta)
        );
    }

    private static void drawBolt(VertexConsumer consumer, Matrix4f pose,
                                  Vec3 start, Vec3 end,
                                  float r, float g, float b, RandomSource rand, float thickness) {
        Vec3 totalDelta = end.subtract(start);
        double length = totalDelta.length();
        if (length < 0.05) return;

        int segments = 3 + rand.nextInt(3);
        Vec3[] points = new Vec3[segments + 1];
        points[0] = start;
        for (int i = 1; i < segments; i++) {
            double t = (double) i / segments;
            Vec3 straight = start.add(totalDelta.scale(t));
            double jitter = length * 0.3;
            points[i] = straight.add(
                (rand.nextDouble() - 0.5) * jitter,
                (rand.nextDouble() - 0.5) * jitter,
                (rand.nextDouble() - 0.5) * jitter
            );
        }
        points[segments] = end;

        for (int i = 0; i < segments; i++) {
            Vec3 from = points[i];
            Vec3 to   = points[i + 1];

            drawTube(consumer, pose, from, to, 0.025f * thickness, 1f, 1f, 1f, 0.9f);
            drawTube(consumer, pose, from, to, 0.055f * thickness, r,  g,  b,  0.4f);
            drawTube(consumer, pose, from, to, 0.11f  * thickness, r,  g,  b,  0.15f);

            if (rand.nextFloat() < 0.5f) {
                Vec3 branch1 = to.add(
                    (rand.nextDouble() - 0.5) * length * 0.4,
                    (rand.nextDouble() - 0.5) * length * 0.4,
                    (rand.nextDouble() - 0.5) * length * 0.4
                );
                drawTube(consumer, pose, to, branch1, 0.015f * thickness, 1f, 1f, 1f, 0.7f);
                drawTube(consumer, pose, to, branch1, 0.035f * thickness, r,  g,  b,  0.3f);

                if (rand.nextFloat() < 0.35f) {
                    Vec3 branch2 = branch1.add(
                        (rand.nextDouble() - 0.5) * length * 0.25,
                        (rand.nextDouble() - 0.5) * length * 0.25,
                        (rand.nextDouble() - 0.5) * length * 0.25
                    );
                    drawTube(consumer, pose, branch1, branch2, 0.008f * thickness, 1f, 1f, 1f, 0.5f);
                    drawTube(consumer, pose, branch1, branch2, 0.02f  * thickness, r,  g,  b,  0.2f);
                }
            }
        }
    }


    private static void drawBoltCylinder(VertexConsumer consumer, Matrix4f pose,
                                          Vec3 start, Vec3 end,
                                          float r, float g, float b, RandomSource rand,
                                          float thickness, float lateralSpread) {
        Vec3 totalDelta = end.subtract(start);
        double boltLen = totalDelta.length();
        if (boltLen < 0.05) return;

        int segments = 3 + rand.nextInt(3);
        Vec3[] points = new Vec3[segments + 1];
        points[0] = start;
        for (int i = 1; i < segments; i++) {
            double t = (double) i / segments;
            Vec3 straight = start.add(totalDelta.scale(t));
            double hJitter = lateralSpread * 0.6;
            double vJitter = boltLen * 0.35;
            points[i] = straight.add(
                (rand.nextDouble() - 0.5) * hJitter,
                (rand.nextDouble() - 0.5) * vJitter,
                (rand.nextDouble() - 0.5) * hJitter
            );
        }
        points[segments] = end;

        for (int i = 0; i < segments; i++) {
            Vec3 from = points[i];
            Vec3 to   = points[i + 1];

            drawTube(consumer, pose, from, to, 0.025f * thickness, 1f, 1f, 1f, 0.9f);
            drawTube(consumer, pose, from, to, 0.055f * thickness, r,  g,  b,  0.4f);
            drawTube(consumer, pose, from, to, 0.11f  * thickness, r,  g,  b,  0.15f);

            if (rand.nextFloat() < 0.5f) {
                Vec3 branch1 = to.add(
                    (rand.nextDouble() - 0.5) * lateralSpread * 0.8,
                    (rand.nextDouble() - 0.5) * boltLen * 0.4,
                    (rand.nextDouble() - 0.5) * lateralSpread * 0.8
                );
                drawTube(consumer, pose, to, branch1, 0.015f * thickness, 1f, 1f, 1f, 0.7f);
                drawTube(consumer, pose, to, branch1, 0.035f * thickness, r,  g,  b,  0.3f);

                if (rand.nextFloat() < 0.35f) {
                    Vec3 branch2 = branch1.add(
                        (rand.nextDouble() - 0.5) * lateralSpread * 0.5,
                        (rand.nextDouble() - 0.5) * boltLen * 0.25,
                        (rand.nextDouble() - 0.5) * lateralSpread * 0.5
                    );
                    drawTube(consumer, pose, branch1, branch2, 0.008f * thickness, 1f, 1f, 1f, 0.5f);
                    drawTube(consumer, pose, branch1, branch2, 0.02f  * thickness, r,  g,  b,  0.2f);
                }
            }
        }
    }

    private static void drawTube(VertexConsumer consumer, Matrix4f pose,
                                  Vec3 from, Vec3 to, float width,
                                  float r, float g, float b, float a) {
        float fx = (float) from.x, fy = (float) from.y, fz = (float) from.z;
        float tx = (float) to.x,   ty = (float) to.y,   tz = (float) to.z;
        float dx = tx - fx, dy = ty - fy, dz = tz - fz;
        if (dx * dx + dy * dy + dz * dz < 1e-8f) return;

        float yRot = (float) Mth.atan2(-dx, dz);
        float h    = width * 0.5f;
        float cosY = Mth.cos(yRot);
        float sinY = Mth.sin(yRot);

        // Left face
        addQuad(consumer, pose,
            -h * cosY + fx, -h + fy, fz - h * sinY,
            -h * cosY + fx,  h + fy, fz - h * sinY,
            -h * cosY + tx,  h + ty, tz - h * sinY,
            -h * cosY + tx, -h + ty, tz - h * sinY,
            r, g, b, a);
        // Right face
        addQuad(consumer, pose,
             h * cosY + tx, -h + ty, tz + h * sinY,
             h * cosY + tx,  h + ty, tz + h * sinY,
             h * cosY + fx,  h + fy, fz + h * sinY,
             h * cosY + fx, -h + fy, fz + h * sinY,
            r, g, b, a);
        // Top face
        addQuad(consumer, pose,
             h * cosY + fx, -h + fy, fz + h * sinY,
            -h * cosY + fx, -h + fy, fz - h * sinY,
            -h * cosY + tx, -h + ty, tz - h * sinY,
             h * cosY + tx, -h + ty, tz + h * sinY,
            r, g, b, a);
        // Bottom face
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

    @Override
    public ResourceLocation getTextureLocation(EnergyCracklingEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
