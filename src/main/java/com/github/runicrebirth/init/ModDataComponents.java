package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.WandStacksData;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {

    public static final DeferredRegister.DataComponents COMPONENTS =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, RunicRebirth.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> INSCRIBED_SPELL =
        COMPONENTS.registerComponentType("inscribed_spell",
            b -> b.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandStacksData>> WAND_STACKS =
        COMPONENTS.registerComponentType("wand_stacks",
            b -> b.persistent(WandStacksData.CODEC).networkSynchronized(WandStacksData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_INSCRIPTIONS =
        COMPONENTS.registerComponentType("max_inscriptions",
            b -> b.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandStacksData.StackEntry>> CIRCUIT_SPELL =
        COMPONENTS.registerComponentType("circuit_spell",
            b -> b.persistent(WandStacksData.StackEntry.CODEC).networkSynchronized(WandStacksData.StackEntry.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CIRCUIT_TIER =
        COMPONENTS.registerComponentType("circuit_tier",
            b -> b.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_MODIFIER_SLOTS =
        COMPONENTS.registerComponentType("max_modifier_slots",
            b -> b.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> INITIAL_CHARGES =
        COMPONENTS.registerComponentType("initial_charges",
            b -> b.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    private ModDataComponents() {}

    public static void init() {}
}
