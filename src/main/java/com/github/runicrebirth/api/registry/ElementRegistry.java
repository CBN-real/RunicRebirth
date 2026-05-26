package com.github.runicrebirth.api.registry;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.Element;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class ElementRegistry {

    public static final ResourceKey<Registry<Element>> REGISTRY_KEY = ResourceKey.createRegistryKey(
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "elements"));

    public static final Registry<Element> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).sync(true).create();

    public static final DeferredRegister<Element> ELEMENTS =
        DeferredRegister.create(REGISTRY_KEY, RunicRebirth.MODID);

    private ElementRegistry() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ElementRegistry::onNewRegistry);
        ELEMENTS.register(modBus);
    }

    private static void onNewRegistry(NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    public static Element get(ResourceLocation id) {
        return REGISTRY.get(id);
    }
}
