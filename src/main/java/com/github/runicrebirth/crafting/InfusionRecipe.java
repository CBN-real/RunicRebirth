package com.github.runicrebirth.crafting;

import com.github.runicrebirth.init.ModRecipeSerializers;
import com.github.runicrebirth.init.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class InfusionRecipe implements Recipe<InfusionRecipeInput> {

    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;
    private final int craftingTime;

    public InfusionRecipe(List<Ingredient> ingredients, ItemStack result, int craftingTime) {
        this.ingredients = NonNullList.copyOf(ingredients);
        this.result = result;
        this.craftingTime = craftingTime;
    }

    @Override
    public boolean matches(InfusionRecipeInput input, Level level) {
        if (input.size() != ingredients.size()) return false;

        List<ItemStack> remaining = new ArrayList<>(input.items());
        for (Ingredient ingredient : ingredients) {
            boolean found = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (!remaining.get(i).isEmpty() && ingredient.test(remaining.get(i))) {
                    remaining.set(i, ItemStack.EMPTY);
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(InfusionRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.INFUSION.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.INFUSION_TYPE.get();
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public ItemStack getResult() {
        return result;
    }
}
