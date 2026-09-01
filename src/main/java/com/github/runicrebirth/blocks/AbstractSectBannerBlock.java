package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.AbstractSectBannerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public abstract class AbstractSectBannerBlock extends BaseEntityBlock {

    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty ROTATION_16 = BlockStateProperties.ROTATION_16;

    private static final VoxelShape FLOOR   = Block.box(6,  0,  6, 10, 16, 10);
    private static final VoxelShape CEILING = Block.box(6,  0,  6, 10, 16, 10);
    private static final VoxelShape WALL_N  = Block.box(0,  0, 14, 16, 16, 16);
    private static final VoxelShape WALL_S  = Block.box(0,  0,  0, 16, 16,  2);
    private static final VoxelShape WALL_E  = Block.box(0,  0,  0,  2, 16, 16);
    private static final VoxelShape WALL_W  = Block.box(14, 0,  0, 16, 16, 16);

    protected AbstractSectBannerBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any()
                .setValue(FACE, AttachFace.FLOOR)
                .setValue(FACING, Direction.NORTH)
                .setValue(ROTATION_16, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, ROTATION_16);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction clicked = ctx.getClickedFace();
        Direction horizontal = ctx.getHorizontalDirection();
        int rot16 = Mth.floor(ctx.getRotation() * 16.0F / 360.0F + 0.5F) & 15;
        return switch (clicked) {
            case UP   -> defaultBlockState().setValue(FACE, AttachFace.FLOOR)  .setValue(FACING, horizontal).setValue(ROTATION_16, rot16);
            case DOWN -> defaultBlockState().setValue(FACE, AttachFace.CEILING).setValue(FACING, horizontal).setValue(ROTATION_16, 0);
            default   -> defaultBlockState().setValue(FACE, AttachFace.WALL)   .setValue(FACING, clicked)   .setValue(ROTATION_16, 0);
        };
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return switch (state.getValue(FACE)) {
            case FLOOR   -> level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
            case CEILING -> level.getBlockState(pos.above()).isFaceSturdy(level, pos.above(), Direction.DOWN);
            case WALL    -> level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()))
                                 .isFaceSturdy(level, pos.relative(state.getValue(FACING).getOpposite()),
                                               state.getValue(FACING));
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACE)) {
            case FLOOR   -> FLOOR;
            case CEILING -> CEILING;
            case WALL    -> switch (state.getValue(FACING)) {
                case NORTH -> WALL_N;
                case SOUTH -> WALL_S;
                case EAST  -> WALL_E;
                case WEST  -> WALL_W;
                default    -> FLOOR;
            };
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
        CollisionContext context) {
      return Shapes.empty();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof AbstractSectBannerBlockEntity banner) {
            ItemStack drop = new ItemStack(this.asItem());
            DyeColor color = banner.getBaseColor();
            if (color != null) {
                drop.set(DataComponents.BASE_COLOR, color);
            }
            BannerPatternLayers patterns = banner.getPatterns();
            if (patterns != null && !patterns.layers().isEmpty()) {
                drop.set(DataComponents.BANNER_PATTERNS, patterns);
            }
            return List.of(drop);
        }
        return super.getDrops(state, params);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this.asItem());
        if (level.getBlockEntity(pos) instanceof AbstractSectBannerBlockEntity be) {
            DyeColor color = be.getBaseColor();
            if (color != null) stack.set(DataComponents.BASE_COLOR, color);
            BannerPatternLayers patterns = be.getPatterns();
            if (patterns != null && !patterns.layers().isEmpty())
                stack.set(DataComponents.BANNER_PATTERNS, patterns);
        }
        return stack;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            net.minecraft.world.level.Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }
}
