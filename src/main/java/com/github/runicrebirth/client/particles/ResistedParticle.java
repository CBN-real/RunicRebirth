package com.github.runicrebirth.client.particles;

import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SimpleAnimatedParticle;


public class ResistedParticle extends SimpleAnimatedParticle {

    public ResistedParticle(ClientLevel level, double x, double y, double z,
                             SpriteSet sprites, double xd, double yd, double zd, float scale) {
        super(level, x, y, z, sprites, 0.0f);
        this.xd = (random.nextFloat() - 0.5f) * 0.18f;
        this.yd = random.nextFloat() * 0.07f + 0.02f;
        this.zd = (random.nextFloat() - 0.5f) * 0.18f;
        this.scale((0.3f + random.nextFloat() * 0.3f) * scale);
        this.lifetime = 6 + random.nextInt(8);
        this.gravity = -0.002f;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0f - (float) this.age / this.lifetime;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return 15728880;
    }

    public static class Provider implements ParticleProvider<ScaledParticleOption> {
        

        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ScaledParticleOption type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz, net.minecraft.util.RandomSource random) {
            return new ResistedParticle(level, x, y, z, sprites, dx, dy, dz, type.getScale());
        }
    }
}
