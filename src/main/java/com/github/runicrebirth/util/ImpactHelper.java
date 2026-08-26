package com.github.runicrebirth.util;

import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.entities.spells.EnergyCracklingEntity;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.network.ImpactEffectS2CPacket;
import com.github.runicrebirth.particle.TremorBlockParticleOption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
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

        int flashCount = Math.min(3, Math.max(1, (int)(radius * 0.5f)));
        double flashSpread = radius * 1.5;
        level.sendParticles(ParticleTypes.FLASH, pos.x, pos.y + 0.5, pos.z, flashCount, flashSpread, 0.2, flashSpread, 0);

        double burstSpread = radius * 2.0;
        level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 0.3, pos.z, (int)(radius * 5), burstSpread, 0.4, burstSpread, 0.05);
        level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y + 0.8, pos.z, (int)(radius * 12), burstSpread * 2, burstSpread, burstSpread * 2, 0.02);

        ImpactEffectS2CPacket packet = new ImpactEffectS2CPacket(
            pos, radius, element.displayColor(), shakeIntensity, shakeDurationTicks);
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(pos) < PACKET_RANGE_SQ) {
                PacketDistributor.sendToPlayer(player, packet);
            }
        }

        float crackleDensity = Math.min(3.0f, 0.3f + radius * 0.3f);
        float crackleThickness = Math.min(2.5f, 0.5f + radius * 0.25f);
        EnergyCracklingEntity crackling = new EnergyCracklingEntity(
            level, radius * 1.5f, element.displayColor(), 35, crackleDensity, 1.0f, crackleThickness);
        crackling.setPos(pos.x, pos.y + 0.5, pos.z);
        level.addFreshEntity(crackling);

        int r = Mth.ceil(radius);
        BlockPos center = BlockPos.containing(pos);
        float baseImpulse = 0.7f;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > radius) continue;
                double chance = 1.0 - dist / radius;
                if (chance < 1.0 && level.random.nextFloat() > chance) continue;
                Vec3 point = new Vec3(center.getX() + dx + 0.5, pos.y + 0.2, center.getZ() + dz + 0.5);
                BlockPos groundPos = findGround(level, point, 2);
                if (groundPos != null) {
                    float strength = (float) (baseImpulse * chance) + level.random.nextFloat() * 0.15f;
                    double xMotion = 0;
                    double zMotion = 0;
                    if (dist > 0.01) {
                        double lateral = strength * 0.8;
                        xMotion = (dx / dist) * lateral;
                        zMotion = (dz / dist) * lateral;
                    }
                    BlockState groundState = level.getBlockState(groundPos);
                    BlockPos tremorPos = groundPos;
                    if (!groundState.isAir() && !groundState.liquid()
                            && groundState.getDestroySpeed(level, groundPos) == 0.0f) {
                        level.destroyBlock(groundPos, true);
                        tremorPos = groundPos.below();
                    }
                    createTremorBlock(level, tremorPos, strength, xMotion, zMotion);
                    // mini debris: half-size, twice the lateral distance
                    for (int i = 0; i < 2; i++) {
                        float miniStrength = strength * (0.7f + level.random.nextFloat() * 0.4f);
                        double miniX = xMotion * 2.0 + (level.random.nextDouble() - 0.5) * 0.2;
                        double miniZ = zMotion * 2.0 + (level.random.nextDouble() - 0.5) * 0.2;
                        createTremorBlockMini(level, tremorPos, miniStrength, miniX, miniZ);
                    }
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

    private static void createTremorBlockMini(ServerLevel level, BlockPos blockPos,
                                               float impulseStrength, double xMotion, double zMotion) {
        Vec3 motion = new Vec3(xMotion, impulseStrength, zMotion);
        level.sendParticles(
            new TremorBlockParticleOption(level.getBlockState(blockPos), motion, 0.5f),
            blockPos.getX(), blockPos.getY(), blockPos.getZ(),
            1, 0, 0, 0, 0);
    }
}
