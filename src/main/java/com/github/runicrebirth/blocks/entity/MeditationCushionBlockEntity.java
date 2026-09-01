package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.entities.EarthVeinCircleEntity;
import com.github.runicrebirth.entities.EarthVeinRunesEntity;
import com.github.runicrebirth.entities.SeatEntity;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.network.OpenUnlockScreenS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class MeditationCushionBlockEntity extends BlockEntity {

    private boolean active = false;
    private int earthVeinEntityId = -1;
    private int runesEntityId = -1;

    public MeditationCushionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MEDITATION_CUSHION.get(), pos, state);
    }

    public boolean isActive() { return active; }
    public int getEarthVeinEntityId() { return earthVeinEntityId; }

    public void setActive(boolean active) {
        this.active = active;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            if (!level.isClientSide()) {
                if (active) {
                    spawnRunesEntity();
                } else {
                    despawnRunesEntity();
                }
            }
        }
    }

    private void spawnRunesEntity() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (runesEntityId != -1 && serverLevel.getEntity(runesEntityId) instanceof EarthVeinRunesEntity) return;
        EarthVeinRunesEntity entity = new EarthVeinRunesEntity(ModEntities.EARTH_VEIN_RUNES.get(), serverLevel);
        entity.setPos(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5);
        serverLevel.addFreshEntity(entity);
        runesEntityId = entity.getId();
    }

    private void despawnRunesEntity() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (runesEntityId == -1) return;
        Entity entity = serverLevel.getEntity(runesEntityId);
        if (entity instanceof EarthVeinRunesEntity) {
            entity.discard();
        }
        runesEntityId = -1;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MeditationCushionBlockEntity be) {
        if (level.isClientSide()) return;
        if (be.earthVeinEntityId == -1) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        Entity entity = serverLevel.getEntity(be.earthVeinEntityId);
        if (entity == null || !(entity instanceof EarthVeinCircleEntity earthVeinEntity)) {
            be.earthVeinEntityId = -1;
            return;
        }

        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class,
                new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
        boolean seatEmpty = seats.isEmpty() || seats.get(0).getPassengers().isEmpty();
        if (seatEmpty) {
            earthVeinEntity.triggerEnd();
            be.earthVeinEntityId = -1;
        }
    }

    public void onPlayerSit(ServerPlayer player) {
        if (!active) return;
        if (earthVeinEntityId != -1) return;
        Level lvl = getLevel();
        if (!(lvl instanceof ServerLevel serverLevel)) return;

        EarthVeinCircleEntity entity = new EarthVeinCircleEntity(ModEntities.EARTH_VEIN_CIRCLE.get(), serverLevel);
        entity.setPos(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5);
        serverLevel.addFreshEntity(entity);
        earthVeinEntityId = entity.getId();

        PacketDistributor.sendToPlayer(player, new OpenUnlockScreenS2CPacket(worldPosition));
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            level.getEntitiesOfClass(SeatEntity.class,
                new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1))
                .forEach(s -> { s.ejectPassengers(); s.discard(); });
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide() && active) {
            spawnRunesEntity();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("active", active);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        active = input.getBooleanOr("active", false);
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
