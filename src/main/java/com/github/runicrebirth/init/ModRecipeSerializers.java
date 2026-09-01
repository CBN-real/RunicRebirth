package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.crafting.DyeArmorRecipe;
import com.github.runicrebirth.crafting.InfusionRecipe;
import com.github.runicrebirth.crafting.RunicAnvilRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, RunicRebirth.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfusionRecipe>> INFUSION =
            RECIPE_SERIALIZERS.register("infusion", () -> new RecipeSerializer<>(
                    InfusionRecipe.MAP_CODEC,
                    InfusionRecipe.STREAM_CODEC
            ));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RunicAnvilRecipe>> RUNIC_ANVIL =
            RECIPE_SERIALIZERS.register("runic_anvil", () -> new RecipeSerializer<>(
                    RunicAnvilRecipe.MAP_CODEC,
                    RunicAnvilRecipe.STREAM_CODEC
            ));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DyeArmorRecipe>> DYE_ARMOR =
            RECIPE_SERIALIZERS.register("dye_armor", () -> new RecipeSerializer<>(
                    DyeArmorRecipe.CODEC,
                    DyeArmorRecipe.STREAM_CODEC
            ));

    private ModRecipeSerializers() {}
}
