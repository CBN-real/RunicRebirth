package com.github.runicrebirth.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TremorBlockParticle extends Particle {

    private static final List<TremorBlockParticle> ACTIVE = new ArrayList<>();

    private final BlockState blockState;
    private final double groundY;

    public TremorBlockParticle(ClientLevel level, double x, double y, double z,
                               BlockState state, Vec3 motion) {
        super(level, x, y, z);
        this.blockState = state;
        this.groundY = y;
        this.xd = motion.x;
        this.yd = motion.y;
        this.zd = motion.z;
        this.lifetime = 40;
        this.gravity = 0.06f;
        this.hasPhysics = false;
        synchronized (ACTIVE) {
            ACTIVE.add(this);
        }
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.yd -= this.gravity;
        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;
        this.xd *= 0.98;
        this.zd *= 0.98;
        if (this.y <= this.groundY && this.yd < 0) {
            if (this.yd > -0.1) {
              this.remove();
              return;
            }
            this.y = this.groundY;
            this.xd *= 0.5;
            this.yd = this.yd / -1.5f;
            this.zd *= 0.5;
        }
    }

    @Override
    public void remove() {
        super.remove();
        synchronized (ACTIVE) {
            ACTIVE.remove(this);
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // No-op: rendered via renderAll
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.NO_RENDER;
    }

    public static void renderAll(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        synchronized (ACTIVE) {
            if (ACTIVE.isEmpty()) return;

            Minecraft mc = Minecraft.getInstance();
            Vec3 camPos = event.getCamera().getPosition();
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);

            for (TremorBlockParticle p : ACTIVE) {
                double x = Mth.lerp(partialTick, p.xo, p.x);
                double y = Mth.lerp(partialTick, p.yo, p.y);
                double z = Mth.lerp(partialTick, p.zo, p.z);

                poseStack.pushPose();
                poseStack.translate(x - camPos.x - 0.35, y - camPos.y, z - camPos.z - 0.35);
                poseStack.scale(1.0f, 1.0f, 1.0f);
                dispatcher.renderSingleBlock(p.blockState, poseStack, bufferSource,
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }

            bufferSource.endBatch();
        }
    }

    public static class Provider implements ParticleProvider<TremorBlockParticleOption> {
        @Override
        public Particle createParticle(TremorBlockParticleOption option, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new TremorBlockParticle(level, x, y, z, option.getState(), option.getMotion());
        }
    }
}
