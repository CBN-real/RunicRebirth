package com.github.runicrebirth.blocks.multiblock;

import com.github.runicrebirth.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultiblockValidator {

    public record Entry(int dx, int dy, int dz, StructureBlock type) {}

    protected AbstractMultiblockValidator() {}

    public static List<Entry> flattenLayers(StructureBlock[][][] layers) {
        var entries = new ArrayList<Entry>();
        for (int y = 0; y < layers.length; y++) {
            for (int z = 0; z < layers[y].length; z++) {
                for (int x = 0; x < layers[y][z].length; x++) {
                    entries.add(new Entry(x, y, z, layers[y][z][x]));
                }
            }
        }
        return List.copyOf(entries);
    }

    public static BlockPos rotateAndOffset(BlockPos origin, int dx, int dy, int dz, Direction orientation) {
        int[] rotated = rotate(dx, dz, orientation);
        return origin.offset(rotated[0], dy, rotated[1]);
    }

    public static int[] rotate(int x, int z, Direction orientation) {
        return switch (orientation) {
            case SOUTH -> new int[]{x, z};
            case NORTH -> new int[]{-x, -z};
            case EAST  -> new int[]{z, -x};
            case WEST  -> new int[]{-z, x};
            default    -> new int[]{x, z};
        };
    }

    public static boolean checkBlock(Level level, BlockPos pos, StructureBlock expected) {
        BlockState state = level.getBlockState(pos);
        return switch (expected) {
            case RUNIC_STONE       -> state.is(ModBlocks.RUNIC_STONE.get());
            case RUNIC_STONE_SLAB  -> state.is(ModBlocks.RUNIC_STONE_SLAB.get());
            case RUNIC_STONE_PILLAR -> state.is(ModBlocks.RUNIC_STONE_PILLAR.get())
                    || state.is(ModBlocks.OCULUS_PILLAR.get());
            case OCULUS_PORTAL     -> state.is(ModBlocks.OCULUS_PORTAL.get());
            case OCULUS_CONTROLLER -> state.is(ModBlocks.OCULUS_CONTROLLER.get());
            case RUNESTEEL_PYLON   -> state.is(ModBlocks.RUNESTEEL_PYLON.get());
            case INFUSION_ALTAR    -> state.is(ModBlocks.INFUSION_ALTAR.get());
          case INFUSION_ALTAR_PROXY ->  state.is(ModBlocks.INFUSION_ALTAR_PROXY.get());
            case RUNIC_ANVIL       -> state.is(ModBlocks.RUNIC_ANVIL.get());
            case AIR               -> state.isAir();
        };
    }
}
