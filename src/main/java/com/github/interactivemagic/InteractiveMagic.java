package com.github.interactivemagic;

import com.github.interactivemagic.api.registry.ElementRegistry;
import com.github.interactivemagic.api.registry.ModifierRegistry;
import com.github.interactivemagic.api.registry.SpellTypeRegistry;
import com.github.interactivemagic.config.ClientConfig;
import com.github.interactivemagic.config.ServerConfig;
import com.github.interactivemagic.init.ModArmorMaterials;
import com.github.interactivemagic.init.ModAttachments;
import com.github.interactivemagic.init.ModBlocks;
import com.github.interactivemagic.init.ModDataComponents;
import com.github.interactivemagic.init.ModElements;
import com.github.interactivemagic.init.ModEntities;
import com.github.interactivemagic.init.ModItems;
import com.github.interactivemagic.init.ModModifiers;
import com.github.interactivemagic.init.ModParticles;
import com.github.interactivemagic.init.ModShapes;
import com.github.interactivemagic.init.ModSpellTypes;
import com.github.interactivemagic.network.ModPackets;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(InteractiveMagic.MODID)
public class InteractiveMagic {

    public static final String MODID = "interactivemagic";
    public static final Logger LOGGER = LogUtils.getLogger();

    public InteractiveMagic(IEventBus modEventBus, ModContainer modContainer) {
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
        LOGGER.info("[InteractiveMagic] Common setup");
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
