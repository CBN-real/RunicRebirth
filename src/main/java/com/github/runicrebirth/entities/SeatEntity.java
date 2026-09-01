package com.github.runicrebirth.entities;

import com.github.runicrebirth.blocks.MeditationCushionBlock;
import com.github.runicrebirth.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SeatEntity extends Entity {

    private BlockPos homePos;

    public SeatEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setInvisible(true);
    }

    public SeatEntity(Level level, BlockPos pos) {
        this(ModEntities.SEAT.get(), level);
        this.homePos = pos;
        setPos(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && homePos != null) {
            if (!(level().getBlockState(homePos).getBlock() instanceof MeditationCushionBlock)) {
                ejectPassengers();
                discard();
                return;
            }
            if (getPassengers().isEmpty()) {
                discard();
            }
        }
    }

    public BlockPos getHomePos() {
        return homePos;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {}

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
