package com.github.runicrebirth.entities;

import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.init.ModEntities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class PhantomMinerEntity extends Entity {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
        SynchedEntityData.defineId(PhantomMinerEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_OWNER_ID =
        SynchedEntityData.defineId(PhantomMinerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Direction> DATA_FACE =
        SynchedEntityData.defineId(PhantomMinerEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Float> DATA_DISPLAY_YAW =
        SynchedEntityData.defineId(PhantomMinerEntity.class, EntityDataSerializers.FLOAT);

    private String ownerUUID = "";

    public PhantomMinerEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static PhantomMinerEntity create(ServerPlayer player, Vec3 pos, Direction face) {
        PhantomMinerEntity entity = new PhantomMinerEntity(ModEntities.PHANTOM_MINER.get(), player.level());
        entity.ownerUUID = player.getUUID().toString();
        entity.entityData.set(DATA_OWNER_ID, player.getId());
        entity.entityData.set(DATA_ITEM, player.getMainHandItem().copy());
        entity.entityData.set(DATA_FACE, face);
        entity.entityData.set(DATA_DISPLAY_YAW, player.getYRot());
        entity.setPos(pos.x, pos.y, pos.z);
        return entity;
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            if (ownerUUID.isEmpty()) { discard(); return; }
            ServerPlayer owner = level().getServer().getPlayerList().getPlayer(UUID.fromString(ownerUUID));
            if (owner == null || !owner.isAlive()) { discard(); return; }
            if (MagicData.of(owner).phantomMiningTicks() <= 0) { discard(); return; }

            ItemStack mainhand = owner.getMainHandItem();
            if (!ItemStack.matches(entityData.get(DATA_ITEM), mainhand)) {
                entityData.set(DATA_ITEM, mainhand.copy());
            }
        }
    }

    public void moveTo(Vec3 pos) {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.setPos(pos.x, pos.y, pos.z);
    }

    public void setFace(Direction face) {
        entityData.set(DATA_FACE, face);
    }

    public void setDisplayYaw(float yaw) {
        entityData.set(DATA_DISPLAY_YAW, yaw);
    }

    public String getOwnerUUID() { return ownerUUID; }
    public ItemStack getDisplayItem() { return entityData.get(DATA_ITEM); }
    public Direction getFace() { return entityData.get(DATA_FACE); }
    public float getDisplayYaw() { return entityData.get(DATA_DISPLAY_YAW); }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ITEM, ItemStack.EMPTY);
        builder.define(DATA_OWNER_ID, -1);
        builder.define(DATA_FACE, Direction.NORTH);
        builder.define(DATA_DISPLAY_YAW, 0f);
    }

    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public boolean isPickable() { return false; }

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {}

    @Override
    public void push(double x, double y, double z) {}

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
}
