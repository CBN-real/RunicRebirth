package com.github.runicrebirth.client.particles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class InkParticle extends TextureSheetParticle {

    private static final List<InkParticle> ACTIVE = new ArrayList<>();

    private final SpriteSet sprites;

    protected InkParticle(ClientLevel level, double x, double y, double z,
                          SpriteSet spriteSet, float scale) {
        super(level, x, y, z, 0, 0, 0);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = 6000;
        this.sprites = spriteSet;
        this.setSpriteFromAge(spriteSet);
        this.gravity = 0f;
        this.quadSize = (this.random.nextFloat() * 0.1F + 0.5F) * 0.043F * scale;
        ACTIVE.add(this);
    }

    @Override
    public void tick() {
        super.tick();
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.setSprite(this.sprites.get(this.age % 18, 15));
    }

    @Override
    public void remove() {
        super.remove();
        ACTIVE.remove(this);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public static void removeAll() {
        for (InkParticle p : new ArrayList<>(ACTIVE)) {
            p.remove();
        }
        ACTIVE.clear();
    }
}
