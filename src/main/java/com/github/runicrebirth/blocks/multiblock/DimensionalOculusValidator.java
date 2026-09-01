package com.github.runicrebirth.blocks.multiblock;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static com.github.runicrebirth.blocks.multiblock.StructureBlock.*;

public final class DimensionalOculusValidator {

    private DimensionalOculusValidator() {}

    public record ValidationResult(
            boolean valid,
            BlockPos portalPos,
            BlockPos controllerPos,
            List<BlockPos> pillarPositions
    ) {
        public static final ValidationResult INVALID = new ValidationResult(false, null, null, List.of());
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
                {AIR, AIR, OCULUS_PORTAL, AIR, AIR},
                {AIR, OCULUS_CONTROLLER, AIR, AIR, AIR},
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

    public static boolean validate(Level level, BlockPos framePos) {
        return validateFull(level, framePos).valid();
    }

    public static ValidationResult validateFull(Level level, BlockPos framePos) {
        BlockState frameState = level.getBlockState(framePos);
        if (!frameState.hasProperty(com.github.runicrebirth.blocks.OculusPortalBlock.FACING)) {
            RunicRebirth.LOGGER.info("[DimensionalOculus] Block at {} is not a directional portal", framePos);
            return ValidationResult.INVALID;
        }
        Direction orientation = frameState.getValue(com.github.runicrebirth.blocks.OculusPortalBlock.FACING);

        BlockPos origin = computeOrigin(framePos, orientation);

        BlockPos portalPos = null;
        BlockPos controllerPos = null;
        List<BlockPos> pillarPositions = new ArrayList<>();

        for (AbstractMultiblockValidator.Entry entry : STRUCTURE) {
            BlockPos worldPos = AbstractMultiblockValidator.rotateAndOffset(origin, entry.dx(), entry.dy(), entry.dz(), orientation);
            if (!AbstractMultiblockValidator.checkBlock(level, worldPos, entry.type())) {
                RunicRebirth.LOGGER.info("[DimensionalOculus] Validation failed at {} — expected {}, found {}",
                        worldPos, entry.type(), level.getBlockState(worldPos).getBlock());
                return ValidationResult.INVALID;
            }

            switch (entry.type()) {
                case OCULUS_PORTAL -> portalPos = worldPos;
                case OCULUS_CONTROLLER -> controllerPos = worldPos;
                case RUNIC_STONE_PILLAR -> pillarPositions.add(worldPos);
                default -> {}
            }
        }

//        RunicRebirth.LOGGER.info("[DimensionalOculus] Structure valid at {} facing {}", framePos, orientation);
        return new ValidationResult(true, portalPos, controllerPos, List.copyOf(pillarPositions));
    }

    private static BlockPos computeOrigin(BlockPos framePos, Direction orientation) {
        int lx = 2, lz = 2;
        int[] rotated = AbstractMultiblockValidator.rotate(lx, lz, orientation);
        return framePos.offset(-rotated[0], -1, -rotated[1]);
    }
}
