package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.blocks.multiblock.InfusionAltarValidator;
import com.github.runicrebirth.crafting.InfusionRecipe;
import com.github.runicrebirth.init.ModBlocks;
import com.github.runicrebirth.crafting.InfusionRecipeInput;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModRecipeTypes;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InfusionAltarBlockEntity extends BlockEntity implements GeoBlockEntity {

    public static final int MAX_ITEMS = 8;
    public static final int CRAFTING_TICKS = 200;

    public enum AnimState { IDLE, ACTIVATING, ACTIVATED, CRAFTING, HOLDING_RESULT, DEACTIVATING }

    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_ACTIVATING = RawAnimation.begin().thenPlay("initiate_activated").thenLoop("hold_activated");
    private static final RawAnimation ANIM_ACTIVATED = RawAnimation.begin().thenLoop("hold_activated");
    private static final RawAnimation ANIM_CRAFTING = RawAnimation.begin().thenPlay("infusing").thenLoop("hold_finished");
    private static final RawAnimation ANIM_HOLDING_RESULT = RawAnimation.begin().thenLoop("hold_finished");
    private static final RawAnimation ANIM_DEACTIVATING = RawAnimation.begin().thenPlay("end_activated").thenLoop("idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private AnimState animState = AnimState.IDLE;
    private boolean active = false;
    private boolean playerNearby = false;
    private boolean structureValid = false;
    private int revalidateTimer = 0;

    private List<BlockPos> pylonPositions = new ArrayList<>();
    private final NonNullList<ItemStack> items = NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY);
    private int itemCount = 0;

    private int craftingTicks = 0;
    private int craftingTotalTicks = 0;
    private long craftingStartTime = 0;

    private ItemStack resultItem = ItemStack.EMPTY;
    private ResourceLocation lastRecipeId = null;

    public InfusionAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFUSION_ALTAR.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            return switch (animState) {
                case IDLE -> state.setAndContinue(ANIM_IDLE);
                case ACTIVATING -> state.setAndContinue(ANIM_ACTIVATING);
                case ACTIVATED -> state.setAndContinue(ANIM_ACTIVATED);
                case CRAFTING -> state.setAndContinue(ANIM_CRAFTING);
                case HOLDING_RESULT -> state.setAndContinue(ANIM_HOLDING_RESULT);
                case DEACTIVATING -> state.setAndContinue(ANIM_DEACTIVATING);
            };
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public boolean isActive() { return active; }
    public boolean isStructureValid() { return structureValid; }
    public AnimState getAnimState() { return animState; }
    public int getItemCount() { return itemCount; }
    public NonNullList<ItemStack> getItems() { return items; }
    public int getCraftingTicks() { return craftingTicks; }
    public int getCraftingTotalTicks() { return craftingTotalTicks; }
    public boolean isCrafting() { return craftingTicks > 0; }
    public long getCraftingStartTime() { return craftingStartTime; }

    public boolean hasResult() { return !resultItem.isEmpty(); }
    public ItemStack getResultItem() { return resultItem; }
    public boolean hasLastRecipe() { return lastRecipeId != null; }
    public ResourceLocation getLastRecipeId() { return lastRecipeId; }

    public ItemStack removeResult() {
        ItemStack removed = resultItem.copy();
        resultItem = ItemStack.EMPTY;
        setAnimState(AnimState.ACTIVATED);
        syncToClient();
        return removed;
    }

    public void addItem(ItemStack stack) {
        for (int i = 0; i < MAX_ITEMS; i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.copy());
                itemCount++;
                lastRecipeId = null;
                if (level != null) {
                    level.playSound(null, worldPosition, ModSounds.INFUSION_ADD_ITEM.get(),
                            SoundSource.BLOCKS, 0.9f, 1.3f);
                }
                syncToClient();
                return;
            }
        }
    }

    public ItemStack removeLastItem() {
        for (int i = MAX_ITEMS - 1; i >= 0; i--) {
            if (!items.get(i).isEmpty()) {
                ItemStack removed = items.get(i).copy();
                items.set(i, ItemStack.EMPTY);
                itemCount--;
                syncToClient();
                return removed;
            }
        }
        return ItemStack.EMPTY;
    }

    public void dropAllItems() {
        if (level == null) return;
        Vec3 center = Vec3.atCenterOf(worldPosition).add(0, 0.5, 0);
        for (int i = 0; i < MAX_ITEMS; i++) {
            if (!items.get(i).isEmpty()) {
                ItemEntity entity = new ItemEntity(level, center.x, center.y, center.z, items.get(i).copy());
                entity.setDefaultPickUpDelay();
                level.addFreshEntity(entity);
                items.set(i, ItemStack.EMPTY);
            }
        }
        itemCount = 0;
        if (!resultItem.isEmpty()) {
            ItemEntity resultEntity = new ItemEntity(level, center.x, center.y, center.z, resultItem.copy());
            resultEntity.setDefaultPickUpDelay();
            level.addFreshEntity(resultEntity);
            resultItem = ItemStack.EMPTY;
        }
    }

    public boolean tryActivate() {
        if (level == null || level.isClientSide()) return false;
        if (active) return false;

        var result = InfusionAltarValidator.validateFull(level, worldPosition);
        if (!result.valid()) return false;

        active = true;
        structureValid = true;
        pylonPositions = new ArrayList<>(result.pillarPositions());
        for (BlockPos p : pylonPositions) {
            if (level.getBlockState(p).is(ModBlocks.RUNIC_STONE_PILLAR.get())) {
                level.setBlock(p, ModBlocks.OCULUS_PILLAR.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
        syncToClient();
        return true;
    }

    public void deactivate() {
        if (level != null && !level.isClientSide()) {
            for (BlockPos p : pylonPositions) {
                if (level.getBlockState(p).is(ModBlocks.OCULUS_PILLAR.get())) {
                    level.setBlock(p, ModBlocks.RUNIC_STONE_PILLAR.get().defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
        active = false;
        structureValid = false;
        pylonPositions.clear();
        craftingTicks = 0;
        craftingTotalTicks = 0;
        lastRecipeId = null;
        setAnimState(AnimState.IDLE);
    }

    public boolean tryStartCrafting() {
        if (level == null || level.isClientSide()) return false;
        if (!active || craftingTicks > 0 || itemCount == 0 || hasResult()) return false;

        Optional<RecipeHolder<InfusionRecipe>> match = findMatchingRecipe();
        if (match.isPresent()) {
            craftingTicks = CRAFTING_TICKS;
            craftingTotalTicks = CRAFTING_TICKS;
            craftingStartTime = level.getGameTime();
            level.playSound(null, worldPosition, ModSounds.INFUSION_INFUSING.get(),
                    SoundSource.BLOCKS, 1.0f, 1.0f);
            setAnimState(AnimState.CRAFTING);
            return true;
        }
        return false;
    }

    public boolean tryRepeatRecipe(Player player) {
        if (level == null || level.isClientSide()) return false;
        if (lastRecipeId == null || hasResult() || isCrafting()) return false;

        Optional<RecipeHolder<?>> holder = level.getRecipeManager().byKey(lastRecipeId);
        if (holder.isEmpty() || !(holder.get().value() instanceof InfusionRecipe recipe)) {
            lastRecipeId = null;
            syncToClient();
            return false;
        }

        var ingredients = recipe.getIngredients();
        Map<Integer, Integer> slotConsumption = new HashMap<>();

        for (var ingredient : ingredients) {
            boolean found = false;
            for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                ItemStack inSlot = player.getInventory().getItem(slot);
                if (inSlot.isEmpty() || !ingredient.test(inSlot)) continue;
                int alreadyUsed = slotConsumption.getOrDefault(slot, 0);
                if (inSlot.getCount() - alreadyUsed > 0) {
                    slotConsumption.merge(slot, 1, Integer::sum);
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        for (var entry : slotConsumption.entrySet()) {
            player.getInventory().getItem(entry.getKey()).shrink(entry.getValue());
        }

        resultItem = recipe.getResultItem(level.registryAccess()).copy();
        level.playSound(null, worldPosition, ModSounds.INFUSION_QUICK_INFUSION.get(),
                SoundSource.BLOCKS, 1.0f, 1.0f);
        setAnimState(AnimState.HOLDING_RESULT);
        syncToClient();
        return true;
    }

    public Optional<RecipeHolder<InfusionRecipe>> findMatchingRecipe() {
        if (level == null || itemCount == 0) return Optional.empty();
        List<ItemStack> nonEmpty = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) nonEmpty.add(stack);
        }
        InfusionRecipeInput input = new InfusionRecipeInput(nonEmpty);
        return level.getRecipeManager().getRecipeFor(ModRecipeTypes.INFUSION_TYPE.get(), input, level);
    }

    private void completeCrafting() {
        if (level == null || level.isClientSide()) return;

        Optional<RecipeHolder<InfusionRecipe>> match = findMatchingRecipe();
        if (match.isEmpty()) {
            craftingTicks = 0;
            craftingTotalTicks = 0;
            return;
        }

        lastRecipeId = match.get().id();

        InfusionRecipe recipe = match.get().value();
        List<ItemStack> nonEmpty = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) nonEmpty.add(stack);
        }
        InfusionRecipeInput input = new InfusionRecipeInput(nonEmpty);
        ItemStack result = recipe.assemble(input, level.registryAccess());

        for (var ingredient : recipe.getIngredients()) {
            for (int i = 0; i < MAX_ITEMS; i++) {
                if (!items.get(i).isEmpty() && ingredient.test(items.get(i))) {
                    items.get(i).shrink(1);
                    if (items.get(i).isEmpty()) {
                        items.set(i, ItemStack.EMPTY);
                        itemCount--;
                    }
                    break;
                }
            }
        }

        resultItem = result;
        craftingTicks = 0;
        craftingTotalTicks = 0;
        syncToClient();
    }

    private void setAnimState(AnimState newState) {
        if (this.animState != newState) {
            this.animState = newState;
            if (level != null && (newState == AnimState.ACTIVATING || newState == AnimState.DEACTIVATING)) {
                level.playSound(null, worldPosition, ModSounds.INFUSION_ALTAR_ACTIVATE.get(),
                        SoundSource.BLOCKS, 0.75f, 0.9f);
            }
            syncToClient();
        }
    }

    private void syncToClient() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, InfusionAltarBlockEntity be) {
        if (level.isClientSide()) return;

        if (!be.active) return;

        if (++be.revalidateTimer >= 20) {
            be.revalidateTimer = 0;
            var result = InfusionAltarValidator.validateFull(level, pos);
            be.structureValid = result.valid();
            if (!be.structureValid) {
                be.deactivate();
                return;
            }
        }

        if (be.craftingTicks > 0) {
            be.craftingTicks--;
            if (be.craftingTicks <= 0) {
                be.completeCrafting();
            }
            return;
        }

        if (be.hasResult()) return;

        boolean wasNearby = be.playerNearby;
        be.playerNearby = !level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(5.0)).isEmpty();

        if (!wasNearby && be.playerNearby) {
            be.setAnimState(AnimState.ACTIVATING);
        } else if (wasNearby && !be.playerNearby) {
            be.setAnimState(AnimState.DEACTIVATING);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("animState", animState.name());
        tag.putBoolean("active", active);
        tag.putBoolean("structureValid", structureValid);
        tag.putInt("craftingTicks", craftingTicks);
        tag.putInt("craftingTotalTicks", craftingTotalTicks);
        tag.putLong("craftingStartTime", craftingStartTime);

        ContainerHelper.saveAllItems(tag, items, registries);

        if (!resultItem.isEmpty()) {
            tag.put("resultItem", resultItem.save(registries));
        }

        if (lastRecipeId != null) {
            tag.putString("lastRecipeId", lastRecipeId.toString());
        }

        if (!pylonPositions.isEmpty()) {
            tag.putLongArray("pylonPositions", pylonPositions.stream().mapToLong(BlockPos::asLong).toArray());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        try {
            animState = AnimState.valueOf(tag.getString("animState"));
        } catch (IllegalArgumentException e) {
            animState = AnimState.IDLE;
        }
        active = tag.getBoolean("active");
        structureValid = tag.getBoolean("structureValid");
        craftingTicks = tag.getInt("craftingTicks");
        craftingTotalTicks = tag.getInt("craftingTotalTicks");
        craftingStartTime = tag.getLong("craftingStartTime");

        items.clear();
        ContainerHelper.loadAllItems(tag, items, registries);
        itemCount = 0;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) itemCount++;
        }

        if (tag.contains("resultItem")) {
            resultItem = ItemStack.parse(registries, tag.getCompound("resultItem")).orElse(ItemStack.EMPTY);
        } else {
            resultItem = ItemStack.EMPTY;
        }

        if (tag.contains("lastRecipeId")) {
            lastRecipeId = ResourceLocation.tryParse(tag.getString("lastRecipeId"));
        } else {
            lastRecipeId = null;
        }

        pylonPositions.clear();
        if (tag.contains("pylonPositions")) {
            for (long packed : tag.getLongArray("pylonPositions")) {
                pylonPositions.add(BlockPos.of(packed));
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
