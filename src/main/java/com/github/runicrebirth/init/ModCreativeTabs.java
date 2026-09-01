package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RunicRebirth.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCKS_TAB = CREATIVE_TABS.register(
        "blocks",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.runicrebirth.blocks"))
            .icon(() -> new ItemStack(ModItems.RUNIC_STONE.get()))
            .displayItems((params, output) -> {
                output.accept(ModItems.RUNIC_STONE.get());
                output.accept(ModItems.RUNIC_STONE_SLAB.get());
                output.accept(ModItems.RUNIC_STONE_STAIRS.get());
                output.accept(ModItems.RUNIC_STONE_PILLAR.get());
                output.accept(ModItems.RUNIC_STONE_BRICKS.get());
                output.accept(ModItems.CRACKED_RUNIC_STONE_BRICKS.get());
                output.accept(ModItems.CRACKED_RUNIC_STONE_BRICKS_SLAB.get());
                output.accept(ModItems.CRACKED_RUNIC_STONE_BRICKS_STAIRS.get());
                output.accept(ModItems.ARCANE_RUNIC_STONE_BRICKS.get());
                output.accept(ModItems.FROZEN_RUNIC_BRICKS.get());
                output.accept(ModItems.FROZEN_RUNIC_BRICKS_SLAB.get());
                output.accept(ModItems.FROZEN_RUNIC_BRICKS_STAIRS.get());
                output.accept(ModItems.FROZEN_RUNIC_BRICKS_WALL.get());
                output.accept(ModItems.FLAMING_RUNIC_BRICKS.get());
                output.accept(ModItems.FLAMING_RUNIC_BRICKS_SLAB.get());
                output.accept(ModItems.FLAMING_RUNIC_BRICKS_STAIRS.get());
                output.accept(ModItems.FLAMING_RUNIC_BRICKS_WALL.get());
                output.accept(ModItems.EARTHEN_RUNIC_BRICKS.get());
                output.accept(ModItems.EARTHEN_RUNIC_BRICKS_SLAB.get());
                output.accept(ModItems.EARTHEN_RUNIC_BRICKS_STAIRS.get());
                output.accept(ModItems.EARTHEN_RUNIC_BRICKS_WALL.get());
                output.accept(ModItems.WINDSWEPT_RUNIC_BRICKS.get());
                output.accept(ModItems.WINDSWEPT_RUNIC_BRICKS_SLAB.get());
                output.accept(ModItems.WINDSWEPT_RUNIC_BRICKS_STAIRS.get());
                output.accept(ModItems.WINDSWEPT_RUNIC_BRICKS_WALL.get());
                output.accept(ModItems.MOSSY_RUNIC_BRICKS.get());
                output.accept(ModItems.MOSSY_RUNIC_BRICKS_SLAB.get());
                output.accept(ModItems.MOSSY_RUNIC_BRICKS_STAIRS.get());
                output.accept(ModItems.MOSSY_RUNIC_BRICKS_WALL.get());
                output.accept(ModItems.RUNESTEEL_BLOCK.get());
                output.accept(ModItems.FLAMING_RUNESTEEL_BLOCK.get());
                output.accept(ModItems.WINDSWEPT_RUNESTEEL_BLOCK.get());
                output.accept(ModItems.FROZEN_RUNESTEEL_BLOCK.get());
                output.accept(ModItems.EARTHEN_RUNESTEEL_BLOCK.get());
                output.accept(ModItems.FALSE_SKY.get());
                output.accept(ModItems.CRACKED_FALSE_SKY.get());
                output.accept(ModItems.CUT_RUNIC_STONE.get());
                output.accept(ModItems.REINFORCED_CUT_RUNIC_STONE.get());
                output.accept(ModItems.RUNELIGHT_TORCH.get());
                output.accept(ModItems.RUNELIGHT_LANTERN.get());
                output.accept(ModItems.OCULUS_PORTAL.get());
                output.accept(ModItems.OCULUS_CONTROLLER.get());
                output.accept(ModItems.OCULUS_PILLAR.get());
                output.accept(ModItems.RUNESTEEL_PYLON.get());
                output.accept(ModItems.RUNESTEEL_PORTCULLIS.get());
                output.accept(ModItems.INFUSION_ALTAR.get());
                output.accept(ModItems.RUNIC_ANVIL.get());
                output.accept(ModItems.RUNESTEEL_CACHE.get());
                output.accept(ModItems.RETURN_PORTAL.get());
                output.accept(ModItems.DUNGEON_MOB_SPAWNER.get());
                output.accept(ModItems.DUNGEON_ROOM_TRACKER.get());
                output.accept(ModItems.DUNGEON_DOOR.get());
                output.accept(ModItems.DUNGEON_TEMPORARY_PLATFORM.get());
                output.accept(ModItems.CRUMBLING_PLATFORM.get());
                output.accept(ModItems.DUNGEON_PRESSURE_PLATE.get());
                output.accept(ModItems.DUNGEON_SPIKE.get());
                output.accept(ModItems.DUNGEON_BOULDER_SPAWNER.get());
                output.accept(ModItems.DUNGEON_SWINGING_AXE.get());
                output.accept(ModItems.DUNGEON_FLAMETHROWER.get());
                output.accept(ModItems.ANCIENT_ARCANE_TURRET.get());
                output.accept(ModItems.MEDITATION_CUSHION.get());
                output.accept(ModItems.RUNIC_LEVER.get());
                output.accept(ModItems.SECT_BANNER.get());
                output.accept(ModItems.TATTERED_SECT_BANNER.get());
                output.accept(ModItems.SECT_BANNER_MAGE.get());
                output.accept(ModItems.SECT_BANNER_ARTIFICER.get());
                output.accept(ModItems.SECT_BANNER_WIZARD.get());
                output.accept(ModItems.SECT_BANNER_RUNEBLADE.get());
                output.accept(ModItems.TATTERED_SECT_BANNER_MAGE.get());
                output.accept(ModItems.TATTERED_SECT_BANNER_ARTIFICER.get());
                output.accept(ModItems.TATTERED_SECT_BANNER_WIZARD.get());
                output.accept(ModItems.TATTERED_SECT_BANNER_RUNEBLADE.get());
                output.accept(ModItems.ADEPT_MAGE_STATUE.get());
                output.accept(ModItems.ADEPT_WIZARD_STATUE.get());
                output.accept(ModItems.ADEPT_RUNEBLADE_STATUE.get());
                output.accept(ModItems.ADEPT_ARTIFICER_STATUE.get());
            })
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEMS_TAB = CREATIVE_TABS.register(
        "items",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.runicrebirth.items"))
            .icon(() -> new ItemStack(ModItems.ARCANE_ACOLYTE_RING.get()))
            .displayItems((params, output) -> {
                output.accept(ModItems.RUNIC_CODEX.get());
                output.accept(ModItems.ACOLYTE_WAND.get());
                output.accept(ModItems.ADEPT_STAFF.get());
output.accept(ModItems.ACOLYTE_RUNIC_CIRCUIT.get());
                output.accept(ModItems.ADEPT_RUNIC_CIRCUIT.get());
                output.accept(ModItems.ARCH_RUNIC_CIRCUIT.get());
                output.accept(ModItems.BASIC_RUNIC_LONGSWORD.get());
                output.accept(ModItems.RUNIC_DAGGER.get());
                output.accept(ModItems.RUNIC_WARSTAFF.get());
                output.accept(ModItems.RUNIC_SHIELD.get());
                output.accept(ModItems.ARCANE_ACOLYTE_RING.get());
                output.accept(ModItems.ARCANE_TETHER_RING.get());
                output.accept(ModItems.MAGIC_HAND_RING.get());
                output.accept(ModItems.RING_OF_LEAPING_GALES.get());
                output.accept(ModItems.RING_OF_PHANTOM_MINING.get());
                output.accept(ModItems.BLINK_RING.get());
                output.accept(ModItems.THRUSTER_RING.get());
                output.accept(ModItems.HOVER_RING.get());
                output.accept(ModItems.RING_OF_EXPANSION.get());
                output.accept(ModItems.ARCANE_DRONE.get());
                output.accept(ModItems.HAMMER_DRONE.get());
                output.accept(ModItems.RUNIC_KEY_RING.get());
                output.accept(ModItems.ARCANE_SPIRIT.get());
                output.accept(ModItems.ARCANE_GEMSTONE.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ICE_ACOLYTE.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ICE_ADEPT.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ICE_ARCH.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_FIRE_ACOLYTE.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_FIRE_ADEPT.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_FIRE_ARCH.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_EARTH_ACOLYTE.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_EARTH_ADEPT.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_EARTH_ARCH.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_WIND_ACOLYTE.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_WIND_ADEPT.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_WIND_ARCH.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ARCANE_ACOLYTE.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ARCANE_ADEPT.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ARCANE_ARCH.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ARCANUM_ACOLYTE.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ARCANUM_ADEPT.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ARCANUM_ARCH.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ORDER_ACOLYTE.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ORDER_ADEPT.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_ORDER_ARCH.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_VIGSALR_ACOLYTE.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_VIGSALR_ADEPT.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_VIGSALR_ARCH.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_YOTOR_ACOLYTE.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_YOTOR_ADEPT.get());
                output.accept(ModItems.ENHANCEMENT_RUNE_YOTOR_ARCH.get());
            })
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARMOR_TAB = CREATIVE_TABS.register(
        "armor",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.runicrebirth.armor"))
            .icon(() -> new ItemStack(ModItems.ADEPT_WIZARD_HAT.get()))
            .displayItems((params, output) -> {
                output.accept(ModItems.ACOLYTE_WIZARD_HAT.get());
                output.accept(ModItems.ACOLYTE_ROBES.get());
                output.accept(ModItems.ACOLYTE_PANTS.get());
                output.accept(ModItems.ACOLYTE_BOOTS.get());
                output.accept(ModItems.ACOLYTE_MAGE_HOOD.get());
                output.accept(ModItems.ACOLYTE_ARTIFICER_HEADGEAR.get());
                output.accept(ModItems.ACOLYTE_RUNEBLADE_HELMET.get());
                output.accept(ModItems.ADEPT_WIZARD_HAT.get());
                output.accept(ModItems.ADEPT_WIZARD_ROBES.get());
                output.accept(ModItems.ADEPT_WIZARD_PANTS.get());
                output.accept(ModItems.ADEPT_WIZARD_BOOTS.get());
                output.accept(ModItems.ADEPT_MAGE_HOOD.get());
                output.accept(ModItems.ADEPT_MAGE_ROBES.get());
                output.accept(ModItems.ADEPT_MAGE_PANTS.get());
                output.accept(ModItems.ADEPT_MAGE_BOOTS.get());
                output.accept(ModItems.ADEPT_ARTIFICER_HEADGEAR.get());
                output.accept(ModItems.ADEPT_ARTIFICER_CHESTGEAR.get());
                output.accept(ModItems.ADEPT_ARTIFICER_PANTS.get());
                output.accept(ModItems.ADEPT_ARTIFICER_BOOTS.get());
                output.accept(ModItems.ADEPT_RUNEBLADE_HELMET.get());
                output.accept(ModItems.ADEPT_RUNEBLADE_CHESTPLATE.get());
                output.accept(ModItems.ADEPT_RUNEBLADE_LEGGINGS.get());
                output.accept(ModItems.ADEPT_RUNEBLADE_BOOTS.get());
            })
            .build());

    private ModCreativeTabs() {}
}
