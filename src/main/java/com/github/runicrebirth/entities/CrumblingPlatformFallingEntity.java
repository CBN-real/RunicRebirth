package com.github.runicrebirth.entities;

import com.github.runicrebirth.blocks.entity.CrumblingPlatformBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CrumblingPlatformFallingEntity extends FallingBlockEntity {

    private static final int RESPAWN_TICKS = 600; // 30 seconds

    private BlockState fallingState = Blocks.STONE.defaultBlockState();
    private BlockPos originalPos = null;
    private BlockState originalPlatformState = null;
    private boolean hasLanded = false;

    public CrumblingPlatformFallingEntity(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
        this.dropItem = false;
    }

    public void setFallingState(BlockState state) {
        this.fallingState = state;
    }

    public void setRespawnData(BlockPos pos, BlockState platformState) {
        this.originalPos = pos.immutable();
        this.originalPlatformState = platformState;
    }

    @Override
    public BlockState getBlockState() {
        return this.fallingState;
    }

    @Override
    public void recreateFromPacket(net.minecraft.network.protocol.game.ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.fallingState = Block.stateById(packet.getData());
    }

    @Override
    public void tick() {
        if (this.getBlockState().isAir()) {
            this.discard();
            return;
        }

        this.time++;

        if (!hasLanded) {
            this.applyGravity();
            this.move(MoverType.SELF, this.getDeltaMovement());

            if (this.onGround() || this.horizontalCollision) {
                hasLanded = true;
                this.setInvisible(true);
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
            }
        }

        if (!this.level().isClientSide && this.time >= RESPAWN_TICKS) {
            tryRespawn();
            this.discard();
        }
    }

    private void tryRespawn() {
        if (originalPos == null || originalPlatformState == null) return;
        if (!this.level().getBlockState(originalPos).isAir()) return;

        this.level().setBlock(originalPos, originalPlatformState, 3);
        BlockEntity be = this.level().getBlockEntity(originalPos);
        if (be instanceof CrumblingPlatformBlockEntity platform) {
            platform.setMimickedState(this.fallingState);
        }
        this.level().sendBlockUpdated(originalPos, originalPlatformState, originalPlatformState, 3);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("BlockState", NbtUtils.writeBlockState(this.fallingState));
        compound.putInt("Time", this.time);
        compound.putBoolean("DropItem", false);
        compound.putBoolean("CancelDrop", true);
        compound.putBoolean("HasLanded", this.hasLanded);
        if (this.originalPos != null) {
            compound.put("OriginalPos", NbtUtils.writeBlockPos(this.originalPos));
        }
        if (this.originalPlatformState != null) {
            compound.put("OriginalPlatformState", NbtUtils.writeBlockState(this.originalPlatformState));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("BlockState")) {
            this.fallingState = NbtUtils.readBlockState(
                    this.level().holderLookup(Registries.BLOCK),
                    compound.getCompound("BlockState"));
        }
        if (compound.contains("Time")) {
            this.time = compound.getInt("Time");
        }
        if (compound.contains("HasLanded")) {
            this.hasLanded = compound.getBoolean("HasLanded");
            if (this.hasLanded) this.setInvisible(true);
        }
        if (compound.contains("OriginalPos")) {
            this.originalPos = NbtUtils.readBlockPos(compound, "OriginalPos").orElse(null);
        }
        if (compound.contains("OriginalPlatformState")) {
            this.originalPlatformState = NbtUtils.readBlockState(
                    this.level().holderLookup(Registries.BLOCK),
                    compound.getCompound("OriginalPlatformState"));
        }
    }
}
