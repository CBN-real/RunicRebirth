package com.github.runicrebirth.api.registry;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class SpellTypeRegistry {

    public static final ResourceKey<Registry<SpellType>> REGISTRY_KEY = ResourceKey.createRegistryKey(
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spell_types"));

    public static final Registry<SpellType> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).sync(true).create();

    public static final DeferredRegister<SpellType> SPELL_TYPES =
        DeferredRegister.create(REGISTRY_KEY, RunicRebirth.MODID);

    private SpellTypeRegistry() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(SpellTypeRegistry::onNewRegistry);
        SPELL_TYPES.register(modBus);
    }

    private static void onNewRegistry(NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    public static SpellType get(ResourceLocation id) {
        return REGISTRY.get(id);
    }
}
