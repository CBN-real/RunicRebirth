package com.github.runicrebirth.client.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class ShockwaveManager {

    private static final List<Shockwave> SHOCKWAVES = new ArrayList<>();

    private record Shockwave(Vec3 origin, float maxRadius, int startTick) {
        boolean isExpired(int tick) { return tick - startTick >= maxRadius * 2; }
    }

    private ShockwaveManager() {}

    public static void addShockwave(Vec3 origin, float maxRadius) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        SHOCKWAVES.add(new Shockwave(origin, maxRadius * 2, mc.player.tickCount));
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        int tick = mc.player.tickCount;
        ClientLevel level = mc.level;

        for (Shockwave wave : SHOCKWAVES) {
            if (wave.isExpired(tick)) continue;
            float progress = (float)(tick - wave.startTick) / (wave.maxRadius * 2f);
            float currentRadius = wave.maxRadius * progress;
            if (currentRadius < 0.5f) continue;

            int particleCount = Math.max(5, (int)(currentRadius * 4));
            for (int i = 0; i < particleCount; i++) {
                double angle = (2.0 * Math.PI * i) / particleCount;
                double x = wave.origin.x + Math.cos(angle) * currentRadius;
                double z = wave.origin.z + Math.sin(angle) * currentRadius;
                level.addParticle(ParticleTypes.EXPLOSION, x, wave.origin.y + 0.1, z, 0, 0.03, 0);
            }
        }

        SHOCKWAVES.removeIf(w -> w.isExpired(tick));
    }

    public static void clear() {
        SHOCKWAVES.clear();
    }
}
