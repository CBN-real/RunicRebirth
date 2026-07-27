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
import com.github.runicrebirth.init.ModElements;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModItems;
import com.github.runicrebirth.init.ModModifiers;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.init.ModRecipeSerializers;
import com.github.runicrebirth.init.ModRecipeTypes;
import com.github.runicrebirth.init.ModShapes;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.init.ModSpellTypes;
import com.github.runicrebirth.compat.modonomicon.ModonomiconCompat;
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
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModAttachments.ATTACHMENTS.register(modEventBus);
        ModDataComponents.COMPONENTS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModCriteriaTriggers.TRIGGERS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
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
    }

    private void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.ACOLYTE_WAND.get());
            event.accept(ModItems.INSCRIBED_WAND.get());
            event.accept(ModItems.ADEPT_STAFF.get());
            event.accept(ModItems.RING_OF_EXPANSION.get());
            event.accept(ModItems.ACOLYTE_ARCANE_RING.get());
            event.accept(ModItems.ACOLYTE_WIZARD_HAT.get());
            event.accept(ModItems.ACOLYTE_ROBES.get());
            event.accept(ModItems.ACOLYTE_PANTS.get());
            event.accept(ModItems.ACOLYTE_BOOTS.get());
            event.accept(ModItems.ACOLYTE_ARTIFICER_HEADGEAR.get());
            event.accept(ModItems.ACOLYTE_MAGE_HOOD.get());
            event.accept(ModItems.ACOLYTE_RUNEBLADE_HELMET.get());
            event.accept(ModItems.ADEPT_RUNEBLADE_HELMET.get());
            event.accept(ModItems.ADEPT_RUNEBLADE_CHESTPLATE.get());
            event.accept(ModItems.ADEPT_RUNEBLADE_LEGGINGS.get());
            event.accept(ModItems.ADEPT_RUNEBLADE_BOOTS.get());
            event.accept(ModItems.ADEPT_WIZARD_HAT.get());
            event.accept(ModItems.ADEPT_WIZARD_ROBES.get());
            event.accept(ModItems.ADEPT_WIZARD_PANTS.get());
            event.accept(ModItems.ADEPT_WIZARD_BOOTS.get());
            event.accept(ModItems.ADEPT_MAGE_HOOD.get());
            event.accept(ModItems.ADEPT_MAGE_ROBES.get());
            event.accept(ModItems.ADEPT_MAGE_PANTS.get());
            event.accept(ModItems.ADEPT_MAGE_BOOTS.get());
            event.accept(ModItems.ADEPT_ARTIFICER_HEADGEAR.get());
            event.accept(ModItems.ADEPT_ARTIFICER_CHESTGEAR.get());
            event.accept(ModItems.ADEPT_ARTIFICER_PANTS.get());
            event.accept(ModItems.ADEPT_ARTIFICER_BOOTS.get());
            event.accept(ModItems.RUNIC_CODEX.get());
            event.accept(ModItems.ACOLYTE_RUNIC_CIRCUIT.get());
            event.accept(ModItems.ADEPT_RUNIC_CIRCUIT.get());
            event.accept(ModItems.ARCH_RUNIC_CIRCUIT.get());
            event.accept(ModItems.ARCANE_SPIRIT.get());
            event.accept(ModItems.ARCANE_GEMSTONE.get());
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.RUNIC_STONE.get());
            event.accept(ModItems.RUNIC_STONE_SLAB.get());
            event.accept(ModItems.RUNIC_STONE_STAIRS.get());
            event.accept(ModItems.RUNIC_STONE_PILLAR.get());
            event.accept(ModItems.OCULUS_PORTAL.get());
            event.accept(ModItems.OCULUS_CONTROLLER.get());
            event.accept(ModItems.OCULUS_PILLAR.get());
            event.accept(ModItems.RUNESTEEL_PYLON.get());
            event.accept(ModItems.INFUSION_ALTAR.get());
            event.accept(ModItems.RUNIC_ANVIL.get());
        }
    }
}
