package com.github.runicrebirth.menu;

import com.github.runicrebirth.init.ModDataComponents;
import com.github.runicrebirth.init.ModMenuTypes;
import com.github.runicrebirth.items.RunicKeyRingItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public class RunicKeyRingMenu extends AbstractContainerMenu {

    public static final String[] RING_SLOT_IDS = {
        "thumb_spell_ring", "index_spell_ring", "middle_spell_ring",
        "ring_spell_ring", "pinkie_spell_ring"
    };

    @SuppressWarnings("unchecked")
    private static final TagKey<Item>[] RING_TAGS = new TagKey[]{
        TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("curios", "thumb_spell_ring")),
        TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("curios", "index_spell_ring")),
        TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("curios", "middle_spell_ring")),
        TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("curios", "ring_spell_ring")),
        TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("curios", "pinkie_spell_ring"))
    };

    private static final TagKey<Item> BASE_SPELL_RING_TAG =
        TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("curios", "spell_ring"));

    public static final int CURIOS_COUNT  = 5;
    public static final int STORAGE_COUNT = RunicKeyRingItem.SIZE;
    public static final int PLAYER_COUNT  = 36;

    private final SimpleContainer curiosMirror;
    private final SimpleContainer storage;
    private final Player player;
    private final int itemSlot;

    public RunicKeyRingMenu(int windowId, Inventory playerInv, int itemSlot) {
        super(ModMenuTypes.RUNIC_KEY_RING.get(), windowId);
        this.player   = playerInv.player;
        this.itemSlot = itemSlot;

        this.curiosMirror = new SimpleContainer(CURIOS_COUNT);
        this.storage = new SimpleContainer(STORAGE_COUNT) {
            @Override
            public void setChanged() {
                super.setChanged();
                if (!player.level().isClientSide()) saveToItem();
            }
        };

        if (!player.level().isClientSide()) {
            loadCurios();
            loadFromItem();
        }

        // Curios ring slots (0-4)  x=8,26,44,62,80  y=26
        for (int i = 0; i < CURIOS_COUNT; i++) {
            final int fi = i;
            this.addSlot(new Slot(curiosMirror, i, 8 + i * 18, 26) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(RING_TAGS[fi]);
                }
                @Override
                public int getMaxStackSize(ItemStack stack) { return 1; }
            });
        }

        // Key ring storage slots (5-14)  2 rows of 5  x=8..80  y=58,76
        for (int i = 0; i < STORAGE_COUNT; i++) {
            final int fi = i;
            int col = i % 5;
            int row = i / 5;
            this.addSlot(new Slot(storage, i, 8 + col * 18, 58 + row * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return isSpellRing(stack);
                }
                @Override
                public int getMaxStackSize(ItemStack stack) { return 1; }
            });
        }

        // Player inventory (15-41)  x=8..152  y=104,122,140
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 104 + row * 18));
            }
        }

        // Hotbar (42-50)  x=8..152  y=162
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 162));
        }
    }

    private void loadCurios() {
        CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
            for (int i = 0; i < RING_SLOT_IDS.length; i++) {
                var handler = curios.getCurios().get(RING_SLOT_IDS[i]);
                if (handler != null) {
                    curiosMirror.setItem(i, handler.getStacks().getStackInSlot(0).copy());
                    handler.getStacks().setStackInSlot(0, ItemStack.EMPTY);
                }
            }
        });
    }

    private void saveCurios() {
        CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
            for (int i = 0; i < RING_SLOT_IDS.length; i++) {
                var handler = curios.getCurios().get(RING_SLOT_IDS[i]);
                if (handler != null) {
                    handler.getStacks().setStackInSlot(0, curiosMirror.getItem(i));
                }
            }
        });
    }

    private void loadFromItem() {
        ItemStack keyRing = getKeyRingStack();
        if (keyRing.isEmpty()) return;
        NonNullList<ItemStack> items = NonNullList.withSize(STORAGE_COUNT, ItemStack.EMPTY);
        keyRing.getOrDefault(ModDataComponents.KEY_RING_INVENTORY.get(), ItemContainerContents.EMPTY)
            .copyInto(items);
        for (int i = 0; i < STORAGE_COUNT; i++) {
            storage.setItem(i, items.get(i));
        }
    }

    private void saveToItem() {
        ItemStack keyRing = getKeyRingStack();
        if (keyRing.isEmpty() || !(keyRing.getItem() instanceof RunicKeyRingItem)) return;
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < STORAGE_COUNT; i++) items.add(storage.getItem(i));
        keyRing.set(ModDataComponents.KEY_RING_INVENTORY.get(), ItemContainerContents.fromItems(items));
    }

    private ItemStack getKeyRingStack() {
        return player.getInventory().getItem(itemSlot);
    }

    private boolean isSpellRing(ItemStack stack) {
        for (TagKey<Item> tag : RING_TAGS) {
            if (stack.is(tag)) return true;
        }
        return stack.is(BASE_SPELL_RING_TAG);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack   = slot.getItem();
        ItemStack original = stack.copy();

        int storageStart = CURIOS_COUNT;
        int storageEnd   = storageStart + STORAGE_COUNT;
        int playerStart  = storageEnd;
        int playerEnd    = playerStart + PLAYER_COUNT;

        if (index < CURIOS_COUNT) {
            // Curios → storage, fallback player inv
            if (!this.moveItemStackTo(stack, storageStart, storageEnd, false))
                if (!this.moveItemStackTo(stack, playerStart, playerEnd, false))
                    return ItemStack.EMPTY;
        } else if (index < storageEnd) {
            // Storage → player inv
            if (!this.moveItemStackTo(stack, playerStart, playerEnd, false))
                return ItemStack.EMPTY;
        } else {
            // Player inv → storage first, then curios slot
            if (isSpellRing(stack)) {
                if (!this.moveItemStackTo(stack, storageStart, storageEnd, false))
                    if (!this.moveItemStackTo(stack, 0, CURIOS_COUNT, false))
                        return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        ItemStack stack = getKeyRingStack();
        return !stack.isEmpty() && stack.getItem() instanceof RunicKeyRingItem;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            saveCurios();
            saveToItem();
        }
    }

    // Accessors for screen layout
    public static int curiosSlotX(int i) { return 8 + i * 18; }
    public static int curiosSlotY()      { return 26; }
    public static int storageSlotX(int col) { return 8 + col * 18; }
    public static int storageSlotY(int row) { return 58 + row * 18; }
}
