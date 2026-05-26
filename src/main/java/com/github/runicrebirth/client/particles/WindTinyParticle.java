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

public class WindTinyParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final boolean mirrored;

    public WindTinyParticle(ClientLevel level, double x, double y, double z,
                               SpriteSet spriteSet, double xd, double yd, double zd, float scale) {
        super(level, x, y, z, xd, yd, zd);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.scale((this.random.nextFloat() / 3.0f + 0.1f) * scale);
        this.lifetime = 1 + (int) (Math.random() * 10);
        this.sprites = spriteSet;
        this.setSpriteFromAge(spriteSet);
        this.gravity = 0.000F;
        this.mirrored = this.random.nextBoolean();
    }

    @Override
    public void tick() {
        super.tick();
        this.xd += this.random.nextFloat() / 400.0F * (this.random.nextBoolean() ? 1 : -1);
        this.yd += this.random.nextFloat() / 400.0F * (this.random.nextBoolean() ? 1 : -1);
        this.zd += this.random.nextFloat() / 400.0F * (this.random.nextBoolean() ? 1 : -1);
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

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(ScaledParticleOption type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new WindTinyParticle(level, x, y, z, this.sprites, dx, dy, dz, type.getScale());
        }
    }
}
