package com.github.runicrebirth.client.particles;

import com.github.runicrebirth.particle.ScaledParticleOption;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HoverEffectParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    public HoverEffectParticle(ClientLevel level, double x, double y, double z,
                                SpriteSet spriteSet, double xd, double yd, double zd, float scale) {
        super(level, x, y, z, 0, 0, 0);
        this.xd = 0;
        this.yd = -0.02;
        this.zd = 0;
        this.scale((0.5f + this.random.nextFloat() * 0.3f) * scale);
        this.lifetime = 20;
        this.sprites = spriteSet;
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
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Horizontal billboard: 90° around X so the quad lies flat on XZ plane
        Quaternionf q = new Quaternionf()
                .rotateX((float) (Math.PI / 2))
                .rotateY(Mth.lerp(partialTick, this.oRoll, this.roll));

        Vec3 camPos = camera.getPosition();
        float px = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - camPos.x());
        float py = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - camPos.y());
        float pz = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - camPos.z());

        float size = this.getQuadSize(partialTick);

        Vector3f[] corners = {
            new Vector3f(-1, -1, 0),
            new Vector3f(-1,  1, 0),
            new Vector3f( 1,  1, 0),
            new Vector3f( 1, -1, 0)
        };
        for (Vector3f c : corners) {
            q.transform(c);
            c.mul(size);
            c.add(px, py, pz);
        }

        float u0 = this.getU0(), u1 = this.getU1();
        float v0 = this.getV0(), v1 = this.getV1();
        int light = this.getLightColor(partialTick);

        buffer.addVertex(corners[0].x(), corners[0].y(), corners[0].z()).setUv(u1, v1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        buffer.addVertex(corners[1].x(), corners[1].y(), corners[1].z()).setUv(u1, v0).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        buffer.addVertex(corners[2].x(), corners[2].y(), corners[2].z()).setUv(u0, v0).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
        buffer.addVertex(corners[3].x(), corners[3].y(), corners[3].z()).setUv(u0, v1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
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
            return new HoverEffectParticle(level, x, y, z, this.sprites, dx, dy, dz, type.getScale());
        }
    }
}
