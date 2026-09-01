package com.github.runicrebirth.crafting;

import com.github.runicrebirth.init.ModRecipeSerializers;
import com.github.runicrebirth.items.armor.DyeableMagicArmorItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DyeArmorRecipe extends CustomRecipe {

    public static final MapCodec<DyeArmorRecipe> CODEC = MapCodec.unit(new DyeArmorRecipe());
    public static final StreamCodec<RegistryFriendlyByteBuf, DyeArmorRecipe> STREAM_CODEC = StreamCodec.unit(new DyeArmorRecipe());

    public DyeArmorRecipe() {
        super();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack armor = ItemStack.EMPTY;
        List<ItemStack> dyes = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof DyeableMagicArmorItem) {
                    if (!armor.isEmpty()) return false;
                    armor = stack;
                } else if (stack.getItem() instanceof DyeItem) {
                    dyes.add(stack);
                } else {
                    return false;
                }
            }
        }

        return !armor.isEmpty() && !dyes.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack armor = ItemStack.EMPTY;
        List<ItemStack> dyeStacks = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof DyeableMagicArmorItem) {
                    if (!armor.isEmpty()) return ItemStack.EMPTY;
                    armor = stack.copy();
                } else if (stack.getItem() instanceof DyeItem) {
                    dyeStacks.add(stack);
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (armor.isEmpty() || dyeStacks.isEmpty()) return ItemStack.EMPTY;

        return blendDyes(armor, dyeStacks);
    }

    private static ItemStack blendDyes(ItemStack stack, List<ItemStack> dyeStacks) {
        int r = 0, g = 0, b = 0, maxBrightness = 0, count = 0;

        DyedItemColor existing = stack.get(DataComponents.DYED_COLOR);
        if (existing != null) {
            int er = ARGB.red(existing.rgb());
            int eg = ARGB.green(existing.rgb());
            int eb = ARGB.blue(existing.rgb());
            maxBrightness += Math.max(er, Math.max(eg, eb));
            r += er; g += eg; b += eb; count++;
        }

        for (ItemStack dyeStack : dyeStacks) {
            DyeColor dyeColor = dyeStack.get(DataComponents.DYE);
            if (dyeColor == null) continue;
            int c = dyeColor.getTextureDiffuseColor();
            int dr = ARGB.red(c);
            int dg = ARGB.green(c);
            int db = ARGB.blue(c);
            maxBrightness += Math.max(dr, Math.max(dg, db));
            r += dr; g += dg; b += db; count++;
        }

        if (count == 0) return stack;

        int finalR = r / count;
        int finalG = g / count;
        int finalB = b / count;
        float avgBrightness = (float) maxBrightness / count;
        float maxChannel = Math.max(finalR, Math.max(finalG, finalB));
        if (maxChannel > 0) {
            finalR = (int)(finalR * avgBrightness / maxChannel);
            finalG = (int)(finalG * avgBrightness / maxChannel);
            finalB = (int)(finalB * avgBrightness / maxChannel);
        }

        int rgb = ARGB.color(0, finalR, finalG, finalB);
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(rgb));
        return stack;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipeSerializers.DYE_ARMOR.get();
    }
}
