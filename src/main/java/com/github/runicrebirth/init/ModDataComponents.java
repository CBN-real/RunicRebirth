package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.WandStacksData;
import com.github.runicrebirth.rune.EnhancementRuneData;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

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

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> KEY_RING_INVENTORY =
        COMPONENTS.registerComponentType("key_ring_inventory",
            b -> b.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> CONFIGURATOR_TRACKER_POS =
        COMPONENTS.registerComponentType("configurator_tracker_pos",
            b -> b.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<EnhancementRuneData>>> ENHANCEMENT_RUNES =
        COMPONENTS.registerComponentType("enhancement_runes",
            b -> b.persistent(EnhancementRuneData.CODEC.listOf())
                   .networkSynchronized(EnhancementRuneData.LIST_STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> RUNE_STATS =
        COMPONENTS.registerComponentType("rune_stats",
            b -> b.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG));

    private ModDataComponents() {}

    public static void init() {}
}
