package com.github.runicrebirth.client.particles;

import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class FireInkParticle extends InkParticle {

    public FireInkParticle(ClientLevel level, double x, double y, double z,
                           SpriteSet spriteSet, float scale) {
        super(level, x, y, z, spriteSet, scale);
    }

    public static class Provider implements ParticleProvider<ScaledParticleOption> {
        

        private final SpriteSet sprites;
        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(ScaledParticleOption type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz, net.minecraft.util.RandomSource random) {
            return new FireInkParticle(level, x, y, z, this.sprites, type.getScale());
        }
    }
}
