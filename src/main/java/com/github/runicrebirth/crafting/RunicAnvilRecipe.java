package com.github.runicrebirth.crafting;

import com.github.runicrebirth.init.ModRecipeSerializers;
import com.github.runicrebirth.init.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class RunicAnvilRecipe implements Recipe<RunicAnvilRecipeInput> {

    public static final MapCodec<RunicAnvilRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("ingredients").forGetter(RunicAnvilRecipe::getIngredients),
            ItemStack.CODEC.fieldOf("result").forGetter(RunicAnvilRecipe::getResult),
            Codec.INT.optionalFieldOf("crafting_time", 120).forGetter(RunicAnvilRecipe::getCraftingTime)
    ).apply(inst, RunicAnvilRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RunicAnvilRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(Registries.ITEM).apply(ByteBufCodecs.list()),
                    RunicAnvilRecipe::getIngredients,
                    ItemStack.STREAM_CODEC,
                    RunicAnvilRecipe::getResult,
                    ByteBufCodecs.INT,
                    RunicAnvilRecipe::getCraftingTime,
                    RunicAnvilRecipe::new
            );

    private final List<Item> ingredients;
    private final ItemStack result;
    private final int craftingTime;

    public RunicAnvilRecipe(List<Item> ingredients, ItemStack result, int craftingTime) {
        this.ingredients = List.copyOf(ingredients);
        this.result = result;
        this.craftingTime = craftingTime;
    }

    @Override
    public boolean matches(RunicAnvilRecipeInput input, Level level) {
        if (input.size() != ingredients.size()) return false;

        List<ItemStack> remaining = new ArrayList<>(input.items());
        for (Item ingredient : ingredients) {
            boolean found = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (!remaining.get(i).isEmpty() && remaining.get(i).is(ingredient)) {
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
    public ItemStack assemble(RunicAnvilRecipeInput input) {
        return result.copy();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }

    public List<Item> getIngredients() {
        return ingredients;
    }

    @Override
    public RecipeSerializer<RunicAnvilRecipe> getSerializer() {
        return ModRecipeSerializers.RUNIC_ANVIL.get();
    }

    @Override
    public RecipeType<RunicAnvilRecipe> getType() {
        return ModRecipeTypes.RUNIC_ANVIL_TYPE.get();
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public ItemStack getResult() {
        return result;
    }
}
