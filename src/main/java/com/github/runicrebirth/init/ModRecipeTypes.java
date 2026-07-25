package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.crafting.InfusionRecipe;
import com.github.runicrebirth.crafting.RunicAnvilRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, RunicRebirth.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<InfusionRecipe>> INFUSION_TYPE =
            RECIPE_TYPES.register("infusion", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return RunicRebirth.MODID + ":infusion";
                }
            });

    public static final DeferredHolder<RecipeType<?>, RecipeType<RunicAnvilRecipe>> RUNIC_ANVIL_TYPE =
            RECIPE_TYPES.register("runic_anvil", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return RunicRebirth.MODID + ":runic_anvil";
                }
            });

    private ModRecipeTypes() {}
}
