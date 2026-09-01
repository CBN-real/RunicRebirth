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

public class InfusionRecipe implements Recipe<InfusionRecipeInput> {

    // Decodes only Item + count; never constructs ItemStack during recipe loading.
    // ItemStack construction deferred to assemble()/getResult() when components are guaranteed bound.
    public static final MapCodec<InfusionRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("ingredients").forGetter(InfusionRecipe::getIngredients),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("result").forGetter(r -> r.resultItem),
            Codec.INT.optionalFieldOf("result_count", 1).forGetter(r -> r.resultCount),
            Codec.INT.optionalFieldOf("crafting_time", 165).forGetter(InfusionRecipe::getCraftingTime)
    ).apply(inst, InfusionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(Registries.ITEM).apply(ByteBufCodecs.list()),
                    InfusionRecipe::getIngredients,
                    ByteBufCodecs.registry(Registries.ITEM),
                    r -> r.resultItem,
                    ByteBufCodecs.INT,
                    r -> r.resultCount,
                    ByteBufCodecs.INT,
                    InfusionRecipe::getCraftingTime,
                    InfusionRecipe::new
            );

    private final List<Item> ingredients;
    private final Item resultItem;
    private final int resultCount;
    private final int craftingTime;

    public InfusionRecipe(List<Item> ingredients, Item resultItem, int resultCount, int craftingTime) {
        this.ingredients = List.copyOf(ingredients);
        this.resultItem = resultItem;
        this.resultCount = resultCount;
        this.craftingTime = craftingTime;
    }

    @Override
    public boolean matches(InfusionRecipeInput input, Level level) {
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
    public ItemStack assemble(InfusionRecipeInput input) {
        return new ItemStack(resultItem, resultCount);
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
    public RecipeSerializer<InfusionRecipe> getSerializer() {
        return ModRecipeSerializers.INFUSION.get();
    }

    @Override
    public RecipeType<InfusionRecipe> getType() {
        return ModRecipeTypes.INFUSION_TYPE.get();
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public ItemStack getResult() {
        return new ItemStack(resultItem, resultCount);
    }
}
