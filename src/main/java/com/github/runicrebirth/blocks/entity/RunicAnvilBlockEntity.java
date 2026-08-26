package com.github.runicrebirth.blocks.entity;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.WandStacksData;
import com.github.runicrebirth.blocks.multiblock.RunicAnvilValidator;
import com.github.runicrebirth.crafting.RunicAnvilRecipe;
import com.github.runicrebirth.crafting.RunicAnvilRecipeInput;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModBlocks;
import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.api.item.IRunicDrone;
import com.github.runicrebirth.init.ModDataComponents;
import com.github.runicrebirth.init.ModRecipeTypes;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.items.EnhancementRuneItem;
import com.github.runicrebirth.items.RunicCircuitItem;
import com.github.runicrebirth.items.SpellWriter;
import com.github.runicrebirth.rune.ElementRuneType;
import com.github.runicrebirth.rune.EnhancementRuneData;
import com.github.runicrebirth.rune.RuneType;
import com.github.runicrebirth.rune.RuneTypeRegistry;
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

public class RunicAnvilBlockEntity extends BlockEntity implements GeoBlockEntity {

    public static final int MAX_ITEMS = 5;
    public static final int CRAFTING_TICKS = 120;

    public enum AnimState { IDLE, ACTIVATING, ACTIVATED, CRAFTING, HOLDING_RESULT, DEACTIVATING }

    public enum AnvilAction { REPAIR, INSCRIBE, DEINSCRIBE, ENGRAVE, NO_SLOTS }

    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_ACTIVATING = RawAnimation.begin().thenPlay("initiate_activated").thenLoop("hold_activated");
    private static final RawAnimation ANIM_ACTIVATED = RawAnimation.begin().thenLoop("hold_activated");
    private static final RawAnimation ANIM_CRAFTING = RawAnimation.begin().thenPlay("forging").thenLoop("hold_activated");
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

    public RunicAnvilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUNIC_ANVIL.get(), pos, state);
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

        var result = RunicAnvilValidator.validateFull(level, worldPosition);
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

        AnvilAction action = getAnvilAction();
        RunicRebirth.LOGGER.info("[RunicAnvil] tryStartCrafting action={} itemCount={}", action, itemCount);
        if (action != null) {
            craftingTicks = CRAFTING_TICKS;
            craftingTotalTicks = CRAFTING_TICKS;
            craftingStartTime = level.getGameTime();
            level.playSound(null, worldPosition, ModSounds.FORGE_FORGING.get(),
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
        if (holder.isEmpty() || !(holder.get().value() instanceof RunicAnvilRecipe recipe)) {
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
        setAnimState(AnimState.HOLDING_RESULT);
        syncToClient();
        return true;
    }

    public AnvilAction getAnvilAction() {
        if (level == null || itemCount == 0) return null;

        ItemStack wandStack = null;
        ItemStack circuitStack = null;
        for (ItemStack item : items) {
            if (item.getItem() instanceof SpellWriter) wandStack = item;
            else if (item.getItem() instanceof RunicCircuitItem) circuitStack = item;
        }

        if (wandStack != null && circuitStack != null) {
            if (RunicCircuitItem.isInscribed(circuitStack)) {
                WandStacksData data = SpellWriter.getStacks(wandStack);
                int maxInscriptions = SpellWriter.getMaxInscriptions(wandStack);
                if (data.inscribedCount() < maxInscriptions) {
                    return AnvilAction.INSCRIBE;
                } else {
                    return AnvilAction.NO_SLOTS;
                }
            } else {
                WandStacksData data = SpellWriter.getStacks(wandStack);
                int activeIndex = data.activeIndex();
                if (!data.stacks().isEmpty() && data.stacks().get(activeIndex).inscribed()) {
                    return AnvilAction.DEINSCRIBE;
                }
            }
        }

        ItemStack implementStack = null;
        ItemStack runeStack = null;
        for (ItemStack item : items) {
            if (item.isEmpty()) continue;
            if (item.getItem() instanceof EnhancementRuneItem && runeStack == null) runeStack = item;
            else if (isRuneTarget(item) && implementStack == null) implementStack = item;
        }
        if (runeStack != null && implementStack != null) {
            EnhancementRuneItem runeItem = (EnhancementRuneItem) runeStack.getItem();
            RuneType runeType = runeItem.getRuneType();
            if (runeType != null && runeType.applicableTo(implementStack)) return AnvilAction.ENGRAVE;
        }

        Optional<RecipeHolder<RunicAnvilRecipe>> match = findMatchingRecipe();
        if (match.isPresent()) {
            return AnvilAction.REPAIR;
        }

        return null;
    }

    private static boolean isRuneTarget(ItemStack stack) {
        return stack.getItem() instanceof SpellWriter
            || stack.getItem() instanceof IMagicWeapon
            || stack.getItem() instanceof IRunicDrone;
    }

    public Optional<RecipeHolder<RunicAnvilRecipe>> findMatchingRecipe() {
        if (level == null || itemCount == 0) return Optional.empty();
        List<ItemStack> nonEmpty = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) nonEmpty.add(stack);
        }
        RunicAnvilRecipeInput input = new RunicAnvilRecipeInput(nonEmpty);
        return level.getRecipeManager().getRecipeFor(ModRecipeTypes.RUNIC_ANVIL_TYPE.get(), input, level);
    }

    public ItemStack getInscriptionPreview() {
        AnvilAction action = getAnvilAction();
        if (action == null) return ItemStack.EMPTY;

        ItemStack wandStack = null;
        ItemStack circuitStack = null;
        for (ItemStack item : items) {
            if (item.getItem() instanceof SpellWriter) wandStack = item;
            else if (item.getItem() instanceof RunicCircuitItem) circuitStack = item;
        }

        return switch (action) {
            case INSCRIBE -> wandStack != null ? wandStack.copy() : ItemStack.EMPTY;
            case DEINSCRIBE -> circuitStack != null ? circuitStack.copy() : ItemStack.EMPTY;
            case REPAIR -> {
                Optional<RecipeHolder<RunicAnvilRecipe>> match = findMatchingRecipe();
                yield match.map(h -> h.value().getResultItem(level.registryAccess()).copy()).orElse(ItemStack.EMPTY);
            }
            case ENGRAVE -> {
                for (ItemStack item : items) {
                    if (!item.isEmpty() && isRuneTarget(item)) yield item.copy();
                }
                yield ItemStack.EMPTY;
            }
            default -> ItemStack.EMPTY;
        };
    }

    private void completeCrafting() {
        if (level == null || level.isClientSide()) return;

        AnvilAction action = getAnvilAction();
        if (action == null) {
            craftingTicks = 0;
            craftingTotalTicks = 0;
            return;
        }

        switch (action) {
            case INSCRIBE -> completeInscription();
            case DEINSCRIBE -> completeDeinscription();
            case REPAIR -> completeRepair();
            case ENGRAVE -> completeEngraving();
            default -> {
                craftingTicks = 0;
                craftingTotalTicks = 0;
            }
        }
    }

    private void completeInscription() {
        ItemStack wandStack = null;
        ItemStack circuitStack = null;
        int wandSlot = -1;
        int circuitSlot = -1;

        for (int i = 0; i < MAX_ITEMS; i++) {
            ItemStack item = items.get(i);
            if (item.getItem() instanceof SpellWriter && wandStack == null) {
                wandStack = item;
                wandSlot = i;
            } else if (item.getItem() instanceof RunicCircuitItem && circuitStack == null) {
                circuitStack = item;
                circuitSlot = i;
            }
        }

        if (wandStack == null || circuitStack == null || !RunicCircuitItem.isInscribed(circuitStack)) {
            craftingTicks = 0;
            craftingTotalTicks = 0;
            return;
        }

        WandStacksData data = SpellWriter.getStacks(wandStack);
        WandStacksData.StackEntry circuitEntry = circuitStack.get(ModDataComponents.CIRCUIT_SPELL.get());
        if (circuitEntry == null) {
            craftingTicks = 0;
            craftingTotalTicks = 0;
            return;
        }

        int activeIndex = data.activeIndex();
        if (data.stacks().get(activeIndex).inscribed()) {
            craftingTicks = 0;
            craftingTotalTicks = 0;
            return;
        }

        WandStacksData.StackEntry inscribedEntry = new WandStacksData.StackEntry(
                circuitEntry.components(),
                circuitEntry.elementId(),
                true,
                circuitEntry.components().size(),
                0,
                null
        );
        ItemStack result = wandStack.copy();
        WandStacksData newData = data.withStack(activeIndex, inscribedEntry);
        SpellWriter.setStacks(result, newData);

        items.set(wandSlot, ItemStack.EMPTY);
        items.set(circuitSlot, ItemStack.EMPTY);
        itemCount = 0;
        for (ItemStack s : items) if (!s.isEmpty()) itemCount++;

        resultItem = result;
        craftingTicks = 0;
        craftingTotalTicks = 0;
        syncToClient();
    }

    private void completeDeinscription() {
        ItemStack wandStack = null;
        int wandSlot = -1;

        for (int i = 0; i < MAX_ITEMS; i++) {
            ItemStack item = items.get(i);
            if (item.getItem() instanceof SpellWriter && wandStack == null) {
                wandStack = item;
                wandSlot = i;
            }
        }

        if (wandStack == null) {
            craftingTicks = 0;
            craftingTotalTicks = 0;
            return;
        }

        WandStacksData data = SpellWriter.getStacks(wandStack);
        int activeIndex = data.activeIndex();

        if (data.stacks().isEmpty() || !data.stacks().get(activeIndex).inscribed()) {
            craftingTicks = 0;
            craftingTotalTicks = 0;
            return;
        }

        ItemStack result = wandStack.copy();
        WandStacksData newData = data.withStack(activeIndex, WandStacksData.StackEntry.EMPTY);
        SpellWriter.setStacks(result, newData);

        int circuitSlot = -1;
        for (int i = 0; i < MAX_ITEMS; i++) {
            if (items.get(i).getItem() instanceof RunicCircuitItem && circuitSlot == -1) circuitSlot = i;
        }
        if (wandSlot >= 0) { items.set(wandSlot, ItemStack.EMPTY); }
        if (circuitSlot >= 0) { items.set(circuitSlot, ItemStack.EMPTY); }
        itemCount = 0;
        for (ItemStack s : items) if (!s.isEmpty()) itemCount++;

        resultItem = result;
        craftingTicks = 0;
        craftingTotalTicks = 0;
        syncToClient();
    }

    private void completeRepair() {
        Optional<RecipeHolder<RunicAnvilRecipe>> match = findMatchingRecipe();
        if (match.isEmpty()) {
            craftingTicks = 0;
            craftingTotalTicks = 0;
            return;
        }

        lastRecipeId = match.get().id();

        RunicAnvilRecipe recipe = match.get().value();
        List<ItemStack> nonEmpty = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) nonEmpty.add(stack);
        }
        RunicAnvilRecipeInput input = new RunicAnvilRecipeInput(nonEmpty);
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

    private void completeEngraving() {
        ItemStack implementStack = null;
        ItemStack runeStack = null;
        int implementSlot = -1;
        int runeSlot = -1;
        for (int i = 0; i < MAX_ITEMS; i++) {
            ItemStack item = items.get(i);
            if (item.isEmpty()) continue;
            if (item.getItem() instanceof EnhancementRuneItem && runeStack == null) {
                runeStack = item;
                runeSlot = i;
            } else if (isRuneTarget(item) && implementStack == null) {
                implementStack = item;
                implementSlot = i;
            }
        }
        if (implementStack == null || runeStack == null) {
            craftingTicks = 0; craftingTotalTicks = 0; return;
        }
        EnhancementRuneItem runeItem = (EnhancementRuneItem) runeStack.getItem();
        RuneType runeType = runeItem.getRuneType();
        if (runeType == null || !runeType.applicableTo(implementStack)) {
            craftingTicks = 0; craftingTotalTicks = 0; return;
        }

        net.minecraft.nbt.CompoundTag statsTag = runeStack.get(ModDataComponents.RUNE_STATS.get());
        if (statsTag == null) {
            craftingTicks = 0; craftingTotalTicks = 0; return;
        }

        java.util.Map<String, Float> stats = new java.util.LinkedHashMap<>();
        for (String key : statsTag.getAllKeys()) stats.put(key, statsTag.getFloat(key));

        EnhancementRuneData runeData = new EnhancementRuneData(runeType.id(), runeItem.getTier(), stats);

        java.util.List<EnhancementRuneData> existing = implementStack.getOrDefault(
            ModDataComponents.ENHANCEMENT_RUNES.get(), java.util.List.of());
        java.util.List<EnhancementRuneData> updated = new java.util.ArrayList<>(existing);
        if (runeType instanceof ElementRuneType) {
            updated.removeIf(r -> RuneTypeRegistry.get(r.runeTypeId()) instanceof ElementRuneType);
        } else {
            updated.removeIf(r -> r.runeTypeId().equals(runeData.runeTypeId()));
        }
        updated.add(runeData);

        ItemStack result = implementStack.copy();
        result.set(ModDataComponents.ENHANCEMENT_RUNES.get(), java.util.List.copyOf(updated));

        items.set(implementSlot, ItemStack.EMPTY);
        items.set(runeSlot, ItemStack.EMPTY);
        itemCount = 0;
        for (ItemStack s : items) if (!s.isEmpty()) itemCount++;

        resultItem = result;
        craftingTicks = 0; craftingTotalTicks = 0;
        syncToClient();
    }

    private void setAnimState(AnimState newState) {
        if (this.animState != newState) {
            this.animState = newState;
            if (level != null && newState == AnimState.ACTIVATING) {
                level.playSound(null, worldPosition, ModSounds.FORGE_ACTIVE.get(),
                        SoundSource.BLOCKS, 1.0f, 1.0f);
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

    public static void tick(Level level, BlockPos pos, BlockState state, RunicAnvilBlockEntity be) {
        if (level.isClientSide()) return;

        if (!be.active) return;

        if (++be.revalidateTimer >= 20) {
            be.revalidateTimer = 0;
            var result = RunicAnvilValidator.validateFull(level, pos);
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
