package com.github.runicrebirth.dungeon.gen;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonType;
import com.github.runicrebirth.dungeon.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

public final class DungeonGenerator {

    private DungeonGenerator() {}

    public static void generate(MinecraftServer server, DungeonInstance instance) {
        ServerLevel dungeonLevel = server.getLevel(ModDimensions.DUNGEON_LEVEL);
        if (dungeonLevel == null) {
            RunicRebirth.LOGGER.error("[DungeonGen] Dungeon dimension not loaded!");
            return;
        }

        if (instance.getDungeonType().getGenerationMode() == DungeonType.GenerationMode.PRESET) {
            generatePreset(dungeonLevel, instance);
        } else {
            generateProcedural(dungeonLevel, instance);
        }

        RunicRebirth.LOGGER.info("[DungeonGen] Generated {} at {}",
                instance.getDungeonType().getId(), instance.getOrigin());
    }

    private static void generatePreset(ServerLevel level, DungeonInstance instance) {
        // TODO: Load NBT structure template for this trial type
        // For now, generate a simple stone room as placeholder
        generatePlaceholderRoom(level, instance.getOrigin(), 11, 6, 11);
    }

    private static void generateProcedural(ServerLevel level, DungeonInstance instance) {
        // TODO: Jigsaw-based room assembly from templates
        // For now, generate a sequence of connected rooms as placeholder
        BlockPos origin = instance.getOrigin();

        // Start room
        generatePlaceholderRoom(level, origin, 11, 6, 11);

        // Hallway
        generatePlaceholderRoom(level, origin.offset(0, 0, 11), 5, 4, 8);

        // Combat room
        generatePlaceholderRoom(level, origin.offset(-3, 0, 19), 11, 6, 11);

        // Hallway 2
        generatePlaceholderRoom(level, origin.offset(0, 0, 30), 5, 4, 8);

        // Boss room (with completion platform)
        BlockPos bossOrigin = origin.offset(-3, 0, 38);
        generatePlaceholderRoom(level, bossOrigin, 15, 8, 15);
        placeCompletionPlatform(level, bossOrigin.offset(5, 1, 5));
    }

    private static void generatePlaceholderRoom(ServerLevel level, BlockPos origin, int width, int height, int depth) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    boolean isWall = x == 0 || x == width - 1 || y == 0 || y == height - 1 || z == 0 || z == depth - 1;
                    if (isWall) {
                        level.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 2);
                    } else {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        // Glowstone lighting
        level.setBlock(origin.offset(width / 2, height - 2, depth / 2), Blocks.GLOWSTONE.defaultBlockState(), 2);
    }

    private static void placeCompletionPlatform(ServerLevel level, BlockPos center) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                level.setBlock(center.offset(x, 0, z), Blocks.GOLD_BLOCK.defaultBlockState(), 2);
            }
        }
    }
}
