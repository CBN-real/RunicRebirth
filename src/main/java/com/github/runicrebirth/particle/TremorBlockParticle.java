package com.github.runicrebirth.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

// TODO 26.1.2: Migrate from TextureSheetParticle to ParticleGroup<TremorBlockParticle>
// TODO 26.1.2: Create ParticleGroupRenderState that calls submitBlock() on SubmitNodeCollector
// TODO 26.1.2: Replace renderAll() with group extraction pattern
// TODO 26.1.2: Particle#render() → absorbed into ParticleGroupRenderState#submit()
public class TremorBlockParticle extends Particle {

    private static final List<TremorBlockParticle> ACTIVE = new ArrayList<>();

    private final BlockState blockState;
    private final double groundY;
    final float scale;

    public TremorBlockParticle(ClientLevel level, double x, double y, double z,
                               BlockState state, Vec3 motion, float scale) {
        super(level, x, y, z);
        this.blockState = state;
        this.groundY = y;
        this.scale = scale;
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

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // No-op: rendered via renderAll
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }

    public static void renderAll(RenderLevelStageEvent.AfterTranslucentParticles event) {
        // TODO 26.1.2: BlockRenderDispatcher.renderSingleBlock removed; needs full submit-pipeline port
        // Particles still simulate (tick/bounce) but are invisible until this is properly ported.
    }

    public static class Provider implements ParticleProvider<TremorBlockParticleOption> {
        @Override
        public Particle createParticle(TremorBlockParticleOption option, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new TremorBlockParticle(level, x, y, z, option.getState(), option.getMotion(), option.getScale());
        }
    }
}
