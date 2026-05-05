package com.github.interactivemagic.init;

import com.github.interactivemagic.InteractiveMagic;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {

    public static final DeferredRegister.DataComponents COMPONENTS =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, InteractiveMagic.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> INSCRIBED_SPELL =
        COMPONENTS.registerComponentType("inscribed_spell",
            b -> b.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));

    private ModDataComponents() {}

    public static void init() {}
}
