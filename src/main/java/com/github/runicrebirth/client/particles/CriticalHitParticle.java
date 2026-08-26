package com.github.runicrebirth.client.particles;

import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class CriticalHitParticle extends TextureSheetParticle {

    public CriticalHitParticle(ClientLevel level, double x, double y, double z,
                                SpriteSet sprites, double xd, double yd, double zd, float scale) {
        super(level, x, y, z, xd, yd, zd);
        this.xd = (random.nextFloat() - 0.5f) * 0.18f;
        this.yd = random.nextFloat() * 0.07f + 0.02f;
        this.zd = (random.nextFloat() - 0.5f) * 0.18f;
        this.scale((0.15f + random.nextFloat() * 0.3f) * scale);
        this.lifetime = 6 + random.nextInt(8);
        this.gravity = 0.04f;
        this.roll = random.nextFloat() * (float) Math.PI * 2;
        this.oRoll = this.roll;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0f - (float) this.age / this.lifetime;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<ScaledParticleOption> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ScaledParticleOption type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new CriticalHitParticle(level, x, y, z, sprites, dx, dy, dz, type.getScale());
        }
    }
}
