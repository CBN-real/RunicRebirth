package com.github.runicrebirth.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ParticleHelper {

    public static void trailParticleEvent(Level level, ParticleOptions particle, Vec3 position, Vec3 velocity, float scale) {
        double length = velocity.length();
        int count = (int) Math.min(20, Math.round(length) * 2) + 1;
        float step = (float) length / count;
        for (int i = 0; i < count; i++) {
            Vec3 rand = Utils.randVec3(0.025 * scale);
            Vec3 offset = velocity.scale(step * i);
            level.addParticle(particle,
                position.x + rand.x + offset.x,
                position.y + rand.y + offset.y,
                position.z + rand.z + offset.z,
                rand.x, rand.y, rand.z);
        }
    }

    public static void areaParticleEvent(Level level, ParticleOptions particle, Vec3 center, double radius, int count, float scale) {
        double r = radius * scale;
        for (int i = 0; i < count; i++) {
            double theta = level.getRandom().nextDouble() * Math.PI * 2;
            double phi = Math.acos(2 * level.getRandom().nextDouble() - 1);
            double dist = Math.cbrt(level.getRandom().nextDouble()) * r;
            double dx = dist * Math.sin(phi) * Math.cos(theta);
            double dy = dist * Math.sin(phi) * Math.sin(theta);
            double dz = dist * Math.cos(phi);
            level.addParticle(particle, center.x + dx, center.y + dy, center.z + dz, 0, 0.00, 0);
        }
    }

    public static void burstParticleEvent(ServerLevel level, ParticleOptions particle, Vec3 position,
                                           int count, double spreadX, double spreadY, double spreadZ,
                                           double speed, float scale) {
        int scaledCount = (int) (count * scale);
        level.sendParticles(particle, position.x, position.y, position.z,
            scaledCount, spreadX * scale, spreadY * scale, spreadZ * scale, speed);
    }
}
