package com.github.runicrebirth.blocks;

import com.github.runicrebirth.blocks.entity.AncientArcaneTurretBlockEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import com.github.runicrebirth.entities.spells.AbstractProjectileSpellEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

// 8×8×8 pixel (0.5×0.5×0.5 block) turret placeable on any face; fires arcane projectiles at the nearest player.
public class AncientArcaneTurretBlock extends BaseEntityBlock {

    public static final MapCodec<AncientArcaneTurretBlock> CODEC = simpleCodec(AncientArcaneTurretBlock::new);
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape FLOOR   = Block.box(4,  0,  4, 12,  8, 12);
    private static final VoxelShape CEILING = Block.box(4,  8,  4, 12, 16, 12);
    private static final VoxelShape WALL_N  = Block.box(4,  4,  8, 12, 12, 16);
    private static final VoxelShape WALL_S  = Block.box(4,  4,  0, 12, 12,  8);
    private static final VoxelShape WALL_E  = Block.box(0,  4,  4,  8, 12, 12);
    private static final VoxelShape WALL_W  = Block.box(8,  4,  4, 16, 12, 12);

    public AncientArcaneTurretBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACE, AttachFace.FLOOR)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction horizontal = context.getHorizontalDirection();
        return switch (clickedFace) {
            case UP   -> defaultBlockState().setValue(FACE, AttachFace.FLOOR).setValue(FACING, horizontal);
            case DOWN -> defaultBlockState().setValue(FACE, AttachFace.CEILING).setValue(FACING, horizontal);
            default   -> defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, clickedFace);
        };
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return switch (state.getValue(FACE)) {
            case FLOOR   -> level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
            case CEILING -> level.getBlockState(pos.above()).isFaceSturdy(level, pos.above(), Direction.DOWN);
            case WALL    -> level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()))
                                 .isFaceSturdy(level, pos.relative(state.getValue(FACING).getOpposite()), state.getValue(FACING));
        };
    }

    public static Direction getFireDirection(BlockState state) {
        return switch (state.getValue(FACE)) {
            case FLOOR   -> Direction.UP;
            case CEILING -> Direction.DOWN;
            case WALL    -> state.getValue(FACING);
        };
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) return;
        if (level.getBlockEntity(pos) instanceof AncientArcaneTurretBlockEntity be) {
            float dmg = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            be.damage(dmg);
        }
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (level.isClientSide()) return;
        if (!(level.getBlockEntity(hit.getBlockPos()) instanceof AncientArcaneTurretBlockEntity be)) return;
        float dmg = 0f;
        if (projectile instanceof AbstractProjectileSpellEntity spell) {
            dmg = spell.getDamage();
        } else if (projectile instanceof AbstractArrow) {
            dmg = 2.0f; // AbstractArrow has no public damage getter in 1.21.5
        }
        if (dmg > 0f) {
            be.damage(dmg);
            projectile.discard();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AncientArcaneTurretBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type,
                ModBlockEntities.ANCIENT_ARCANE_TURRET.get(),
                AncientArcaneTurretBlockEntity::serverTick);
    }
}
