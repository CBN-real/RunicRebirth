package com.github.interactivemagic.capabilities.magic;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Lerped aim target for casting mobs. Ported from Iron's Spellbooks CastingMobAimingData.
 * Kept ready for future IMagicEntity mob casters; not used by player casts in v1.
 */
public class CastingEntityAimingData {

    private Vec3 aimPosition = Vec3.ZERO;
    private Vec3 lastAimPosition = Vec3.ZERO;

    public void updateAim(Entity target, float strength) {
        Vec3 wanted = target.getBoundingBox().getCenter();
        if (aimPosition.equals(Vec3.ZERO)) {
            aimPosition = wanted;
            lastAimPosition = wanted;
        } else {
            lastAimPosition = aimPosition;
            aimPosition = aimPosition.add(wanted.subtract(aimPosition).scale(strength));
        }
    }

    public Vec3 getAimPosition() { return aimPosition; }

    public Vec3 getAimPosition(float partialTick) {
        return lastAimPosition.add(aimPosition.subtract(lastAimPosition).scale(partialTick));
    }

    public Vec3 getForward(Entity host) {
        return aimPosition.subtract(host.getEyePosition()).normalize();
    }

    public void reset() {
        aimPosition = Vec3.ZERO;
        lastAimPosition = Vec3.ZERO;
    }

    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeDouble(aimPosition.x); buffer.writeDouble(aimPosition.y); buffer.writeDouble(aimPosition.z);
        buffer.writeDouble(lastAimPosition.x); buffer.writeDouble(lastAimPosition.y); buffer.writeDouble(lastAimPosition.z);
    }

    public void readFromBuffer(FriendlyByteBuf buffer) {
        aimPosition = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        lastAimPosition = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return new CompoundTag();
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
    }
}
