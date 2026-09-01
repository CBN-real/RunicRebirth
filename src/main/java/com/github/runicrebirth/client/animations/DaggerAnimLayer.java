package com.github.runicrebirth.client.animations;

import com.github.runicrebirth.network.DaggerAnimS2CPacket;
import net.minecraft.client.player.AbstractClientPlayer;

import java.util.HashMap;
import java.util.Map;

public final class DaggerAnimLayer {

    private static final Map<Integer, DaggerAnimS2CPacket.Anim> animStates = new HashMap<>();

    private DaggerAnimLayer() {}

    public static void trigger(AbstractClientPlayer player, DaggerAnimS2CPacket.Anim anim) {
        animStates.put(player.getId(), anim);
    }

    public static DaggerAnimS2CPacket.Anim getAnim(int entityId) {
        return animStates.getOrDefault(entityId, DaggerAnimS2CPacket.Anim.IDLE);
    }

    public static void clear(int entityId) {
        animStates.remove(entityId);
    }
}
