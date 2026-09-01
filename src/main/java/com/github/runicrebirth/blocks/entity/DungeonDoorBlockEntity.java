package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.network.ImpactEffectS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class DungeonDoorBlockEntity extends BlockEntity implements GeoBlockEntity {

    public enum AnimState { CLOSED, OPENING, OPEN, CLOSING }

    private static final RawAnimation ANIM_OPEN   = RawAnimation.begin().thenPlayAndHold("open");
    private static final RawAnimation ANIM_CLOSED = RawAnimation.begin().thenPlayAndHold("closed");
    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenPlayAndHold("idle");
    private static final int TRANSITION_TICKS = 80; // 4 seconds

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private AnimState animState = AnimState.CLOSED;
    private int transitionTick = 0;
    private long transitionStartTime = 0L;

    public DungeonDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_DOOR.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<DungeonDoorBlockEntity>("controller", 0, state -> switch (animState) {
            case OPENING, OPEN -> state.setAndContinue(ANIM_OPEN);
            case CLOSING -> state.setAndContinue(ANIM_CLOSED);
            default -> state.setAndContinue(ANIM_IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public AnimState getAnimState() {
        return animState;
    }

    public long getTransitionStartTime() {
        return transitionStartTime;
    }

    public void setAnimatingOpen(boolean open) {
        if (open  && animState != AnimState.CLOSED) return;
        if (!open && animState != AnimState.OPEN)   return;
        animState = open ? AnimState.OPENING : AnimState.CLOSING;
        transitionTick = 0;
        transitionStartTime = level != null ? level.getGameTime() : 0L;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            if (!level.isClientSide()) {
                playDoorEffects((ServerLevel) level);
            }
        }
    }

    private void playDoorEffects(ServerLevel level) {
        var sound = animState == AnimState.CLOSING ? ModSounds.DUNGEON_DOOR_CLOSING.get() : ModSounds.DUNGEON_DOOR.get();
        level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1.5f, 1.0f);
        Vec3 center = Vec3.atCenterOf(worldPosition).add(0, 1.5, 0);
        ImpactEffectS2CPacket packet = new ImpactEffectS2CPacket(center, 0f, 0, 0.5f, 20);
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(center) < 64.0 * 64.0) {
                PacketDistributor.sendToPlayer(player, packet);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DungeonDoorBlockEntity be) {
        if (level.isClientSide()) return;
        if (be.animState == AnimState.OPENING || be.animState == AnimState.CLOSING) {
            be.transitionTick++;
            if (be.transitionTick >= TRANSITION_TICKS) {
                be.animState = be.animState == AnimState.OPENING ? AnimState.OPEN : AnimState.CLOSED;
                be.transitionTick = 0;
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("animState", animState.name());
        output.putInt("transitionTick", transitionTick);
        output.putLong("transitionStartTime", transitionStartTime);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        try {
            animState = AnimState.valueOf(input.getStringOr("animState", "CLOSED"));
        } catch (IllegalArgumentException e) {
            animState = AnimState.CLOSED;
        }
        transitionTick = input.getIntOr("transitionTick", 0);
        transitionStartTime = input.getLongOr("transitionStartTime", 0L);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
