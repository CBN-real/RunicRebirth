package com.github.runicrebirth.client.particles;

import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;

public class HoverEffectParticle extends SimpleAnimatedParticle {

    

    public HoverEffectParticle(ClientLevel level, double x, double y, double z,
                                SpriteSet spriteSet, double xd, double yd, double zd, float scale) {
        super(level, x, y, z, spriteSet, 0.0f);
        this.xd = 0;
        this.yd = -0.02;
        this.zd = 0;
        this.scale((0.5f + this.random.nextFloat() * 0.3f) * scale);
        this.lifetime = 20;
        this.setSpriteFromAge(spriteSet);
        this.gravity = 0.01f;
        this.roll = this.random.nextFloat() * (float) Math.PI * 2;
        this.oRoll = this.roll;
        this.alpha = 0.9f;
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
        this.move(this.xd, this.yd, this.zd);
        this.alpha = 0.9f * (1.0f - (float) this.age / this.lifetime);
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void extract(QuadParticleRenderState state, Camera camera, float partialTick) {
        // Horizontal billboard: 90° around X so the quad lies flat on XZ plane
        Quaternionf q = new Quaternionf()
                .rotateX((float) (Math.PI / 2))
                .rotateY(Mth.lerp(partialTick, this.oRoll, this.roll));
        extractRotatedQuad(state, camera, q, partialTick);
    }

    @Override
    public int getLightCoords(float partialTick) {
        return 15728880;
    }

    public static class Provider implements ParticleProvider<ScaledParticleOption> {
        

        private final SpriteSet sprites;
        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(ScaledParticleOption type, ClientLevel level,
                                        double x, double y, double z,
                                        double dx, double dy, double dz, RandomSource random) {
            return new HoverEffectParticle(level, x, y, z, this.sprites, dx, dy, dz, type.getScale());
        }
    }
}
