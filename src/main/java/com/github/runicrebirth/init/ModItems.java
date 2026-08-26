package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.AdeptStaffItem;
import com.github.runicrebirth.items.EnhancementRuneItem;
import com.github.runicrebirth.items.DungeonRoomConfiguratorItem;
import com.github.runicrebirth.items.AcolyteWandItem;
import com.github.runicrebirth.items.BasicRunicLongsword;
import com.github.runicrebirth.items.RunicDaggerItem;
import com.github.runicrebirth.items.RunicWarstaffItem;
import com.github.runicrebirth.items.RunicShieldItem;
import com.github.runicrebirth.items.RunelightTorchItem;
import com.github.runicrebirth.items.RunicBlockItem;
import com.github.runicrebirth.items.RunicCircuitItem;
import com.github.runicrebirth.items.RunicCodexItem;
import com.github.runicrebirth.items.armor.AcolyteSetItem;
import com.github.runicrebirth.items.armor.AdeptSetItem;
import com.github.runicrebirth.items.armor.DyeableAcolyteSetItem;
import com.github.runicrebirth.items.armor.DyeableAdeptSetItem;
import com.github.runicrebirth.items.RunicKeyRingItem;
import com.github.runicrebirth.items.SectBannerItem;
import com.github.runicrebirth.items.TatteredSectBannerItem;
import com.github.runicrebirth.items.SectBannerVariantItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import com.github.runicrebirth.items.curios.ArcaneAcolyteRingItem;
import com.github.runicrebirth.items.curios.ArcaneDroneItem;
import com.github.runicrebirth.items.curios.ArcaneTetherRingItem;
import com.github.runicrebirth.items.curios.HammerDroneItem;
import com.github.runicrebirth.items.curios.BlinkRingItem;
import com.github.runicrebirth.items.curios.MagicHandRingItem;
import com.github.runicrebirth.items.curios.RingOfExpansionItem;
import com.github.runicrebirth.items.curios.HoverRingItem;
import com.github.runicrebirth.items.curios.RingOfLeapingGalesItem;
import com.github.runicrebirth.items.curios.RingOfPhantomMiningItem;
import com.github.runicrebirth.items.curios.ThrusterRingItem;
import com.github.runicrebirth.spells.modifiers.AdditiveSizeModifier;
import com.github.runicrebirth.util.ItemPropertiesHelper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RunicRebirth.MODID);

    public static final DeferredItem<AcolyteWandItem> ACOLYTE_WAND = ITEMS.register(
        "acolyte_wand", () -> new AcolyteWandItem(ItemPropertiesHelper.wand()
            .component(ModDataComponents.WAND_STACKS.get(), com.github.runicrebirth.api.spells.WandStacksData.createDefault(2))
            .component(ModDataComponents.MAX_INSCRIPTIONS.get(), 2)
            .component(ModDataComponents.MAX_MODIFIER_SLOTS.get(), 1)
            .component(ModDataComponents.INITIAL_CHARGES.get(), 2)));

    public static final DeferredItem<AdeptStaffItem> ADEPT_STAFF = ITEMS.register(
        "adept_staff", () -> new AdeptStaffItem(ItemPropertiesHelper.wand()
            .component(ModDataComponents.WAND_STACKS.get(), com.github.runicrebirth.api.spells.WandStacksData.createDefault(2))
            .component(ModDataComponents.MAX_INSCRIPTIONS.get(), 2)
            .component(ModDataComponents.MAX_MODIFIER_SLOTS.get(), 3)
            .component(ModDataComponents.INITIAL_CHARGES.get(), 3)));

public static final DeferredItem<RingOfExpansionItem> RING_OF_EXPANSION = ITEMS.registerItem(
        "ring_of_expansion", RingOfExpansionItem::new, ItemPropertiesHelper.equipment().rarity(net.minecraft.world.item.Rarity.UNCOMMON));

    public static final DeferredItem<ArcaneAcolyteRingItem> ARCANE_ACOLYTE_RING = ITEMS.registerItem(
        "arcane_acolyte_ring", ArcaneAcolyteRingItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<ArcaneTetherRingItem> ARCANE_TETHER_RING = ITEMS.registerItem(
        "arcane_tether_ring", ArcaneTetherRingItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<MagicHandRingItem> MAGIC_HAND_RING = ITEMS.registerItem(
        "magic_hand_ring", MagicHandRingItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<RingOfLeapingGalesItem> RING_OF_LEAPING_GALES = ITEMS.registerItem(
        "ring_of_leaping_gales", RingOfLeapingGalesItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<RingOfPhantomMiningItem> RING_OF_PHANTOM_MINING = ITEMS.registerItem(
        "ring_of_phantom_mining", RingOfPhantomMiningItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<BlinkRingItem> BLINK_RING = ITEMS.registerItem(
        "blink_ring", BlinkRingItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<ThrusterRingItem> THRUSTER_RING = ITEMS.registerItem(
        "thruster_ring", ThrusterRingItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<HoverRingItem> HOVER_RING = ITEMS.registerItem(
        "hover_ring", HoverRingItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<ArcaneDroneItem> ARCANE_DRONE = ITEMS.registerItem(
        "arcane_drone", ArcaneDroneItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<HammerDroneItem> HAMMER_DRONE = ITEMS.registerItem(
        "hammer_drone", HammerDroneItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<RunicKeyRingItem> RUNIC_KEY_RING = ITEMS.registerItem(
        "runic_key_ring", RunicKeyRingItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<RunicCodexItem> RUNIC_CODEX = ITEMS.registerItem(
        "runic_codex", RunicCodexItem::new, new Item.Properties().stacksTo(1));

    private static final int ACOLYTE_DURABILITY_MULT = 5;

    public static final DeferredItem<DyeableAcolyteSetItem> ACOLYTE_WIZARD_HAT = ITEMS.registerItem(
        "acolyte_wizard_hat",
        props -> new DyeableAcolyteSetItem(ArmorItem.Type.HELMET, props, "acolyte_wizard_hat", List.of(new AdditiveSizeModifier(2f))),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAcolyteSetItem> ACOLYTE_ROBES = ITEMS.registerItem(
        "acolyte_robes",
        props -> new DyeableAcolyteSetItem(ArmorItem.Type.CHESTPLATE, props, "acolyte_set", List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAcolyteSetItem> ACOLYTE_PANTS = ITEMS.registerItem(
        "acolyte_pants",
        props -> new DyeableAcolyteSetItem(ArmorItem.Type.LEGGINGS, props, "acolyte_set", List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAcolyteSetItem> ACOLYTE_BOOTS = ITEMS.registerItem(
        "acolyte_boots",
        props -> new DyeableAcolyteSetItem(ArmorItem.Type.BOOTS, props, "acolyte_set", List.of()),
        new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_ARTIFICER_HEADGEAR = ITEMS.registerItem(
        "acolyte_artificer_headgear",
        props -> new AcolyteSetItem(ArmorItem.Type.HELMET, props, "acolyte_artificer_headgear", List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_MAGE_HOOD = ITEMS.registerItem(
        "acolyte_mage_hood",
        props -> new AcolyteSetItem(ArmorItem.Type.HELMET, props, "acolyte_mage_hood", List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_RUNEBLADE_HELMET = ITEMS.registerItem(
        "acolyte_runeblade_helmet",
        props -> new AcolyteSetItem(ArmorItem.Type.HELMET, props, "acolyte_runeblade_helmet", List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    // --- Adept Tier Armor ---
    private static final int ADEPT_DURABILITY_MULT = 33;

    // Adept Runeblade Set
    public static final DeferredItem<AdeptSetItem> ADEPT_RUNEBLADE_HELMET = ITEMS.registerItem(
        "adept_runeblade_helmet",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT_RUNEBLADE, ArmorItem.Type.HELMET, props, "adept_runeblade_armor", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_RUNEBLADE_CHESTPLATE = ITEMS.registerItem(
        "adept_runeblade_chestplate",
        props -> new DyeableAdeptSetItem(ModArmorMaterials.ADEPT_RUNEBLADE, ArmorItem.Type.CHESTPLATE, props, "adept_runeblade_armor", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_RUNEBLADE_LEGGINGS = ITEMS.registerItem(
        "adept_runeblade_leggings",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT_RUNEBLADE, ArmorItem.Type.LEGGINGS, props, "adept_runeblade_armor", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_RUNEBLADE_BOOTS = ITEMS.registerItem(
        "adept_runeblade_boots",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT_RUNEBLADE, ArmorItem.Type.BOOTS, props, "adept_runeblade_armor", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Adept Wizard Set
    public static final DeferredItem<AdeptSetItem> ADEPT_WIZARD_HAT = ITEMS.registerItem(
        "adept_wizard_hat",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.HELMET, props, "adept_wizard_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_WIZARD_ROBES = ITEMS.registerItem(
        "adept_wizard_robes",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.CHESTPLATE, props, "adept_wizard_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_WIZARD_PANTS = ITEMS.registerItem(
        "adept_wizard_pants",
        props -> new DyeableAdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.LEGGINGS, props, "adept_wizard_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_WIZARD_BOOTS = ITEMS.registerItem(
        "adept_wizard_boots",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.BOOTS, props, "adept_wizard_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Adept Mage Set
    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_MAGE_HOOD = ITEMS.registerItem(
        "adept_mage_hood",
        props -> new DyeableAdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.HELMET, props, "adept_mage_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_MAGE_ROBES = ITEMS.registerItem(
        "adept_mage_robes",
        props -> new DyeableAdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.CHESTPLATE, props, "adept_mage_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_MAGE_PANTS = ITEMS.registerItem(
        "adept_mage_pants",
        props -> new DyeableAdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.LEGGINGS, props, "adept_mage_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_MAGE_BOOTS = ITEMS.registerItem(
        "adept_mage_boots",
        props -> new DyeableAdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.BOOTS, props, "adept_mage_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Adept Artificer Set
    public static final DeferredItem<AdeptSetItem> ADEPT_ARTIFICER_HEADGEAR = ITEMS.registerItem(
        "adept_artificer_headgear",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.HELMET, props, "adept_artificer_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_ARTIFICER_CHESTGEAR = ITEMS.registerItem(
        "adept_artificer_chestgear",
        props -> new DyeableAdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.CHESTPLATE, props, "adept_artificer_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_ARTIFICER_PANTS = ITEMS.registerItem(
        "adept_artificer_pants",
        props -> new DyeableAdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.LEGGINGS, props, "adept_artificer_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_ARTIFICER_BOOTS = ITEMS.registerItem(
        "adept_artificer_boots",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.BOOTS, props, "adept_artificer_set", "adept_armor", List.of()),
        new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Runic Circuits
    public static final DeferredItem<RunicCircuitItem> ACOLYTE_RUNIC_CIRCUIT = ITEMS.register(
        "acolyte_runic_circuit", () -> new RunicCircuitItem(
            new Item.Properties().component(ModDataComponents.CIRCUIT_TIER.get(), 1), 1));
    public static final DeferredItem<RunicCircuitItem> ADEPT_RUNIC_CIRCUIT = ITEMS.register(
        "adept_runic_circuit", () -> new RunicCircuitItem(
            new Item.Properties().component(ModDataComponents.CIRCUIT_TIER.get(), 2), 2));
    public static final DeferredItem<RunicCircuitItem> ARCH_RUNIC_CIRCUIT = ITEMS.register(
        "arch_runic_circuit", () -> new RunicCircuitItem(
            new Item.Properties().component(ModDataComponents.CIRCUIT_TIER.get(), 3), 3));

    public static final DeferredItem<BasicRunicLongsword> BASIC_RUNIC_LONGSWORD = ITEMS.registerItem(
        "basic_runic_longsword", BasicRunicLongsword::new, new Item.Properties().stacksTo(1).attributes(
            SwordItem.createAttributes(Tiers.IRON, 3, -2.4F)));

    public static final DeferredItem<RunicDaggerItem> RUNIC_DAGGER = ITEMS.register(
        "runic_dagger", () -> new RunicDaggerItem(new Item.Properties().stacksTo(1).attributes(
            SwordItem.createAttributes(Tiers.IRON, 3, -0.8F))));

    public static final DeferredItem<RunicWarstaffItem> RUNIC_WARSTAFF = ITEMS.register(
        "runic_warstaff", () -> new RunicWarstaffItem(new Item.Properties().stacksTo(1).attributes(
            SwordItem.createAttributes(Tiers.DIAMOND, 0, -2.0F))));

    public static final DeferredItem<RunicShieldItem> RUNIC_SHIELD = ITEMS.register(
        "runic_shield", () -> new RunicShieldItem(new Item.Properties()));

    // Dungeon items
    public static final DeferredItem<DungeonRoomConfiguratorItem> DUNGEON_ROOM_CONFIGURATOR =
        ITEMS.registerItem("dungeon_room_configurator", DungeonRoomConfiguratorItem::new, new Item.Properties());

    public static final DeferredItem<Item> ARCANE_SPIRIT = ITEMS.registerSimpleItem("arcane_spirit", new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> ARCANE_GEMSTONE = ITEMS.registerSimpleItem("arcane_gemstone", new Item.Properties().stacksTo(64));

    // Block items
    public static final DeferredItem<BlockItem> RUNIC_STONE = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE);
    public static final DeferredItem<BlockItem> RUNIC_STONE_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_SLAB);
    public static final DeferredItem<BlockItem> RUNIC_STONE_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_STAIRS);
    public static final DeferredItem<BlockItem> RUNIC_STONE_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_WALL);
    public static final DeferredItem<BlockItem> RUNIC_STONE_PILLAR = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_PILLAR);
    public static final DeferredItem<BlockItem> RUNIC_STONE_BRICKS = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_BRICKS);
    public static final DeferredItem<BlockItem> RUNIC_STONE_BRICKS_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_BRICKS_SLAB);
    public static final DeferredItem<BlockItem> RUNIC_STONE_BRICKS_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_BRICKS_STAIRS);
    public static final DeferredItem<BlockItem> RUNIC_STONE_BRICKS_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_BRICKS_WALL);
    public static final DeferredItem<BlockItem> ARCANE_RUNIC_STONE_BRICKS = ITEMS.registerSimpleBlockItem(ModBlocks.ARCANE_RUNIC_STONE_BRICKS);
    public static final DeferredItem<BlockItem> CRACKED_RUNIC_STONE_BRICKS = ITEMS.registerSimpleBlockItem(ModBlocks.CRACKED_RUNIC_STONE_BRICKS);
    public static final DeferredItem<BlockItem> CRACKED_RUNIC_STONE_BRICKS_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.CRACKED_RUNIC_STONE_BRICKS_SLAB);
    public static final DeferredItem<BlockItem> CRACKED_RUNIC_STONE_BRICKS_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.CRACKED_RUNIC_STONE_BRICKS_STAIRS);
    public static final DeferredItem<BlockItem> CRACKED_RUNIC_STONE_BRICKS_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.CRACKED_RUNIC_STONE_BRICKS_WALL);
    public static final DeferredItem<BlockItem> FROZEN_RUNIC_BRICKS = ITEMS.registerSimpleBlockItem(ModBlocks.FROZEN_RUNIC_BRICKS);
    public static final DeferredItem<BlockItem> FROZEN_RUNIC_BRICKS_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.FROZEN_RUNIC_BRICKS_SLAB);
    public static final DeferredItem<BlockItem> FROZEN_RUNIC_BRICKS_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.FROZEN_RUNIC_BRICKS_STAIRS);
    public static final DeferredItem<BlockItem> FROZEN_RUNIC_BRICKS_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.FROZEN_RUNIC_BRICKS_WALL);
    public static final DeferredItem<BlockItem> FLAMING_RUNIC_BRICKS = ITEMS.registerSimpleBlockItem(ModBlocks.FLAMING_RUNIC_BRICKS);
    public static final DeferredItem<BlockItem> FLAMING_RUNIC_BRICKS_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.FLAMING_RUNIC_BRICKS_SLAB);
    public static final DeferredItem<BlockItem> FLAMING_RUNIC_BRICKS_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.FLAMING_RUNIC_BRICKS_STAIRS);
    public static final DeferredItem<BlockItem> FLAMING_RUNIC_BRICKS_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.FLAMING_RUNIC_BRICKS_WALL);
    public static final DeferredItem<BlockItem> EARTHEN_RUNIC_BRICKS = ITEMS.registerSimpleBlockItem(ModBlocks.EARTHEN_RUNIC_BRICKS);
    public static final DeferredItem<BlockItem> EARTHEN_RUNIC_BRICKS_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.EARTHEN_RUNIC_BRICKS_SLAB);
    public static final DeferredItem<BlockItem> EARTHEN_RUNIC_BRICKS_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.EARTHEN_RUNIC_BRICKS_STAIRS);
    public static final DeferredItem<BlockItem> EARTHEN_RUNIC_BRICKS_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.EARTHEN_RUNIC_BRICKS_WALL);
    public static final DeferredItem<BlockItem> WINDSWEPT_RUNIC_BRICKS = ITEMS.registerSimpleBlockItem(ModBlocks.WINDSWEPT_RUNIC_BRICKS);
    public static final DeferredItem<BlockItem> WINDSWEPT_RUNIC_BRICKS_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.WINDSWEPT_RUNIC_BRICKS_SLAB);
    public static final DeferredItem<BlockItem> WINDSWEPT_RUNIC_BRICKS_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.WINDSWEPT_RUNIC_BRICKS_STAIRS);
    public static final DeferredItem<BlockItem> WINDSWEPT_RUNIC_BRICKS_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.WINDSWEPT_RUNIC_BRICKS_WALL);
    public static final DeferredItem<BlockItem> MOSSY_RUNIC_BRICKS = ITEMS.registerSimpleBlockItem(ModBlocks.MOSSY_RUNIC_BRICKS);
    public static final DeferredItem<BlockItem> MOSSY_RUNIC_BRICKS_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.MOSSY_RUNIC_BRICKS_SLAB);
    public static final DeferredItem<BlockItem> MOSSY_RUNIC_BRICKS_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.MOSSY_RUNIC_BRICKS_STAIRS);
    public static final DeferredItem<BlockItem> MOSSY_RUNIC_BRICKS_WALL = ITEMS.registerSimpleBlockItem(ModBlocks.MOSSY_RUNIC_BRICKS_WALL);
    public static final DeferredItem<BlockItem> RUNESTEEL_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.RUNESTEEL_BLOCK);
    public static final DeferredItem<BlockItem> FLAMING_RUNESTEEL_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.FLAMING_RUNESTEEL_BLOCK);
    public static final DeferredItem<BlockItem> WINDSWEPT_RUNESTEEL_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.WINDSWEPT_RUNESTEEL_BLOCK);
    public static final DeferredItem<BlockItem> FROZEN_RUNESTEEL_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.FROZEN_RUNESTEEL_BLOCK);
    public static final DeferredItem<BlockItem> EARTHEN_RUNESTEEL_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.EARTHEN_RUNESTEEL_BLOCK);
    public static final DeferredItem<BlockItem> FALSE_SKY = ITEMS.registerSimpleBlockItem(ModBlocks.FALSE_SKY);
    public static final DeferredItem<BlockItem> CRACKED_FALSE_SKY = ITEMS.registerSimpleBlockItem(ModBlocks.CRACKED_FALSE_SKY);
    public static final DeferredItem<BlockItem> CUT_RUNIC_STONE = ITEMS.registerSimpleBlockItem(ModBlocks.CUT_RUNIC_STONE);
    public static final DeferredItem<BlockItem> REINFORCED_CUT_RUNIC_STONE = ITEMS.registerSimpleBlockItem(ModBlocks.REINFORCED_CUT_RUNIC_STONE);
    public static final DeferredItem<RunicBlockItem> OCULUS_PORTAL = ITEMS.register("oculus_portal",
            () -> new RunicBlockItem(ModBlocks.OCULUS_PORTAL.get(), new Item.Properties(), "inactive", "oculus_portal")
                    .withHandRotationY(90));
    public static final DeferredItem<RunicBlockItem> OCULUS_CONTROLLER = ITEMS.register("oculus_controller",
            () -> new RunicBlockItem(ModBlocks.OCULUS_CONTROLLER.get(), new Item.Properties(), "idle", "oculus_controller"));
    public static final DeferredItem<RunicBlockItem> OCULUS_PILLAR = ITEMS.register("oculus_pillar",
            () -> new RunicBlockItem(ModBlocks.OCULUS_PILLAR.get(), new Item.Properties(), "idle", "oculus_pillar")
                .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                .withGuiTranslation(0f,-0.4f,0f));
    public static final DeferredItem<RunicBlockItem> RUNESTEEL_PYLON = ITEMS.register("runesteel_pylon",
            () -> new RunicBlockItem(ModBlocks.RUNESTEEL_PYLON.get(), new Item.Properties(), "idle", "runesteel_pylon")
                    .withGuiScale(1.5f).withGuiTranslation(0f, -0.4f, 0f));
    public static final DeferredItem<RunicBlockItem> INFUSION_ALTAR = ITEMS.register("infusion_altar",
            () -> new RunicBlockItem(ModBlocks.INFUSION_ALTAR.get(), new Item.Properties(), "idle", "infusion_altar")
                .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                .withGuiTranslation(0f, -0.4f, 0f)
                .withGuiScale(0.8f));
    public static final DeferredItem<RunicBlockItem> RUNIC_ANVIL = ITEMS.register("runic_anvil",
            () -> new RunicBlockItem(ModBlocks.RUNIC_ANVIL.get(), new Item.Properties(), "idle", "runic_anvil")
                    .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                    .withGuiTranslation(0f, -0.35f, 0f));

    public static final DeferredItem<RunicBlockItem> RUNESTEEL_CACHE = ITEMS.register("runesteel_cache",
            () -> new RunicBlockItem(ModBlocks.RUNESTEEL_CACHE.get(), new Item.Properties(), "idle", "runesteel_cache")
                    .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                    .withGuiTranslation(0f, -0.35f, 0f));
    public static final DeferredItem<BlockItem> RETURN_PORTAL = ITEMS.registerSimpleBlockItem(ModBlocks.RETURN_PORTAL);
    public static final DeferredItem<RunicBlockItem> DUNGEON_MOB_SPAWNER = ITEMS.register("dungeon_mob_spawner",
        () -> new RunicBlockItem(ModBlocks.DUNGEON_MOB_SPAWNER.get(), new Item.Properties(), "hold_spawning", "dungeon_mob_spawner")
            .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
            .withGuiTranslation(0f,-0.4f,0f)
            .withGuiScale(0.5f));
    public static final DeferredItem<BlockItem> DUNGEON_ROOM_TRACKER = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_ROOM_TRACKER);
    public static final DeferredItem<RunicBlockItem> RUNESTEEL_PORTCULLIS = ITEMS.register("runesteel_portcullis",
            () -> new RunicBlockItem(ModBlocks.RUNESTEEL_PORTCULLIS.get(), new Item.Properties(), "open_top", "runesteel_portcullis")
                .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                .withGuiTranslation(0f, -0.3f, 0f)
                .withHandRotationY(30));

    public static final DeferredItem<RunicBlockItem> DUNGEON_DOOR = ITEMS.register("dungeon_door",
            () -> new RunicBlockItem(ModBlocks.DUNGEON_DOOR.get(), new Item.Properties(), "idle", "dungeon_door")
                .withGuiScale(0.3f)
                .withGuiTranslation(0f, -0.35f, 0f));

    public static final DeferredItem<RunelightTorchItem> RUNELIGHT_TORCH = ITEMS.register("runelight_torch",
            () -> new RunelightTorchItem(ModBlocks.RUNELIGHT_TORCH.get(), ModBlocks.RUNELIGHT_WALL_TORCH.get(), new Item.Properties()));

    public static final DeferredItem<RunicBlockItem> RUNELIGHT_LANTERN = ITEMS.register("runelight_lantern",
            () -> new RunicBlockItem(ModBlocks.RUNELIGHT_LANTERN.get(), new Item.Properties(), "idle", "runelight_lantern")
                    .withTexture("textures/entity/runic_templates/arcane_runic_template.png"));

    public static final DeferredItem<RunicBlockItem> ANCIENT_ARCANE_TURRET = ITEMS.register("ancient_arcane_turret",
      () -> new RunicBlockItem(ModBlocks.ANCIENT_ARCANE_TURRET.get(), new Item.Properties(), "idle", "ancient_arcane_turret_floor")
          .withTexture("textures/entity/runic_templates/arcane_runic_template.png"));

    // Dungeon trap block items
    public static final DeferredItem<BlockItem> DUNGEON_TEMPORARY_PLATFORM = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_TEMPORARY_PLATFORM);
    public static final DeferredItem<BlockItem> DUNGEON_PRESSURE_PLATE = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> DUNGEON_SPIKE = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_SPIKE);
    public static final DeferredItem<BlockItem> CRUMBLING_PLATFORM = ITEMS.registerSimpleBlockItem(ModBlocks.CRUMBLING_PLATFORM);
    public static final DeferredItem<RunicBlockItem> DUNGEON_BOULDER_SPAWNER = ITEMS.register("dungeon_boulder_spawner",
            () -> new RunicBlockItem(ModBlocks.DUNGEON_BOULDER_SPAWNER.get(), new Item.Properties(), "idle", "dungeon_boulder_spawner")
                .withTexture("textures/entity/runic_templates/earth_runic_template.png")
                .withGuiScale(0.33f));
    public static final DeferredItem<RunicBlockItem> DUNGEON_SWINGING_AXE = ITEMS.register("dungeon_swinging_axe",
            () -> new RunicBlockItem(ModBlocks.DUNGEON_SWINGING_AXE.get(), new Item.Properties(), "swinging_axe", "dungeon_swinging_axe")
                .withTexture("textures/entity/runic_templates/earth_runic_template.png")
                .withGuiScale(0.3f)
                .withGuiTranslation(0f, 0.45f, 0f));
    public static final DeferredItem<BlockItem> DUNGEON_FLAMETHROWER = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_FLAMETHROWER);
    public static final DeferredItem<BlockItem> MEDITATION_CUSHION = ITEMS.registerSimpleBlockItem(ModBlocks.MEDITATION_CUSHION);
    public static final DeferredItem<RunicBlockItem> RUNIC_LEVER = ITEMS.register("runic_lever",
            () -> new RunicBlockItem(ModBlocks.RUNIC_LEVER.get(), new Item.Properties(), "off", "runic_lever")
                .withTexture("textures/entity/runic_templates/earth_runic_template.png")
                .withGuiTranslation(0f, -0.4f, 0f));

    // --- Sect Banner items ---
    public static final DeferredItem<SectBannerItem> SECT_BANNER = ITEMS.register("sect_banner",
            () -> (SectBannerItem) new SectBannerItem(ModBlocks.SECT_BANNER.get(),
                    new Item.Properties().stacksTo(16)
                            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY))
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f));

    public static final DeferredItem<TatteredSectBannerItem> TATTERED_SECT_BANNER = ITEMS.register("tattered_sect_banner",
            () -> (TatteredSectBannerItem) new TatteredSectBannerItem(ModBlocks.TATTERED_SECT_BANNER.get(),
                    new Item.Properties().stacksTo(16)
                            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY))
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f));

    public static final DeferredItem<SectBannerVariantItem> SECT_BANNER_MAGE = ITEMS.register("sect_banner_mage",
            () -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.SECT_BANNER_MAGE.get(),
                    new Item.Properties().stacksTo(16)
                            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY))
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f));

    public static final DeferredItem<SectBannerVariantItem> SECT_BANNER_ARTIFICER = ITEMS.register("sect_banner_artificer",
            () -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.SECT_BANNER_ARTIFICER.get(),
                    new Item.Properties().stacksTo(16)
                            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY))
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f));

    public static final DeferredItem<SectBannerVariantItem> SECT_BANNER_WIZARD = ITEMS.register("sect_banner_wizard",
            () -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.SECT_BANNER_WIZARD.get(),
                    new Item.Properties().stacksTo(16)
                            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY))
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f));

    public static final DeferredItem<SectBannerVariantItem> SECT_BANNER_RUNEBLADE = ITEMS.register("sect_banner_runeblade",
            () -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.SECT_BANNER_RUNEBLADE.get(),
                    new Item.Properties().stacksTo(16)
                            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY))
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f));

    public static final DeferredItem<SectBannerVariantItem> TATTERED_SECT_BANNER_MAGE = ITEMS.register("tattered_sect_banner_mage",
            () -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.TATTERED_SECT_BANNER_MAGE.get(),
                    new Item.Properties().stacksTo(16)
                            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY))
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f));

    public static final DeferredItem<SectBannerVariantItem> TATTERED_SECT_BANNER_ARTIFICER = ITEMS.register("tattered_sect_banner_artificer",
            () -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.TATTERED_SECT_BANNER_ARTIFICER.get(),
                    new Item.Properties().stacksTo(16)
                            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY))
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f));

    public static final DeferredItem<SectBannerVariantItem> TATTERED_SECT_BANNER_WIZARD = ITEMS.register("tattered_sect_banner_wizard",
            () -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.TATTERED_SECT_BANNER_WIZARD.get(),
                    new Item.Properties().stacksTo(16)
                            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY))
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f));

    public static final DeferredItem<SectBannerVariantItem> TATTERED_SECT_BANNER_RUNEBLADE = ITEMS.register("tattered_sect_banner_runeblade",
            () -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.TATTERED_SECT_BANNER_RUNEBLADE.get(),
                    new Item.Properties().stacksTo(16)
                            .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY))
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f));

    // --- Adept Set Statues ---
    public static final DeferredItem<RunicBlockItem> ADEPT_MAGE_STATUE = ITEMS.register("adept_mage_statue",
            () -> new RunicBlockItem(ModBlocks.ADEPT_MAGE_STATUE.get(), new Item.Properties(), "idle", "adept_mage_set_statue")
                    .withTexture("textures/block/adept_armor_statue_texture.png")
                    .withGuiScale(0.5f)
                    .withGuiTranslation(0.0f,-0.5f,0.0f));
    public static final DeferredItem<RunicBlockItem> ADEPT_WIZARD_STATUE = ITEMS.register("adept_wizard_statue",
            () -> new RunicBlockItem(ModBlocks.ADEPT_WIZARD_STATUE.get(), new Item.Properties(), "idle", "adept_wizard_set_statue")
                    .withTexture("textures/block/adept_armor_statue_texture.png")
                    .withGuiScale(0.5f)
                    .withGuiTranslation(0.0f,-0.5f,0.0f));
    public static final DeferredItem<RunicBlockItem> ADEPT_RUNEBLADE_STATUE = ITEMS.register("adept_runeblade_statue",
            () -> new RunicBlockItem(ModBlocks.ADEPT_RUNEBLADE_STATUE.get(), new Item.Properties(), "idle", "adept_runeblade_armor_statue")
                    .withTexture("textures/block/adept_armor_statue_texture.png")
                    .withGuiScale(0.5f)
                    .withGuiTranslation(0.0f,-0.5f,0.0f));
    public static final DeferredItem<RunicBlockItem> ADEPT_ARTIFICER_STATUE = ITEMS.register("adept_artificer_statue",
            () -> new RunicBlockItem(ModBlocks.ADEPT_ARTIFICER_STATUE.get(), new Item.Properties(), "idle", "adept_artificer_set_statue")
                    .withTexture("textures/block/adept_armor_statue_texture.png")
                    .withGuiScale(0.5f)
                    .withGuiTranslation(0.0f,-0.5f,0.0f));

    // Enhancement Runes - Ice
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ICE_ACOLYTE = ITEMS.register(
        "enhancement_rune_ice_acolyte", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_ice"), 1, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ICE_ADEPT = ITEMS.register(
        "enhancement_rune_ice_adept", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_ice"), 2, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ICE_ARCH = ITEMS.register(
        "enhancement_rune_ice_arch", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_ice"), 3, new Item.Properties()));

    // Enhancement Runes - Fire
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_FIRE_ACOLYTE = ITEMS.register(
        "enhancement_rune_fire_acolyte", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_fire"), 1, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_FIRE_ADEPT = ITEMS.register(
        "enhancement_rune_fire_adept", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_fire"), 2, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_FIRE_ARCH = ITEMS.register(
        "enhancement_rune_fire_arch", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_fire"), 3, new Item.Properties()));

    // Enhancement Runes - Earth
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_EARTH_ACOLYTE = ITEMS.register(
        "enhancement_rune_earth_acolyte", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_earth"), 1, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_EARTH_ADEPT = ITEMS.register(
        "enhancement_rune_earth_adept", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_earth"), 2, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_EARTH_ARCH = ITEMS.register(
        "enhancement_rune_earth_arch", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_earth"), 3, new Item.Properties()));

    // Enhancement Runes - Wind
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_WIND_ACOLYTE = ITEMS.register(
        "enhancement_rune_wind_acolyte", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_wind"), 1, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_WIND_ADEPT = ITEMS.register(
        "enhancement_rune_wind_adept", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_wind"), 2, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_WIND_ARCH = ITEMS.register(
        "enhancement_rune_wind_arch", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_wind"), 3, new Item.Properties()));

    // Enhancement Runes - Arcane
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANE_ACOLYTE = ITEMS.register(
        "enhancement_rune_arcane_acolyte", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_arcane"), 1, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANE_ADEPT = ITEMS.register(
        "enhancement_rune_arcane_adept", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_arcane"), 2, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANE_ARCH = ITEMS.register(
        "enhancement_rune_arcane_arch", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "element_arcane"), 3, new Item.Properties()));

    // Enhancement Runes - Arcanum (Wizard/Staff)
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANUM_ACOLYTE = ITEMS.register(
        "enhancement_rune_arcanum_acolyte", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_arcanum"), 1, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANUM_ADEPT = ITEMS.register(
        "enhancement_rune_arcanum_adept", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_arcanum"), 2, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANUM_ARCH = ITEMS.register(
        "enhancement_rune_arcanum_arch", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_arcanum"), 3, new Item.Properties()));

    // Enhancement Runes - Order (Mage/Wand)
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ORDER_ACOLYTE = ITEMS.register(
        "enhancement_rune_order_acolyte", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_order"), 1, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ORDER_ADEPT = ITEMS.register(
        "enhancement_rune_order_adept", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_order"), 2, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ORDER_ARCH = ITEMS.register(
        "enhancement_rune_order_arch", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_order"), 3, new Item.Properties()));

    // Enhancement Runes - Vigsalr (Runeblade/Weapon)
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_VIGSALR_ACOLYTE = ITEMS.register(
        "enhancement_rune_vigsalr_acolyte", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_vigsalr"), 1, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_VIGSALR_ADEPT = ITEMS.register(
        "enhancement_rune_vigsalr_adept", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_vigsalr"), 2, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_VIGSALR_ARCH = ITEMS.register(
        "enhancement_rune_vigsalr_arch", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_vigsalr"), 3, new Item.Properties()));

    // Enhancement Runes - Yotor (Artificer/Drone)
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_YOTOR_ACOLYTE = ITEMS.register(
        "enhancement_rune_yotor_acolyte", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_yotor"), 1, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_YOTOR_ADEPT = ITEMS.register(
        "enhancement_rune_yotor_adept", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_yotor"), 2, new Item.Properties()));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_YOTOR_ARCH = ITEMS.register(
        "enhancement_rune_yotor_arch", () -> new EnhancementRuneItem(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "class_yotor"), 3, new Item.Properties()));

    private ModItems() {}
}
