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

public class InfusionRecipeSerializer implements RecipeSerializer<InfusionRecipe> {

    public static final MapCodec<InfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(InfusionRecipe::getIngredients),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(InfusionRecipe::getResult),
            Codec.INT.optionalFieldOf("crafting_time", 165).forGetter(InfusionRecipe::getCraftingTime)
    ).apply(inst, InfusionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                    InfusionRecipe::getIngredients,
                    ItemStack.STREAM_CODEC,
                    InfusionRecipe::getResult,
                    ByteBufCodecs.INT,
                    InfusionRecipe::getCraftingTime,
                    InfusionRecipe::new
            );

    @Override
    public MapCodec<InfusionRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
