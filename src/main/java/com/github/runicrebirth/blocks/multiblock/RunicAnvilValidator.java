package com.github.runicrebirth.blocks.multiblock;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static com.github.runicrebirth.blocks.multiblock.StructureBlock.*;

public final class RunicAnvilValidator {

    private RunicAnvilValidator() {}

    public record ValidationResult(
            boolean valid,
            BlockPos anvilPos,
            List<BlockPos> pillarPositions,
            List<BlockPos> pylonPositions
    ) {
        public static final ValidationResult INVALID = new ValidationResult(false, null, List.of(), List.of());
    }

    private static final List<AbstractMultiblockValidator.Entry> STRUCTURE = buildStructure();

    private static List<AbstractMultiblockValidator.Entry> buildStructure() {
        StructureBlock[][][] layers = {
            // Layer 0 (y=0)
            {
                {RUNIC_STONE, RUNIC_STONE_SLAB, RUNIC_STONE_SLAB, RUNIC_STONE_SLAB, RUNIC_STONE},
                {RUNIC_STONE_SLAB, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE_SLAB},
                {RUNIC_STONE_SLAB, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE_SLAB},
                {RUNIC_STONE_SLAB, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE, RUNIC_STONE_SLAB},
                {RUNIC_STONE, RUNIC_STONE_SLAB, RUNIC_STONE_SLAB, RUNIC_STONE_SLAB, RUNIC_STONE},
            },
            // Layer 1 (y=1)
            {
                {RUNIC_STONE_PILLAR, AIR, AIR, AIR, RUNIC_STONE_PILLAR},
                {AIR, AIR, AIR, AIR, AIR},
                {AIR, AIR, RUNIC_ANVIL, AIR, AIR},
                {AIR, AIR, AIR, AIR, AIR},
                {RUNIC_STONE_PILLAR, AIR, AIR, AIR, RUNIC_STONE_PILLAR},
            },
            // Layer 2 (y=2)
            {
                {RUNESTEEL_PYLON, AIR, AIR, AIR, RUNESTEEL_PYLON},
                {AIR, AIR, AIR, AIR, AIR},
                {AIR, AIR, AIR, AIR, AIR},
                {AIR, AIR, AIR, AIR, AIR},
                {RUNESTEEL_PYLON, AIR, AIR, AIR, RUNESTEEL_PYLON},
            },
        };
        return AbstractMultiblockValidator.flattenLayers(layers);
    }

    public static boolean validate(Level level, BlockPos anvilPos) {
        return validateFull(level, anvilPos).valid();
    }

    public static ValidationResult validateFull(Level level, BlockPos anvilPos) {
        BlockPos origin = anvilPos.offset(-2, -1, -2);

        BlockPos foundAnvilPos = null;
        List<BlockPos> pillarPositions = new ArrayList<>();
        List<BlockPos> pylonPositions = new ArrayList<>();

        for (AbstractMultiblockValidator.Entry entry : STRUCTURE) {
            BlockPos worldPos = origin.offset(entry.dx(), entry.dy(), entry.dz());
            if (!AbstractMultiblockValidator.checkBlock(level, worldPos, entry.type())) {
                RunicRebirth.LOGGER.info("[RunicAnvil] Validation failed at {} — expected {}, found {}",
                        worldPos, entry.type(), level.getBlockState(worldPos).getBlock());
                return ValidationResult.INVALID;
            }

            switch (entry.type()) {
                case RUNIC_ANVIL -> foundAnvilPos = worldPos;
                case RUNIC_STONE_PILLAR -> pillarPositions.add(worldPos);
                case RUNESTEEL_PYLON -> pylonPositions.add(worldPos);
                default -> {}
            }
        }

        return new ValidationResult(true, foundAnvilPos, List.copyOf(pillarPositions), List.copyOf(pylonPositions));
    }
}
