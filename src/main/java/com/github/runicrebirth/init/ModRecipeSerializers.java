package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.crafting.InfusionRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, RunicRebirth.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, InfusionRecipeSerializer> INFUSION =
            RECIPE_SERIALIZERS.register("infusion", InfusionRecipeSerializer::new);

    private ModRecipeSerializers() {}
}
