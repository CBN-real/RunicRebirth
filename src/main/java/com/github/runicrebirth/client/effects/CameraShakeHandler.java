package com.github.runicrebirth.client.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.util.ArrayList;
import java.util.List;

public final class CameraShakeHandler {

    private static final List<ShakeInstance> SHAKES = new ArrayList<>();

    private record ShakeInstance(Vec3 origin, float intensity, int durationTicks, int startTick) {
        boolean isExpired(int currentTick) {
            return currentTick - startTick >= durationTicks;
        }

        float fadeMultiplier(int currentTick) {
            int elapsed = currentTick - startTick;
            int fadeStart = durationTicks - 20;
            if (fadeStart > 0 && elapsed >= fadeStart) {
                return 1f - (float) (elapsed - fadeStart) / 20f;
            }
            return 1f;
        }
    }

    private CameraShakeHandler() {}

    public static void addShake(Vec3 origin, float intensity, int durationTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        SHAKES.add(new ShakeInstance(origin, intensity, durationTicks, mc.player.tickCount));
    }

    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || SHAKES.isEmpty()) return;

        Vec3 cameraPos = event.getCamera().position();
        int tick = mc.player.tickCount;

        ShakeInstance closest = null;
        double closestDistSq = Double.MAX_VALUE;

        for (ShakeInstance shake : SHAKES) {
            if (shake.isExpired(tick)) continue;
            double distSq = cameraPos.distanceToSqr(shake.origin);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = shake;
            }
        }

        if (closest == null) return;

        float radiusSq = 15f * 15f;
        float distanceFalloff = 1f - (float) Mth.clamp(closestDistSq / radiusSq, 0, 1);
        float fade = closest.fadeMultiplier(tick);
        float intensity = closest.intensity * distanceFalloff * fade;

        if (intensity < 0.001f) return;

        float f = tick + (float) event.getPartialTick();
        event.setYaw(event.getYaw() + Mth.cos(f * 1.5f) * intensity * 0.5f);
        event.setPitch(event.getPitch() + Mth.cos(f * 2f) * intensity * 0.5f);
        event.setRoll(event.getRoll() + Mth.sin(f * 2.2f) * intensity * 0.5f);
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        int tick = mc.player.tickCount;
        SHAKES.removeIf(s -> s.isExpired(tick));
    }

    public static void clear() {
        SHAKES.clear();
    }
}
