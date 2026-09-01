package com.github.runicrebirth.entities;

import com.github.runicrebirth.blocks.entity.CrumblingPlatformBlockEntity;
import net.minecraft.core.BlockPos;
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

        if (!this.level().isClientSide() && this.time >= RESPAWN_TICKS) {
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
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        output.store("BlockState", BlockState.CODEC, this.fallingState);
        output.putInt("Time", this.time);
        output.putBoolean("DropItem", false);
        output.putBoolean("CancelDrop", true);
        output.putBoolean("HasLanded", this.hasLanded);
        if (this.originalPos != null) {
            output.store("OriginalPos", BlockPos.CODEC, this.originalPos);
        }
        if (this.originalPlatformState != null) {
            output.store("OriginalPlatformState", BlockState.CODEC, this.originalPlatformState);
        }
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        input.read("BlockState", BlockState.CODEC).ifPresent(s -> this.fallingState = s);
        this.time = input.getIntOr("Time", 0);
        this.hasLanded = input.getBooleanOr("HasLanded", false);
        if (this.hasLanded) this.setInvisible(true);
        this.originalPos = input.read("OriginalPos", BlockPos.CODEC).orElse(null);
        this.originalPlatformState = input.read("OriginalPlatformState", BlockState.CODEC).orElse(null);
    }

}
