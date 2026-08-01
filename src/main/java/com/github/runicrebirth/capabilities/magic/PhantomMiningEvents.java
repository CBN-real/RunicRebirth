package com.github.runicrebirth.capabilities.magic;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.PhantomMinerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public final class PhantomMiningEvents {

    private static final ThreadLocal<Boolean> BREAKING = ThreadLocal.withInitial(() -> false);

    // Player UUID → array of 8 entity IDs (phantom miners)
    private static final Map<UUID, int[]> ACTIVE_MINERS = new HashMap<>();

    private static final Field FIELD_IS_DESTROYING;
    private static final Field FIELD_DESTROY_POS;

    static {
        Field isDestroying = null;
        Field destroyPos = null;
        try {
            isDestroying = ObfuscationReflectionHelper.findField(ServerPlayerGameMode.class, "isDestroyingBlock");
            destroyPos = ObfuscationReflectionHelper.findField(ServerPlayerGameMode.class, "destroyPos");
        } catch (Exception e) {
            RunicRebirth.LOGGER.error("[RunicRebirth] Could not reflect ServerPlayerGameMode mining fields", e);
        }
        FIELD_IS_DESTROYING = isDestroying;
        FIELD_DESTROY_POS = destroyPos;
    }

    private PhantomMiningEvents() {}

    @SubscribeEvent
    public static void onPlayerPostTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        if (MagicData.of(player).phantomMiningTicks() <= 0) {
            clearMiners(player.getUUID(), level);
            return;
        }

        boolean isDestroying = isDestroyingBlock(player);
        BlockPos destroyPos = isDestroying ? getDestroyPos(player) : null;

        if (!isDestroying || destroyPos == null || destroyPos.equals(BlockPos.ZERO)) {
            clearMiners(player.getUUID(), level);
            return;
        }

        Direction face = getFace(player, destroyPos);
        if (face == null) {
            clearMiners(player.getUUID(), level);
            return;
        }

        Vec3[] positions = compute3x3Positions(destroyPos, face);
        int[] ids = ACTIVE_MINERS.get(player.getUUID());

        if (ids == null || ids.length != 8) {
            // Spawn fresh set
            ids = spawnMiners(player, level, positions, face);
            ACTIVE_MINERS.put(player.getUUID(), ids);
        } else {
            // Update existing entities
            for (int i = 0; i < 8; i++) {
                Entity e = level.getEntity(ids[i]);
                if (e instanceof PhantomMinerEntity miner) {
                    miner.moveTo(positions[i]);
                    miner.setFace(face);
                    miner.setDisplayYaw(player.getYRot());
                } else {
                    // Entity missing — respawn entire set
                    clearMiners(player.getUUID(), level);
                    ids = spawnMiners(player, level, positions, face);
                    ACTIVE_MINERS.put(player.getUUID(), ids);
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        clearMiners(player.getUUID(), level);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (BREAKING.get()) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        MagicData data = MagicData.of(player);
        if (data.phantomMiningTicks() <= 0) return;
        if (player.isShiftKeyDown()) return;

        BlockPos targetPos = event.getPos();

        Direction face;
        HitResult hit = player.pick(6.0, 1.0f, false);
        if (hit instanceof BlockHitResult blockHit && blockHit.getBlockPos().equals(targetPos)) {
            face = blockHit.getDirection();
        } else {
            Vec3 look = player.getLookAngle();
            face = Direction.getNearest(look.x, look.y, look.z);
        }

        try {
            BREAKING.set(true);
            for (int u = -1; u <= 1; u++) {
                for (int v = -1; v <= 1; v++) {
                    if (u == 0 && v == 0) continue;
                    BlockPos adjacent = switch (face) {
                        case UP, DOWN -> targetPos.offset(u, 0, v);
                        case NORTH, SOUTH -> targetPos.offset(u, v, 0);
                        case EAST, WEST -> targetPos.offset(0, u, v);
                    };
                    BlockState state = level.getBlockState(adjacent);
                    if (state.isAir()) continue;
                    if (state.getDestroySpeed(level, adjacent) < 0) continue;
                    player.gameMode.destroyBlock(adjacent);
                }
            }
        } finally {
            BREAKING.set(false);
        }
    }

    // --- helpers ---

    private static int[] spawnMiners(ServerPlayer player, ServerLevel level, Vec3[] positions, Direction face) {
        int[] ids = new int[8];
        for (int i = 0; i < 8; i++) {
            PhantomMinerEntity entity = PhantomMinerEntity.create(player, positions[i], face);
            level.addFreshEntity(entity);
            ids[i] = entity.getId();
        }
        return ids;
    }

    private static void clearMiners(UUID playerUuid, ServerLevel level) {
        int[] ids = ACTIVE_MINERS.remove(playerUuid);
        if (ids == null) return;
        for (int id : ids) {
            Entity e = level.getEntity(id);
            if (e != null) e.discard();
        }
    }

    private static Vec3[] compute3x3Positions(BlockPos target, Direction face) {
        Vec3[] positions = new Vec3[8];
        double fx = face.getStepX() * 0.8;
        double fy = face.getStepY() * 0.8;
        double fz = face.getStepZ() * 0.8;
        int idx = 0;
        for (int u = -1; u <= 1; u++) {
            for (int v = -1; v <= 1; v++) {
                if (u == 0 && v == 0) continue;
                BlockPos adj = switch (face) {
                    case UP, DOWN -> target.offset(u, -0, v);
                    case NORTH, SOUTH -> target.offset(u, v, 0);
                    case EAST, WEST -> target.offset(0, u, v);
                };
                Vec3 center = Vec3.atCenterOf(adj);
                positions[idx++] = new Vec3(center.x + fx, center.y + fy, center.z + fz);
            }
        }
        return positions;
    }

    private static Direction getFace(ServerPlayer player, BlockPos target) {
        HitResult hit = player.pick(6.0, 1.0f, false);
        if (hit instanceof BlockHitResult blockHit && blockHit.getBlockPos().equals(target)) {
            return blockHit.getDirection();
        }
        Vec3 look = player.getLookAngle();
        return Direction.getNearest(look.x, look.y, look.z);
    }

    private static boolean isDestroyingBlock(ServerPlayer player) {
        if (FIELD_IS_DESTROYING == null) return false;
        try {
            return (boolean) FIELD_IS_DESTROYING.get(player.gameMode);
        } catch (Exception e) {
            return false;
        }
    }

    private static BlockPos getDestroyPos(ServerPlayer player) {
        if (FIELD_DESTROY_POS == null) return BlockPos.ZERO;
        try {
            return (BlockPos) FIELD_DESTROY_POS.get(player.gameMode);
        } catch (Exception e) {
            return BlockPos.ZERO;
        }
    }
}
