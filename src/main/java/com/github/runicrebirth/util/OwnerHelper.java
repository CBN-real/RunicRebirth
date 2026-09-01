package com.github.runicrebirth.util;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class OwnerHelper {

    private OwnerHelper() {}

    public static LivingEntity getAndCacheOwner(Level level, LivingEntity cachedOwner, UUID summonerUUID) {
        if (cachedOwner != null && cachedOwner.isAlive()) {
            return cachedOwner;
        } else if (summonerUUID != null && level instanceof ServerLevel serverLevel) {
            if (serverLevel.getEntity(summonerUUID) instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }
        return null;
    }

    public static void serializeOwner(CompoundTag tag, UUID ownerUUID) {
        if (ownerUUID != null) {
            tag.putString("Summoner", ownerUUID.toString());
        }
    }

    public static UUID deserializeOwner(CompoundTag tag) {
        return tag.contains("Summoner") ? tag.getString("Summoner").map(UUID::fromString).orElse(null) : null;
    }
}
