package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RunesteelCacheBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider, Container {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation OPEN = RawAnimation.begin().thenPlayAndHold("open");
    private static final RawAnimation CLOSE_THEN_IDLE = RawAnimation.begin().thenPlay("close").thenLoop("idle");

    private static final int SLOTS = 27;
    private static final int CLOSE_ANIM_TICKS = 30;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

    private int viewerCount = 0;
    private int closingTicks = 0;
    // 0=idle, 1=open, 2=closing
    private byte animState = 0;

    public RunesteelCacheBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUNESTEEL_CACHE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RunesteelCacheBlockEntity be) {
        if (be.closingTicks > 0) {
            be.closingTicks--;
            if (be.closingTicks == 0) {
                be.animState = 0;
                be.sync();
            }
        }
    }

    public void startOpen(Player player) {
        if (remove || player.isSpectator()) return;
        viewerCount++;
        if (viewerCount == 1) {
            animState = 1;
            closingTicks = 0;
            sync();
        }
    }

    public void stopOpen(Player player) {
        if (remove || player.isSpectator()) return;
        viewerCount--;
        if (viewerCount <= 0) {
            viewerCount = 0;
            animState = 2;
            closingTicks = CLOSE_ANIM_TICKS;
            sync();
        }
    }

    private void sync() {
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (animState == 1) return state.setAndContinue(OPEN);
            if (animState == 2) return state.setAndContinue(CLOSE_THEN_IDLE);
            return state.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.runicrebirth.runesteel_cache");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        startOpen(player);
        RunesteelCacheBlockEntity self = this;
        return new ChestMenu(MenuType.GENERIC_9x3, containerId, playerInventory, this, 3) {
            @Override
            public void removed(Player p) {
                super.removed(p);
                self.stopOpen(p);
            }
        };
    }

    // Container interface
    @Override
    public int getContainerSize() { return SLOTS; }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) { return items.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() { items.clear(); }

    public void dropContents(Level level, BlockPos pos) {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
        items.clear();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putByte("AnimState", animState);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Items")) {
            items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, items, registries);
        }
        if (tag.contains("AnimState")) {
            animState = tag.getByte("AnimState");
            if (animState == 2) {
                animState = 0;
                closingTicks = 0;
            }
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putByte("AnimState", animState);
        return tag;
    }

}
