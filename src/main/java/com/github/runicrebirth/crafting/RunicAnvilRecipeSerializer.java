package com.github.runicrebirth.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

public class RunicAnvilRecipeSerializer implements RecipeSerializer<RunicAnvilRecipe> {

    public static final MapCodec<RunicAnvilRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(RunicAnvilRecipe::getIngredients),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(RunicAnvilRecipe::getResult),
            Codec.INT.optionalFieldOf("crafting_time", 120).forGetter(RunicAnvilRecipe::getCraftingTime)
    ).apply(inst, RunicAnvilRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RunicAnvilRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                    RunicAnvilRecipe::getIngredients,
                    ItemStack.STREAM_CODEC,
                    RunicAnvilRecipe::getResult,
                    ByteBufCodecs.INT,
                    RunicAnvilRecipe::getCraftingTime,
                    RunicAnvilRecipe::new
            );

    @Override
    public MapCodec<RunicAnvilRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RunicAnvilRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
