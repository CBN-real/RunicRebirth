package com.github.runicrebirth.dungeon.gen;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonVariant;
import com.github.runicrebirth.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public final class JigsawDungeonAssembler {

    private JigsawDungeonAssembler() {}

    private record JigsawInfo(BlockPos worldPos, Direction facing, String name, String target, String pool, String finalState) {}
    private record PlacedRoom(AABB bounds) {}

    public static void assemble(ServerLevel level, DungeonInstance instance, DungeonVariant variant, RandomSource random) {
        BlockPos origin = instance.getOrigin();
        List<PlacedRoom> placedRooms = new ArrayList<>();
        Deque<JigsawInfo> openConnections = new ArrayDeque<>();
        List<JigsawInfo> bossConnections = new ArrayList<>();

        // 1. Place entry room
        ResourceLocation entryRL = variant.pickEntryRoom(random);
        Optional<StructureTemplate> entryOpt = loadTemplate(level, entryRL);
        if (entryOpt.isEmpty()) {
            RunicRebirth.LOGGER.error("[Dungeon] Failed to load entry room template: {}", entryRL);
            return;
        }
        StructureTemplate entryTemplate = entryOpt.get();
        StructurePlaceSettings entrySettings = new StructurePlaceSettings().setRotation(Rotation.NONE);
        ensureChunksLoaded(level, origin, entryTemplate.getSize());
        entryTemplate.placeInWorld(level, origin, origin, entrySettings, random, 3);

        Vec3i entrySize = entryTemplate.getSize();
        placedRooms.add(new PlacedRoom(computeAABB(origin, entrySize)));

        scanForEntryPortal(level, origin, entrySize, instance);

        List<JigsawInfo> entryJigsaws = extractJigsaws(level, origin, entryTemplate, entrySettings);
        for (JigsawInfo ji : entryJigsaws) {
            if (DungeonVariant.POOL_BOSS.equals(ji.pool())) {
                bossConnections.add(ji);
            } else {
                openConnections.add(ji);
            }
        }

        // 2. BFS: fill rooms up to roomCount
        int remainingRooms = variant.getRoomCount();
        int maxAttempts = remainingRooms * 10;
        int attempts = 0;

        while (!openConnections.isEmpty() && remainingRooms > 0 && attempts < maxAttempts) {
            attempts++;
            JigsawInfo sourceConn = openConnections.poll();

            ResourceLocation roomRL = variant.pickFromPool(sourceConn.pool(), random);
            if (roomRL == null) continue;

            Optional<StructureTemplate> roomOpt = loadTemplate(level, roomRL);
            if (roomOpt.isEmpty()) {
                RunicRebirth.LOGGER.warn("[Dungeon] Missing template: {}", roomRL);
                continue;
            }
            StructureTemplate roomTemplate = roomOpt.get();

            List<Rotation> rotList = new ArrayList<>(Arrays.asList(Rotation.values()));
            Collections.shuffle(rotList, new java.util.Random(random.nextLong()));

            boolean placed = false;
            for (Rotation rotation : rotList) {
                StructurePlaceSettings candidateSettings = new StructurePlaceSettings().setRotation(rotation);
                List<StructureTemplate.StructureBlockInfo> candidateJigsaws =
                        roomTemplate.filterBlocks(BlockPos.ZERO, candidateSettings, Blocks.JIGSAW);

                for (StructureTemplate.StructureBlockInfo candidateInfo : candidateJigsaws) {
                    CompoundTag nbt = candidateInfo.nbt();
                    if (nbt == null) continue;
                    String candidateTarget = nbt.getString("target");
                    if (!candidateTarget.equals(sourceConn.name())) continue;

                    FrontAndTop candidateOrientation = candidateInfo.state().getValue(JigsawBlock.ORIENTATION);
                    Direction candidateFacing = rotation.rotate(candidateOrientation.front());
                    if (!candidateFacing.equals(sourceConn.facing().getOpposite())) continue;

                    BlockPos targetJigsawWorldPos = sourceConn.worldPos().relative(sourceConn.facing());
                    BlockPos localJigsawPos = candidateInfo.pos();
                    BlockPos placementOrigin = targetJigsawWorldPos.subtract(localJigsawPos);

                    Vec3i rotatedSize = getRotatedSize(roomTemplate.getSize(), rotation);
                    AABB candidateAABB = computeAABB(placementOrigin, rotatedSize);

                    if (intersectsAny(candidateAABB, placedRooms)) continue;

                    ensureChunksLoaded(level, placementOrigin, rotatedSize);
                    roomTemplate.placeInWorld(level, placementOrigin, placementOrigin, candidateSettings, random, 3);

                    setFinalState(level, sourceConn.worldPos(), sourceConn.finalState());
                    setFinalState(level, targetJigsawWorldPos, nbt.getString("final_state"));

                    placedRooms.add(new PlacedRoom(candidateAABB));

                    List<JigsawInfo> newJigsaws = extractJigsaws(level, placementOrigin, roomTemplate, candidateSettings);
                    for (JigsawInfo nji : newJigsaws) {
                        if (DungeonVariant.POOL_BOSS.equals(nji.pool())) {
                            bossConnections.add(nji);
                        } else {
                            openConnections.add(nji);
                        }
                    }

                    remainingRooms--;
                    placed = true;
                    break;
                }
                if (placed) break;
            }
        }

        // 3. Place boss room
        if (bossConnections.isEmpty() && !openConnections.isEmpty()) {
            bossConnections.add(openConnections.poll());
        }

        JigsawInfo bossOutwardJigsaw = null;
        if (!bossConnections.isEmpty()) {
            JigsawInfo bossSrcConn = bossConnections.get(0);
            ResourceLocation bossRL = variant.pickBossRoom(random);
            Optional<StructureTemplate> bossOpt = loadTemplate(level, bossRL);
            if (bossOpt.isPresent()) {
                bossOutwardJigsaw = placeSpecialRoom(level, bossOpt.get(), bossSrcConn, placedRooms, random, DungeonVariant.POOL_INNER_SANCTUM);
            } else {
                RunicRebirth.LOGGER.error("[Dungeon] Failed to load boss room: {}", bossRL);
            }
        }

        // 4. Place inner sanctum
        if (bossOutwardJigsaw != null) {
            ResourceLocation sanctumRL = variant.pickInnerSanctumRoom(random);
            Optional<StructureTemplate> sanctumOpt = loadTemplate(level, sanctumRL);
            if (sanctumOpt.isPresent()) {
                placeSpecialRoom(level, sanctumOpt.get(), bossOutwardJigsaw, placedRooms, random, null);
            } else {
                RunicRebirth.LOGGER.error("[Dungeon] Failed to load inner sanctum: {}", sanctumRL);
            }
        }

        RunicRebirth.LOGGER.info("[Dungeon] Assembly complete for instance {}. Placed {} rooms.",
                instance.getInstanceId(), placedRooms.size());
    }

    @Nullable
    private static JigsawInfo placeSpecialRoom(ServerLevel level, StructureTemplate template,
                                                JigsawInfo sourceConn, List<PlacedRoom> placedRooms,
                                                RandomSource random, @Nullable String outwardPoolName) {
        for (Rotation rotation : Rotation.values()) {
            StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation);
            List<StructureTemplate.StructureBlockInfo> candidates =
                    template.filterBlocks(BlockPos.ZERO, settings, Blocks.JIGSAW);

            for (StructureTemplate.StructureBlockInfo candidateInfo : candidates) {
                CompoundTag nbt = candidateInfo.nbt();
                if (nbt == null) continue;
                if (!nbt.getString("target").equals(sourceConn.name())) continue;

                FrontAndTop orientation = candidateInfo.state().getValue(JigsawBlock.ORIENTATION);
                Direction candidateFacing = rotation.rotate(orientation.front());
                if (!candidateFacing.equals(sourceConn.facing().getOpposite())) continue;

                BlockPos targetJigsawWorldPos = sourceConn.worldPos().relative(sourceConn.facing());
                BlockPos placementOrigin = targetJigsawWorldPos.subtract(candidateInfo.pos());

                Vec3i rotatedSize = getRotatedSize(template.getSize(), rotation);
                AABB candidateAABB = computeAABB(placementOrigin, rotatedSize);

                if (intersectsAny(candidateAABB, placedRooms)) continue;

                ensureChunksLoaded(level, placementOrigin, rotatedSize);
                template.placeInWorld(level, placementOrigin, placementOrigin, settings, random, 3);

                setFinalState(level, sourceConn.worldPos(), sourceConn.finalState());
                setFinalState(level, targetJigsawWorldPos, nbt.getString("final_state"));

                placedRooms.add(new PlacedRoom(candidateAABB));

                if (outwardPoolName != null) {
                    List<JigsawInfo> newJigsaws = extractJigsaws(level, placementOrigin, template, settings);
                    for (JigsawInfo ji : newJigsaws) {
                        if (outwardPoolName.equals(ji.pool())) return ji;
                    }
                }
                return null;
            }
        }
        return null;
    }

    private static Optional<StructureTemplate> loadTemplate(ServerLevel level, ResourceLocation id) {
        return level.getServer().getStructureManager().get(id);
    }

    private static List<JigsawInfo> extractJigsaws(ServerLevel level, BlockPos origin,
                                                    StructureTemplate template, StructurePlaceSettings settings) {
        List<JigsawInfo> result = new ArrayList<>();
        List<StructureTemplate.StructureBlockInfo> jigsawInfos = template.filterBlocks(origin, settings, Blocks.JIGSAW);
        for (StructureTemplate.StructureBlockInfo info : jigsawInfos) {
            CompoundTag nbt = info.nbt();
            if (nbt == null) continue;
            String name = nbt.getString("name");
            String target = nbt.getString("target");
            String pool = nbt.getString("pool");
            String finalState = nbt.getString("final_state");
            FrontAndTop orientation = info.state().getValue(JigsawBlock.ORIENTATION);
            Direction facing = orientation.front();
            result.add(new JigsawInfo(info.pos(), facing, name, target, pool, finalState));
        }
        return result;
    }

    private static AABB computeAABB(BlockPos origin, Vec3i size) {
        return new AABB(
                origin.getX(), origin.getY(), origin.getZ(),
                origin.getX() + size.getX(), origin.getY() + size.getY(), origin.getZ() + size.getZ()
        );
    }

    private static boolean intersectsAny(AABB candidate, List<PlacedRoom> placed) {
        AABB shrunk = candidate.deflate(0.5);
        for (PlacedRoom room : placed) {
            if (shrunk.intersects(room.bounds().deflate(0.5))) return true;
        }
        return false;
    }

    private static void setFinalState(ServerLevel level, BlockPos pos, String finalState) {
        try {
            int bracketIdx = finalState.indexOf('[');
            String blockName = bracketIdx >= 0 ? finalState.substring(0, bracketIdx) : finalState;
            if (blockName.isEmpty()) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return;
            }
            ResourceLocation blockId = ResourceLocation.parse(blockName);
            Block block = BuiltInRegistries.BLOCK.get(blockId);
            level.setBlock(pos, block.defaultBlockState(), 3);
        } catch (Exception e) {
            RunicRebirth.LOGGER.warn("[Dungeon] Could not parse final_state '{}': {}", finalState, e.getMessage());
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static void ensureChunksLoaded(ServerLevel level, BlockPos origin, Vec3i size) {
        int minCX = SectionPos.blockToSectionCoord(origin.getX());
        int maxCX = SectionPos.blockToSectionCoord(origin.getX() + size.getX());
        int minCZ = SectionPos.blockToSectionCoord(origin.getZ());
        int maxCZ = SectionPos.blockToSectionCoord(origin.getZ() + size.getZ());
        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                level.getChunkSource().getChunk(cx, cz, true);
            }
        }
    }

    private static void scanForEntryPortal(ServerLevel level, BlockPos origin, Vec3i size, DungeonInstance instance) {
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (level.getBlockState(pos).is(ModBlocks.OCULUS_PORTAL.get())) {
                        instance.setEntryPortalPos(pos);
                        return;
                    }
                }
            }
        }
    }

    private static Vec3i getRotatedSize(Vec3i originalSize, Rotation rotation) {
        return switch (rotation) {
            case NONE, CLOCKWISE_180 -> originalSize;
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> new Vec3i(originalSize.getZ(), originalSize.getY(), originalSize.getX());
        };
    }
}
