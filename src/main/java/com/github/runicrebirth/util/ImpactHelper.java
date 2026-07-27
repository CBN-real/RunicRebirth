package com.github.runicrebirth.util;

import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.network.ImpactEffectS2CPacket;
import com.github.runicrebirth.particle.TremorBlockParticleOption;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ImpactHelper {

    private static final double PACKET_RANGE_SQ = 64.0 * 64.0;

    private ImpactHelper() {}

    public static void createImpact(ServerLevel level, Vec3 pos, float radius, Element element,
                                    float shakeIntensity) {
        createImpact(level, pos, radius, element, shakeIntensity, 40);
    }

    public static void createImpact(ServerLevel level, Vec3 pos, float radius, Element element,
                                    float shakeIntensity, int shakeDurationTicks) {
        level.playSound(null, pos.x, pos.y, pos.z, ModSounds.SPELLS_EXPLOSION.get(), SoundSource.PLAYERS, 1.0f + radius * 0.05f, 1.0f);

        ImpactEffectS2CPacket packet = new ImpactEffectS2CPacket(
            pos, radius, element.displayColor(), shakeIntensity, shakeDurationTicks);
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(pos) < PACKET_RANGE_SQ) {
                PacketDistributor.sendToPlayer(player, packet);
            }
        }

        int r = Mth.ceil(radius);
        BlockPos center = BlockPos.containing(pos);
        float baseImpulse = 0.55f;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > radius) continue;
                double chance = 1.0 - dist / radius;
                if (chance < 1.0 && level.random.nextFloat() > chance) continue;
                Vec3 point = new Vec3(center.getX() + dx + 0.5, pos.y + 0.2, center.getZ() + dz + 0.5);
                BlockPos groundPos = findGround(level, point, 2);
                if (groundPos != null) {
                    float strength = (float) (baseImpulse * chance) + level.random.nextFloat() * 0.1f;
                    double xMotion = 0;
                    double zMotion = 0;
                    if (dist > 0.01) {
                        double lateral = strength * 0.75;
                        xMotion = (dx / dist) * lateral;
                        zMotion = (dz / dist) * lateral;
                    }
                    createTremorBlock(level, groundPos, strength, xMotion, zMotion);
                }
            }
        }
    }

    private static BlockPos findGround(ServerLevel level, Vec3 pos, int searchRange) {
        BlockPos base = BlockPos.containing(pos);
        for (int y = searchRange; y >= -searchRange; y--) {
            BlockPos check = base.offset(0, y, 0);
            if (!level.getBlockState(check).isAir() && level.getBlockState(check.above()).isAir()) {
                return check;
            }
        }
        return null;
    }

    private static void createTremorBlock(ServerLevel level, BlockPos blockPos,
                                           float impulseStrength, double xMotion, double zMotion) {
        if (level.getBlockState(blockPos.above()).isAir() || level.getBlockState(blockPos.above().above()).isAir()) {
            Vec3 motion = new Vec3(xMotion, impulseStrength, zMotion);
            level.sendParticles(
                new TremorBlockParticleOption(level.getBlockState(blockPos), motion),
                blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                1, 0, 0, 0, 0);
            if (!level.getBlockState(blockPos.above()).isAir()) {
                level.sendParticles(
                    new TremorBlockParticleOption(level.getBlockState(blockPos.above()), motion),
                    blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                    1, 0, 0, 0, 0);
            }
        }
    }
}
