package com.github.runicrebirth.client.animations;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class WarstaffAnimLayer {

    private static final Map<Integer, Long> expireMap = new HashMap<>();

    private WarstaffAnimLayer() {}

    public static void setSpinning(int playerId, long durationMs) {
        expireMap.put(playerId, System.currentTimeMillis() + durationMs);
    }

    public static boolean isSpinning(int playerId) {
        return System.currentTimeMillis() < expireMap.getOrDefault(playerId, 0L);
    }

}
