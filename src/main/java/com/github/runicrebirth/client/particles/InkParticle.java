package com.github.runicrebirth.client.particles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SimpleAnimatedParticle;


public abstract class InkParticle extends SimpleAnimatedParticle {

    private static final List<InkParticle> ACTIVE = new ArrayList<>();

    

    protected InkParticle(ClientLevel level, double x, double y, double z,
                          SpriteSet spriteSet, float scale) {
        super(level, x, y, z, spriteSet, 0.0f);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.lifetime = 6000;
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
    public int getLightCoords(float partialTick) {
        return 15728880;
    }

    public static void removeAll() {
        for (InkParticle p : new ArrayList<>(ACTIVE)) {
            p.remove();
        }
        ACTIVE.clear();
    }
}
