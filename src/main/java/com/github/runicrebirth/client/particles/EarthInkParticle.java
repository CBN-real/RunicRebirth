package com.github.runicrebirth.client.particles;

import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EarthInkParticle extends InkParticle {

    public EarthInkParticle(ClientLevel level, double x, double y, double z,
                            SpriteSet spriteSet, float scale) {
        super(level, x, y, z, spriteSet, scale);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<ScaledParticleOption> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(ScaledParticleOption type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new EarthInkParticle(level, x, y, z, this.sprites, type.getScale());
        }
    }
}
