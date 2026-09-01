package com.github.runicrebirth.client.particles;

import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;


public class FireElementParticle extends SimpleAnimatedParticle {

    
    private final boolean mirrored;

    public FireElementParticle(ClientLevel level, double x, double y, double z,
                               SpriteSet spriteSet, double xd, double yd, double zd, float scale) {
        super(level, x, y, z, spriteSet, 0.0f);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.scale((this.random.nextFloat() + 0.25f) * scale);
        this.lifetime = 10 + (int) (Math.random() * 10);
        this.setSpriteFromAge(spriteSet);
        this.gravity = -0.015F;
        this.mirrored = this.random.nextBoolean();
    }

    @Override
    public void tick() {
        super.tick();
        this.xd += this.random.nextFloat() / 500.0F * (this.random.nextBoolean() ? 1 : -1);
        this.yd += this.random.nextFloat() / 500.0F * (this.random.nextBoolean() ? 1 : -1);
        this.zd += this.random.nextFloat() / 500.0F * (this.random.nextBoolean() ? 1 : -1);
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    protected float getU0() {
        return mirrored ? super.getU1() : super.getU0();
    }

    @Override
    protected float getU1() {
        return mirrored ? super.getU0() : super.getU1();
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

        // TODO: Verify ParticleProvider.createParticle signature in 26.1.2 — may need RandomSource parameter
        @Override
        public Particle createParticle(ScaledParticleOption type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz, net.minecraft.util.RandomSource random) {
            return new FireElementParticle(level, x, y, z, this.sprites, dx, dy, dz, type.getScale());
        }
    }
}
