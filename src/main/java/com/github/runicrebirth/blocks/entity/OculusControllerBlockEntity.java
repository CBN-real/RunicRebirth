package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.multiblock.DimensionalOculusValidator;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModBlocks;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class OculusControllerBlockEntity extends BlockEntity implements GeoBlockEntity {

    public enum AnimState { IDLE, ACTIVATING, DEACTIVATING }

    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_ACTIVATING = RawAnimation.begin().thenPlay("initiate_activated").thenLoop("hold_activated");
    private static final RawAnimation ANIM_DEACTIVATING = RawAnimation.begin().thenPlay("end_activated").thenLoop("idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean active = false;
    private boolean playerNearby = false;
    private AnimState animState = AnimState.IDLE;
    private int revalidateTimer = 0;

    private BlockPos portalPos;
    private List<BlockPos> pillarPositions = new ArrayList<>();

    public OculusControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OCULUS_CONTROLLER.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            return switch (animState) {
                case IDLE -> state.setAndContinue(ANIM_IDLE);
                case ACTIVATING -> state.setAndContinue(ANIM_ACTIVATING);
                case DEACTIVATING -> state.setAndContinue(ANIM_DEACTIVATING);
            };
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public boolean isActive() { return active; }
    public BlockPos getPortalPos() { return portalPos; }
    public List<BlockPos> getPillarPositions() { return pillarPositions; }

    public void activate(BlockPos portalPos, List<BlockPos> pillarPositions) {
        this.active = true;
        this.portalPos = portalPos;
        this.pillarPositions = new ArrayList<>(pillarPositions);
        setChanged();
    }

    public void deactivate() {
        if (level != null && !level.isClientSide()) {
            for (BlockPos pos : pillarPositions) {
                if (level.getBlockState(pos).is(ModBlocks.OCULUS_PILLAR.get())) {
                    level.setBlock(pos, ModBlocks.RUNIC_STONE_PILLAR.get().defaultBlockState(), Block.UPDATE_ALL);
                }
            }
            if (portalPos != null) {
                BlockEntity be = level.getBlockEntity(portalPos);
                if (be instanceof OculusPortalBlockEntity portal) {
                    portal.clearSelectedDungeon();
                    portal.setAnimState(OculusPortalBlockEntity.AnimState.INACTIVE);
                }
            }
        }
        this.active = false;
        this.portalPos = null;
        this.pillarPositions.clear();
        setAnimState(AnimState.IDLE);
    }

    private void setAnimState(AnimState newState) {
        if (this.animState != newState) {
            this.animState = newState;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
                if (newState == AnimState.ACTIVATING || newState == AnimState.DEACTIVATING) {
                    level.playSound(null, worldPosition, ModSounds.OCULUS_CONTROLLER_OPEN.get(),
                            SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OculusControllerBlockEntity be) {
        if (level.isClientSide()) return;
        if (!be.active) {
            be.setAnimState(AnimState.IDLE);
            return;
        }

        if (++be.revalidateTimer >= 20) {
            be.revalidateTimer = 0;
//            RunicRebirth.LOGGER.info("Oculus Disable Check");
            if (be.portalPos != null && !DimensionalOculusValidator.validate(level, be.portalPos)) {
//                RunicRebirth.LOGGER.info("*Oculus Disabled*");
                be.deactivate();
                return;
            }
        }

        boolean wasNearby = be.playerNearby;
        be.playerNearby = !level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(3.0)).isEmpty();

        if (!wasNearby && be.playerNearby) {
            be.setAnimState(AnimState.ACTIVATING);
        } else if (wasNearby && !be.playerNearby) {
            be.setAnimState(AnimState.DEACTIVATING);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("active", active);
        tag.putString("animState", animState.name());
        if (portalPos != null) {
            tag.putLong("portalPos", portalPos.asLong());
        }
        if (!pillarPositions.isEmpty()) {
            tag.putLongArray("pillarPositions", pillarPositions.stream().mapToLong(BlockPos::asLong).toArray());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        active = tag.getBoolean("active");
        try {
            animState = AnimState.valueOf(tag.getString("animState"));
        } catch (IllegalArgumentException e) {
            animState = AnimState.IDLE;
        }
        if (tag.contains("portalPos")) {
            portalPos = BlockPos.of(tag.getLong("portalPos"));
        }
        pillarPositions.clear();
        if (tag.contains("pillarPositions")) {
            for (long packed : tag.getLongArray("pillarPositions")) {
                pillarPositions.add(BlockPos.of(packed));
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
