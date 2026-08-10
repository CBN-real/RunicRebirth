package com.github.runicrebirth.blocks;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.OculusPortalBlockEntity;
import com.github.runicrebirth.blocks.multiblock.DimensionalOculusValidator;
import com.github.runicrebirth.dungeon.DungeonEventHandler;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.dungeon.DungeonTeleporter;
import com.github.runicrebirth.dungeon.DungeonType;
import com.github.runicrebirth.dungeon.gen.DungeonGenerator;
import com.github.runicrebirth.items.SpellWriter;
import com.github.runicrebirth.network.DungeonDataSyncS2CPacket;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OculusPortalBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<OculusPortalBlock> CODEC = simpleCodec(OculusPortalBlock::new);

    private static final Map<UUID, Long> TELEPORT_COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_TICKS = 40;

    public OculusPortalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OculusPortalBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
      BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

      if (stack.getItem() instanceof SpellWriter) {
        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      }
    return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
  }

  @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            boolean valid = DimensionalOculusValidator.validate(level, pos);
            if (valid) {
                player.displayClientMessage(Component.literal("Dimensional Oculus structure found!"), true);
            } else {
                player.displayClientMessage(Component.literal("Dimensional Oculus structure incomplete."), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide()) return;
        if (!(entity instanceof ServerPlayer player)) return;

        long gameTime = level.getGameTime();
        Long lastTeleport = TELEPORT_COOLDOWNS.get(player.getUUID());
        if (lastTeleport != null && gameTime - lastTeleport < COOLDOWN_TICKS) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof OculusPortalBlockEntity portal)) return;
        if (!portal.hasSelectedDungeon()) return;
        if (portal.getAnimState() != OculusPortalBlockEntity.AnimState.ACTIVATING) return;

        DungeonType type = portal.getSelectedDungeonType();
        if (type == null) return;
        int difficulty = portal.getSelectedDifficulty();

        if (DungeonInstanceManager.get().isPlayerInDungeon(player.getUUID())) return;

        TELEPORT_COOLDOWNS.put(player.getUUID(), gameTime);

        ResourceLocation returnDim = player.level().dimension().location();
        BlockPos controllerPos = portal.getControllerPos();
        BlockPos returnPos = controllerPos != null ? controllerPos : pos;

        DungeonInstance instance = DungeonInstanceManager.get().createInstance(
                type, difficulty, returnPos, returnDim);

        DungeonGenerator.generate(player.getServer(), instance);
        DungeonInstanceManager.get().enterInstance(player, instance);
        DungeonTeleporter.teleportToDungeon(player, instance);
        DungeonEventHandler.onEnterDungeon(player, instance);
        DungeonDataSyncS2CPacket.sendTo(player);
    }
}
