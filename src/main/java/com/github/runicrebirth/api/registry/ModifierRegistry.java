package com.github.runicrebirth.api.registry;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellModifier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class ModifierRegistry {

    public static final ResourceKey<Registry<SpellModifier>> REGISTRY_KEY = ResourceKey.createRegistryKey(
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spell_modifiers"));

    public static final Registry<SpellModifier> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).sync(true).create();

    public static final DeferredRegister<SpellModifier> MODIFIERS =
        DeferredRegister.create(REGISTRY_KEY, RunicRebirth.MODID);

    private ModifierRegistry() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModifierRegistry::onNewRegistry);
        MODIFIERS.register(modBus);
    }

    private static void onNewRegistry(NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    public static SpellModifier get(ResourceLocation id) {
        return REGISTRY.get(id);
    }
}
