package com.github.runicrebirth.crafting;

import com.github.runicrebirth.init.ModRecipeSerializers;
import com.github.runicrebirth.items.armor.DyeableMagicArmorItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DyeArmorRecipe extends CustomRecipe {

    public DyeArmorRecipe(CraftingBookCategory category) {
        super(category);
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
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack armor = ItemStack.EMPTY;
        List<DyeItem> dyes = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof DyeableMagicArmorItem) {
                    if (!armor.isEmpty()) return ItemStack.EMPTY;
                    armor = stack.copy();
                } else if (stack.getItem() instanceof DyeItem dyeItem) {
                    dyes.add(dyeItem);
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (armor.isEmpty() || dyes.isEmpty()) return ItemStack.EMPTY;

        return blendDyes(armor, dyes);
    }

    private static ItemStack blendDyes(ItemStack stack, List<DyeItem> dyes) {
        int r = 0, g = 0, b = 0, maxBrightness = 0, count = 0;

        DyedItemColor existing = stack.get(net.minecraft.core.component.DataComponents.DYED_COLOR);
        if (existing != null) {
            int er = FastColor.ARGB32.red(existing.rgb());
            int eg = FastColor.ARGB32.green(existing.rgb());
            int eb = FastColor.ARGB32.blue(existing.rgb());
            maxBrightness += Math.max(er, Math.max(eg, eb));
            r += er; g += eg; b += eb; count++;
        }

        for (DyeItem dye : dyes) {
            int c = dye.getDyeColor().getTextureDiffuseColor();
            int dr = FastColor.ARGB32.red(c);
            int dg = FastColor.ARGB32.green(c);
            int db = FastColor.ARGB32.blue(c);
            maxBrightness += Math.max(dr, Math.max(dg, db));
            r += dr; g += dg; b += db; count++;
        }

        int finalR = r / count;
        int finalG = g / count;
        int finalB = b / count;
        float avgBrightness = (float) maxBrightness / count;
        float maxChannel = Math.max(finalR, Math.max(finalG, finalB));
        finalR = (int)(finalR * avgBrightness / maxChannel);
        finalG = (int)(finalG * avgBrightness / maxChannel);
        finalB = (int)(finalB * avgBrightness / maxChannel);

        int rgb = FastColor.ARGB32.color(0, finalR, finalG, finalB);
        stack.set(net.minecraft.core.component.DataComponents.DYED_COLOR, new DyedItemColor(rgb, true));
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.DYE_ARMOR.get();
    }
}
