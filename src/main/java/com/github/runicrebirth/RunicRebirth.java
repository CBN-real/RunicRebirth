package com.github.runicrebirth;

import com.github.runicrebirth.advancement.triggers.ModCriteriaTriggers;
import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.registry.ModifierRegistry;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.config.ClientConfig;
import com.github.runicrebirth.config.ServerConfig;
import com.github.runicrebirth.init.ModArmorMaterials;
import com.github.runicrebirth.init.ModAttachments;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModBlocks;
import com.github.runicrebirth.init.ModDataComponents;
import com.github.runicrebirth.init.ModCreativeTabs;
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModItems;
import com.github.runicrebirth.init.ModMenuTypes;
import com.github.runicrebirth.init.ModModifiers;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.init.ModRecipeSerializers;
import com.github.runicrebirth.init.ModRecipeTypes;
import com.github.runicrebirth.init.ModShapes;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.init.ModSpellTypes;
import com.github.runicrebirth.compat.modonomicon.ModonomiconCompat;
import com.github.runicrebirth.network.ModPackets;
import com.github.runicrebirth.rune.RuneTypeRegistry;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(RunicRebirth.MODID)
public class RunicRebirth {

    public static final String MODID = "runicrebirth";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RunicRebirth(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModPackets::register);

        // Vanilla-style registries
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModAttachments.ATTACHMENTS.register(modEventBus);
        ModDataComponents.COMPONENTS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModCriteriaTriggers.TRIGGERS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        // Custom registries
        SpellTypeRegistry.register(modEventBus);
        ModifierRegistry.register(modEventBus);
        ElementRegistry.register(modEventBus);

        // Keep these references alive so DeferredHolder<> static fields initialize + register.
        ModSpellTypes.init();
        ModModifiers.init();
        ModElements.init();
        ModCriteriaTriggers.init();
        ModSounds.init();

        // Configs
        ServerConfig.register(modContainer);
        ClientConfig.register(modContainer);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[RunicRebirth] Common setup");
        event.enqueueWork(ModShapes::init);
        event.enqueueWork(ModonomiconCompat::registerPageLoaders);
        event.enqueueWork(RuneTypeRegistry::init);
    }

}
