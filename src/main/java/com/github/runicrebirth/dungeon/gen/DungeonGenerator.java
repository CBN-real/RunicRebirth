package com.github.runicrebirth.dungeon.gen;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonVariant;
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

        generateProcedural(dungeonLevel, instance);

        RunicRebirth.LOGGER.info("[DungeonGen] Generated instance {} at {}",
                instance.getTierId(), instance.getOrigin());
    }

    private static void generateProcedural(ServerLevel level, DungeonInstance instance) {
        DungeonVariant variant = instance.getVariant();
        if (variant == null) {
            RunicRebirth.LOGGER.warn("[Dungeon] No variant found for instance {}, using placeholder",
                    instance.getInstanceId());
            generatePlaceholderRoom(level, instance.getOrigin(), 11, 6, 11);
            return;
        }
        if (!variant.isValid()) {
            RunicRebirth.LOGGER.error("[Dungeon] Variant {} is invalid (missing required room types), using placeholder",
                    variant.getId());
            generatePlaceholderRoom(level, instance.getOrigin(), 11, 6, 11);
            return;
        }
        JigsawDungeonAssembler.assemble(level, instance, variant, level.getRandom());
    }

    static void generatePlaceholderRoom(ServerLevel level, BlockPos origin, int width, int height, int depth) {
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
        level.setBlock(origin.offset(width / 2, height - 2, depth / 2), Blocks.GLOWSTONE.defaultBlockState(), 2);
    }
}
