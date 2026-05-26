package com.github.runicrebirth;

import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.registry.ModifierRegistry;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.config.ClientConfig;
import com.github.runicrebirth.config.ServerConfig;
import com.github.runicrebirth.init.ModArmorMaterials;
import com.github.runicrebirth.init.ModAttachments;
import com.github.runicrebirth.init.ModBlocks;
import com.github.runicrebirth.init.ModDataComponents;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModItems;
import com.github.runicrebirth.init.ModModifiers;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.init.ModShapes;
import com.github.runicrebirth.init.ModSpellTypes;
import com.github.runicrebirth.network.ModPackets;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(RunicRebirth.MODID)
public class RunicRebirth {

    public static final String MODID = "runicrebirth";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RunicRebirth(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::buildContents);
        modEventBus.addListener(ModPackets::register);

        // Vanilla-style registries
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModAttachments.ATTACHMENTS.register(modEventBus);
        ModDataComponents.COMPONENTS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);

        // Custom registries
        SpellTypeRegistry.register(modEventBus);
        ModifierRegistry.register(modEventBus);
        ElementRegistry.register(modEventBus);

        // Keep these references alive so DeferredHolder<> static fields initialize + register.
        ModSpellTypes.init();
        ModModifiers.init();
        ModElements.init();

        // Configs
        ServerConfig.register(modContainer);
        ClientConfig.register(modContainer);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[RunicRebirth] Common setup");
        event.enqueueWork(ModShapes::init);
    }

    private void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.BASIC_WAND.get());
            event.accept(ModItems.INSCRIBED_WAND.get());
            event.accept(ModItems.RING_OF_EXPANSION.get());
            event.accept(ModItems.APPRENTICE_SET_HELMET.get());
            event.accept(ModItems.APPRENTICE_SET_CHESTPLATE.get());
            event.accept(ModItems.APPRENTICE_SET_LEGGINGS.get());
            event.accept(ModItems.APPRENTICE_SET_BOOTS.get());
        }

    }
}
