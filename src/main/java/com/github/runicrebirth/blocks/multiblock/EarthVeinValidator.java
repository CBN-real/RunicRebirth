package com.github.runicrebirth.blocks.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import static com.github.runicrebirth.blocks.multiblock.StructureBlock.*;

public final class EarthVeinValidator {

    private EarthVeinValidator() {}

    // Layer 0 (dy=0, same Y as cushion). null = skip (center cell).
    // row = Z offset from -3, col = X offset from -3
    private static final StructureBlock[][] LAYER_0 = {
        {AIR, AIR, AIR, RUNELIGHT_TORCH, AIR, AIR, AIR},
        {AIR, RUNELIGHT_TORCH, AIR, AIR, AIR, RUNELIGHT_TORCH, AIR},
        {AIR, AIR, AIR, AIR, AIR, AIR, AIR},
        {RUNELIGHT_TORCH, AIR, AIR, null, AIR, AIR, RUNELIGHT_TORCH},
        {AIR, AIR, AIR, AIR, AIR, AIR, AIR},
        {AIR, RUNELIGHT_TORCH, AIR, AIR, AIR, RUNELIGHT_TORCH, AIR},
        {AIR, AIR, AIR, RUNELIGHT_TORCH, AIR, AIR, AIR},
    };

    // Layer 1 (dy=-1, one block below cushion). null = skip (any block fine).
    private static final StructureBlock[][] LAYER_1 = {
        {null, null, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, null, null},
        {null, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, null},
        {RUNIC_STONE, RUNIC_STONE, CUT_RUNIC_STONE, CUT_RUNIC_STONE, CUT_RUNIC_STONE, RUNIC_STONE, RUNIC_STONE},
        {RUNIC_STONE, RUNIC_STONE, CUT_RUNIC_STONE, CUT_RUNIC_STONE, CUT_RUNIC_STONE, RUNIC_STONE, RUNIC_STONE},
        {RUNIC_STONE, RUNIC_STONE, CUT_RUNIC_STONE, CUT_RUNIC_STONE, CUT_RUNIC_STONE, RUNIC_STONE, RUNIC_STONE},
        {null, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, null},
        {null, null, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, null, null},
    };

    public static boolean validate(Level level, BlockPos cushionPos) {
        int baseX = cushionPos.getX() - 3;
        int baseZ = cushionPos.getZ() - 3;

        // Layer 0 (same Y as cushion)
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                StructureBlock expected = LAYER_0[row][col];
                if (expected == null) continue;
                BlockPos check = new BlockPos(baseX + col, cushionPos.getY(), baseZ + row);
                if (!AbstractMultiblockValidator.checkBlock(level, check, expected)) {
                    return false;
                }
            }
        }

        // Layer 1 (one block below cushion)
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                StructureBlock expected = LAYER_1[row][col];
                if (expected == null) continue;
                BlockPos check = new BlockPos(baseX + col, cushionPos.getY() - 1, baseZ + row);
                if (!AbstractMultiblockValidator.checkBlock(level, check, expected)) {
                    return false;
                }
            }
        }

        return true;
    }
}
