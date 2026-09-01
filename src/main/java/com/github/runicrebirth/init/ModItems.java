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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RunicRebirth.MODID);

    public static final DeferredItem<AcolyteWandItem> ACOLYTE_WAND = ITEMS.registerItem(
        "acolyte_wand", props -> new AcolyteWandItem(props
            .component(ModDataComponents.WAND_STACKS.get(), com.github.runicrebirth.api.spells.WandStacksData.createDefault(2))
            .component(ModDataComponents.MAX_INSCRIPTIONS.get(), 2)
            .component(ModDataComponents.MAX_MODIFIER_SLOTS.get(), 1)
            .component(ModDataComponents.INITIAL_CHARGES.get(), 2)), ItemPropertiesHelper::wand);

    public static final DeferredItem<AdeptStaffItem> ADEPT_STAFF = ITEMS.registerItem(
        "adept_staff", props -> new AdeptStaffItem(props
            .component(ModDataComponents.WAND_STACKS.get(), com.github.runicrebirth.api.spells.WandStacksData.createDefault(2))
            .component(ModDataComponents.MAX_INSCRIPTIONS.get(), 2)
            .component(ModDataComponents.MAX_MODIFIER_SLOTS.get(), 3)
            .component(ModDataComponents.INITIAL_CHARGES.get(), 3)), ItemPropertiesHelper::wand);

public static final DeferredItem<RingOfExpansionItem> RING_OF_EXPANSION = ITEMS.registerItem(
        "ring_of_expansion", RingOfExpansionItem::new, () -> ItemPropertiesHelper.equipment().rarity(net.minecraft.world.item.Rarity.UNCOMMON));

    public static final DeferredItem<ArcaneAcolyteRingItem> ARCANE_ACOLYTE_RING = ITEMS.registerItem(
        "arcane_acolyte_ring", ArcaneAcolyteRingItem::new, () -> ItemPropertiesHelper.equipment());

    public static final DeferredItem<ArcaneTetherRingItem> ARCANE_TETHER_RING = ITEMS.registerItem(
        "arcane_tether_ring", ArcaneTetherRingItem::new, () -> ItemPropertiesHelper.equipment());

    public static final DeferredItem<MagicHandRingItem> MAGIC_HAND_RING = ITEMS.registerItem(
        "magic_hand_ring", MagicHandRingItem::new, () -> ItemPropertiesHelper.equipment());

    public static final DeferredItem<RingOfLeapingGalesItem> RING_OF_LEAPING_GALES = ITEMS.registerItem(
        "ring_of_leaping_gales", RingOfLeapingGalesItem::new, () -> ItemPropertiesHelper.equipment());

    public static final DeferredItem<RingOfPhantomMiningItem> RING_OF_PHANTOM_MINING = ITEMS.registerItem(
        "ring_of_phantom_mining", RingOfPhantomMiningItem::new, () -> ItemPropertiesHelper.equipment());

    public static final DeferredItem<BlinkRingItem> BLINK_RING = ITEMS.registerItem(
        "blink_ring", BlinkRingItem::new, () -> ItemPropertiesHelper.equipment());

    public static final DeferredItem<ThrusterRingItem> THRUSTER_RING = ITEMS.registerItem(
        "thruster_ring", ThrusterRingItem::new, () -> ItemPropertiesHelper.equipment());

    public static final DeferredItem<HoverRingItem> HOVER_RING = ITEMS.registerItem(
        "hover_ring", HoverRingItem::new, () -> ItemPropertiesHelper.equipment());

    public static final DeferredItem<ArcaneDroneItem> ARCANE_DRONE = ITEMS.registerItem(
        "arcane_drone", ArcaneDroneItem::new, () -> ItemPropertiesHelper.equipment());

    public static final DeferredItem<HammerDroneItem> HAMMER_DRONE = ITEMS.registerItem(
        "hammer_drone", HammerDroneItem::new, () -> ItemPropertiesHelper.equipment());

    public static final DeferredItem<RunicKeyRingItem> RUNIC_KEY_RING = ITEMS.registerItem(
        "runic_key_ring", RunicKeyRingItem::new, () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<RunicCodexItem> RUNIC_CODEX = ITEMS.registerItem(
        "runic_codex", RunicCodexItem::new, () -> new Item.Properties().stacksTo(1));

    private static final int ACOLYTE_DURABILITY_MULT = 5;

    public static final DeferredItem<DyeableAcolyteSetItem> ACOLYTE_WIZARD_HAT = ITEMS.registerItem(
        "acolyte_wizard_hat",
        props -> new DyeableAcolyteSetItem(props, "acolyte_wizard_hat", List.of(new AdditiveSizeModifier(2f))),
        () -> new Item.Properties().durability(ArmorType.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAcolyteSetItem> ACOLYTE_ROBES = ITEMS.registerItem(
        "acolyte_robes",
        props -> new DyeableAcolyteSetItem(props, "acolyte_set", List.of()),
        () -> new Item.Properties().durability(ArmorType.CHESTPLATE.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAcolyteSetItem> ACOLYTE_PANTS = ITEMS.registerItem(
        "acolyte_pants",
        props -> new DyeableAcolyteSetItem(props, "acolyte_set", List.of()),
        () -> new Item.Properties().durability(ArmorType.LEGGINGS.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAcolyteSetItem> ACOLYTE_BOOTS = ITEMS.registerItem(
        "acolyte_boots",
        props -> new DyeableAcolyteSetItem(props, "acolyte_set", List.of()),
        () -> new Item.Properties().durability(ArmorType.BOOTS.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_ARTIFICER_HEADGEAR = ITEMS.registerItem(
        "acolyte_artificer_headgear",
        props -> new AcolyteSetItem(props, "acolyte_artificer_headgear", List.of()),
        () -> new Item.Properties().durability(ArmorType.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_MAGE_HOOD = ITEMS.registerItem(
        "acolyte_mage_hood",
        props -> new AcolyteSetItem(props, "acolyte_mage_hood", List.of()),
        () -> new Item.Properties().durability(ArmorType.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_RUNEBLADE_HELMET = ITEMS.registerItem(
        "acolyte_runeblade_helmet",
        props -> new AcolyteSetItem(props, "acolyte_runeblade_helmet", List.of()),
        () -> new Item.Properties().durability(ArmorType.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    // --- Adept Tier Armor ---
    private static final int ADEPT_DURABILITY_MULT = 33;

    // Adept Runeblade Set
    public static final DeferredItem<AdeptSetItem> ADEPT_RUNEBLADE_HELMET = ITEMS.registerItem(
        "adept_runeblade_helmet",
        props -> new AdeptSetItem(props, "adept_runeblade_armor", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_RUNEBLADE_CHESTPLATE = ITEMS.registerItem(
        "adept_runeblade_chestplate",
        props -> new DyeableAdeptSetItem(props, "adept_runeblade_armor", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_RUNEBLADE_LEGGINGS = ITEMS.registerItem(
        "adept_runeblade_leggings",
        props -> new AdeptSetItem(props, "adept_runeblade_armor", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_RUNEBLADE_BOOTS = ITEMS.registerItem(
        "adept_runeblade_boots",
        props -> new AdeptSetItem(props, "adept_runeblade_armor", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Adept Wizard Set
    public static final DeferredItem<AdeptSetItem> ADEPT_WIZARD_HAT = ITEMS.registerItem(
        "adept_wizard_hat",
        props -> new AdeptSetItem(props, "adept_wizard_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_WIZARD_ROBES = ITEMS.registerItem(
        "adept_wizard_robes",
        props -> new AdeptSetItem(props, "adept_wizard_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_WIZARD_PANTS = ITEMS.registerItem(
        "adept_wizard_pants",
        props -> new DyeableAdeptSetItem(props, "adept_wizard_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_WIZARD_BOOTS = ITEMS.registerItem(
        "adept_wizard_boots",
        props -> new AdeptSetItem(props, "adept_wizard_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Adept Mage Set
    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_MAGE_HOOD = ITEMS.registerItem(
        "adept_mage_hood",
        props -> new DyeableAdeptSetItem(props, "adept_mage_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_MAGE_ROBES = ITEMS.registerItem(
        "adept_mage_robes",
        props -> new DyeableAdeptSetItem(props, "adept_mage_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_MAGE_PANTS = ITEMS.registerItem(
        "adept_mage_pants",
        props -> new DyeableAdeptSetItem(props, "adept_mage_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_MAGE_BOOTS = ITEMS.registerItem(
        "adept_mage_boots",
        props -> new DyeableAdeptSetItem(props, "adept_mage_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Adept Artificer Set
    public static final DeferredItem<AdeptSetItem> ADEPT_ARTIFICER_HEADGEAR = ITEMS.registerItem(
        "adept_artificer_headgear",
        props -> new AdeptSetItem(props, "adept_artificer_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_ARTIFICER_CHESTGEAR = ITEMS.registerItem(
        "adept_artificer_chestgear",
        props -> new DyeableAdeptSetItem(props, "adept_artificer_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<DyeableAdeptSetItem> ADEPT_ARTIFICER_PANTS = ITEMS.registerItem(
        "adept_artificer_pants",
        props -> new DyeableAdeptSetItem(props, "adept_artificer_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_ARTIFICER_BOOTS = ITEMS.registerItem(
        "adept_artificer_boots",
        props -> new AdeptSetItem(props, "adept_artificer_set", "adept_armor", List.of()),
        () -> new Item.Properties().durability(ArmorType.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Runic Circuits
    public static final DeferredItem<RunicCircuitItem> ACOLYTE_RUNIC_CIRCUIT = ITEMS.registerItem(
        "acolyte_runic_circuit", props -> new RunicCircuitItem(props.component(ModDataComponents.CIRCUIT_TIER.get(), 1), 1));
    public static final DeferredItem<RunicCircuitItem> ADEPT_RUNIC_CIRCUIT = ITEMS.registerItem(
        "adept_runic_circuit", props -> new RunicCircuitItem(props.component(ModDataComponents.CIRCUIT_TIER.get(), 2), 2));
    public static final DeferredItem<RunicCircuitItem> ARCH_RUNIC_CIRCUIT = ITEMS.registerItem(
        "arch_runic_circuit", props -> new RunicCircuitItem(props.component(ModDataComponents.CIRCUIT_TIER.get(), 3), 3));

    public static final DeferredItem<BasicRunicLongsword> BASIC_RUNIC_LONGSWORD = ITEMS.registerItem(
        "basic_runic_longsword", BasicRunicLongsword::new, () -> new Item.Properties());

    public static final DeferredItem<RunicDaggerItem> RUNIC_DAGGER = ITEMS.registerItem(
        "runic_dagger", RunicDaggerItem::new);

    public static final DeferredItem<RunicWarstaffItem> RUNIC_WARSTAFF = ITEMS.registerItem(
        "runic_warstaff", RunicWarstaffItem::new);

    public static final DeferredItem<RunicShieldItem> RUNIC_SHIELD = ITEMS.registerItem(
        "runic_shield", RunicShieldItem::new);

    // Dungeon items
    public static final DeferredItem<DungeonRoomConfiguratorItem> DUNGEON_ROOM_CONFIGURATOR =
        ITEMS.registerItem("dungeon_room_configurator", DungeonRoomConfiguratorItem::new, () -> new Item.Properties());

    public static final DeferredItem<Item> ARCANE_SPIRIT = ITEMS.registerSimpleItem("arcane_spirit", () -> new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> ARCANE_GEMSTONE = ITEMS.registerSimpleItem("arcane_gemstone", () -> new Item.Properties().stacksTo(64));

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
    public static final DeferredItem<RunicBlockItem> OCULUS_PORTAL = ITEMS.registerItem("oculus_portal",
            props -> new RunicBlockItem(ModBlocks.OCULUS_PORTAL.get(), props, "inactive", "oculus_portal")
                    .withHandRotationY(90));
    public static final DeferredItem<RunicBlockItem> OCULUS_CONTROLLER = ITEMS.registerItem("oculus_controller",
            props -> new RunicBlockItem(ModBlocks.OCULUS_CONTROLLER.get(), props, "idle", "oculus_controller"));
    public static final DeferredItem<RunicBlockItem> OCULUS_PILLAR = ITEMS.registerItem("oculus_pillar",
            props -> new RunicBlockItem(ModBlocks.OCULUS_PILLAR.get(), props, "idle", "oculus_pillar")
                .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                .withGuiTranslation(0f,-0.4f,0f));
    public static final DeferredItem<RunicBlockItem> RUNESTEEL_PYLON = ITEMS.registerItem("runesteel_pylon",
            props -> new RunicBlockItem(ModBlocks.RUNESTEEL_PYLON.get(), props, "idle", "runesteel_pylon")
                    .withGuiScale(1.5f).withGuiTranslation(0f, -0.4f, 0f));
    public static final DeferredItem<RunicBlockItem> INFUSION_ALTAR = ITEMS.registerItem("infusion_altar",
            props -> new RunicBlockItem(ModBlocks.INFUSION_ALTAR.get(), props, "idle", "infusion_altar")
                .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                .withGuiTranslation(0f, -0.4f, 0f)
                .withGuiScale(0.8f));
    public static final DeferredItem<RunicBlockItem> RUNIC_ANVIL = ITEMS.registerItem("runic_anvil",
            props -> new RunicBlockItem(ModBlocks.RUNIC_ANVIL.get(), props, "idle", "runic_anvil")
                    .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                    .withGuiTranslation(0f, -0.35f, 0f));

    public static final DeferredItem<RunicBlockItem> RUNESTEEL_CACHE = ITEMS.registerItem("runesteel_cache",
            props -> new RunicBlockItem(ModBlocks.RUNESTEEL_CACHE.get(), props, "idle", "runesteel_cache")
                    .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                    .withGuiTranslation(0f, -0.35f, 0f));
    public static final DeferredItem<BlockItem> RETURN_PORTAL = ITEMS.registerSimpleBlockItem(ModBlocks.RETURN_PORTAL);
    public static final DeferredItem<RunicBlockItem> DUNGEON_MOB_SPAWNER = ITEMS.registerItem("dungeon_mob_spawner",
        props -> new RunicBlockItem(ModBlocks.DUNGEON_MOB_SPAWNER.get(), props, "hold_spawning", "dungeon_mob_spawner")
            .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
            .withGuiTranslation(0f,-0.4f,0f)
            .withGuiScale(0.5f));
    public static final DeferredItem<BlockItem> DUNGEON_ROOM_TRACKER = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_ROOM_TRACKER);
    public static final DeferredItem<RunicBlockItem> RUNESTEEL_PORTCULLIS = ITEMS.registerItem("runesteel_portcullis",
            props -> new RunicBlockItem(ModBlocks.RUNESTEEL_PORTCULLIS.get(), props, "open_top", "runesteel_portcullis")
                .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                .withGuiTranslation(0f, -0.3f, 0f)
                .withHandRotationY(30));

    public static final DeferredItem<RunicBlockItem> DUNGEON_DOOR = ITEMS.registerItem("dungeon_door",
            props -> new RunicBlockItem(ModBlocks.DUNGEON_DOOR.get(), props, "idle", "dungeon_door")
                .withGuiScale(0.3f)
                .withGuiTranslation(0f, -0.35f, 0f));

    public static final DeferredItem<RunelightTorchItem> RUNELIGHT_TORCH = ITEMS.registerItem("runelight_torch",
            props -> new RunelightTorchItem(ModBlocks.RUNELIGHT_TORCH.get(), ModBlocks.RUNELIGHT_WALL_TORCH.get(), props));

    public static final DeferredItem<RunicBlockItem> RUNELIGHT_LANTERN = ITEMS.registerItem("runelight_lantern",
            props -> new RunicBlockItem(ModBlocks.RUNELIGHT_LANTERN.get(), props, "idle", "runelight_lantern")
                    .withTexture("textures/entity/runic_templates/arcane_runic_template.png"));

    public static final DeferredItem<RunicBlockItem> ANCIENT_ARCANE_TURRET = ITEMS.registerItem("ancient_arcane_turret",
      props -> new RunicBlockItem(ModBlocks.ANCIENT_ARCANE_TURRET.get(), props, "idle", "ancient_arcane_turret_floor")
          .withTexture("textures/entity/runic_templates/arcane_runic_template.png"));

    // Dungeon trap block items
    public static final DeferredItem<BlockItem> DUNGEON_TEMPORARY_PLATFORM = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_TEMPORARY_PLATFORM);
    public static final DeferredItem<BlockItem> DUNGEON_PRESSURE_PLATE = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> DUNGEON_SPIKE = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_SPIKE);
    public static final DeferredItem<BlockItem> CRUMBLING_PLATFORM = ITEMS.registerSimpleBlockItem(ModBlocks.CRUMBLING_PLATFORM);
    public static final DeferredItem<RunicBlockItem> DUNGEON_BOULDER_SPAWNER = ITEMS.registerItem("dungeon_boulder_spawner",
            props -> new RunicBlockItem(ModBlocks.DUNGEON_BOULDER_SPAWNER.get(), props, "idle", "dungeon_boulder_spawner")
                .withTexture("textures/entity/runic_templates/earth_runic_template.png")
                .withGuiScale(0.33f));
    public static final DeferredItem<RunicBlockItem> DUNGEON_SWINGING_AXE = ITEMS.registerItem("dungeon_swinging_axe",
            props -> new RunicBlockItem(ModBlocks.DUNGEON_SWINGING_AXE.get(), props, "swinging_axe", "dungeon_swinging_axe")
                .withTexture("textures/entity/runic_templates/earth_runic_template.png")
                .withGuiScale(0.3f)
                .withGuiTranslation(0f, 0.45f, 0f));
    public static final DeferredItem<BlockItem> DUNGEON_FLAMETHROWER = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_FLAMETHROWER);
    public static final DeferredItem<BlockItem> MEDITATION_CUSHION = ITEMS.registerSimpleBlockItem(ModBlocks.MEDITATION_CUSHION);
    public static final DeferredItem<RunicBlockItem> RUNIC_LEVER = ITEMS.registerItem("runic_lever",
            props -> new RunicBlockItem(ModBlocks.RUNIC_LEVER.get(), props, "off", "runic_lever")
                .withTexture("textures/entity/runic_templates/earth_runic_template.png")
                .withGuiTranslation(0f, -0.4f, 0f));

    // --- Sect Banner items ---
    public static final DeferredItem<SectBannerItem> SECT_BANNER = ITEMS.registerItem("sect_banner",
            props -> (SectBannerItem) new SectBannerItem(ModBlocks.SECT_BANNER.get(), props)
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f),
            () -> new Item.Properties().stacksTo(16));

    public static final DeferredItem<TatteredSectBannerItem> TATTERED_SECT_BANNER = ITEMS.registerItem("tattered_sect_banner",
            props -> (TatteredSectBannerItem) new TatteredSectBannerItem(ModBlocks.TATTERED_SECT_BANNER.get(), props)
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f),
            () -> new Item.Properties().stacksTo(16));

    public static final DeferredItem<SectBannerVariantItem> SECT_BANNER_MAGE = ITEMS.registerItem("sect_banner_mage",
            props -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.SECT_BANNER_MAGE.get(), props)
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f),
            () -> new Item.Properties().stacksTo(16));

    public static final DeferredItem<SectBannerVariantItem> SECT_BANNER_ARTIFICER = ITEMS.registerItem("sect_banner_artificer",
            props -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.SECT_BANNER_ARTIFICER.get(), props)
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f),
            () -> new Item.Properties().stacksTo(16));

    public static final DeferredItem<SectBannerVariantItem> SECT_BANNER_WIZARD = ITEMS.registerItem("sect_banner_wizard",
            props -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.SECT_BANNER_WIZARD.get(), props)
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f),
            () -> new Item.Properties().stacksTo(16));

    public static final DeferredItem<SectBannerVariantItem> SECT_BANNER_RUNEBLADE = ITEMS.registerItem("sect_banner_runeblade",
            props -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.SECT_BANNER_RUNEBLADE.get(), props)
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f),
            () -> new Item.Properties().stacksTo(16));

    public static final DeferredItem<SectBannerVariantItem> TATTERED_SECT_BANNER_MAGE = ITEMS.registerItem("tattered_sect_banner_mage",
            props -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.TATTERED_SECT_BANNER_MAGE.get(), props)
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f),
            () -> new Item.Properties().stacksTo(16));

    public static final DeferredItem<SectBannerVariantItem> TATTERED_SECT_BANNER_ARTIFICER = ITEMS.registerItem("tattered_sect_banner_artificer",
            props -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.TATTERED_SECT_BANNER_ARTIFICER.get(), props)
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f),
            () -> new Item.Properties().stacksTo(16));

    public static final DeferredItem<SectBannerVariantItem> TATTERED_SECT_BANNER_WIZARD = ITEMS.registerItem("tattered_sect_banner_wizard",
            props -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.TATTERED_SECT_BANNER_WIZARD.get(), props)
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f),
            () -> new Item.Properties().stacksTo(16));

    public static final DeferredItem<SectBannerVariantItem> TATTERED_SECT_BANNER_RUNEBLADE = ITEMS.registerItem("tattered_sect_banner_runeblade",
            props -> (SectBannerVariantItem) new SectBannerVariantItem(ModBlocks.TATTERED_SECT_BANNER_RUNEBLADE.get(), props)
                .withGuiScale(0.3f)
                .withGuiTranslation(0.0f,-0.5f,0.0f),
            () -> new Item.Properties().stacksTo(16));

    // --- Adept Set Statues ---
    public static final DeferredItem<RunicBlockItem> ADEPT_MAGE_STATUE = ITEMS.registerItem("adept_mage_statue",
            props -> new RunicBlockItem(ModBlocks.ADEPT_MAGE_STATUE.get(), props, "idle", "adept_mage_set_statue")
                    .withTexture("textures/block/adept_armor_statue_texture.png")
                    .withGuiScale(0.5f)
                    .withGuiTranslation(0.0f,-0.5f,0.0f));
    public static final DeferredItem<RunicBlockItem> ADEPT_WIZARD_STATUE = ITEMS.registerItem("adept_wizard_statue",
            props -> new RunicBlockItem(ModBlocks.ADEPT_WIZARD_STATUE.get(), props, "idle", "adept_wizard_set_statue")
                    .withTexture("textures/block/adept_armor_statue_texture.png")
                    .withGuiScale(0.5f)
                    .withGuiTranslation(0.0f,-0.5f,0.0f));
    public static final DeferredItem<RunicBlockItem> ADEPT_RUNEBLADE_STATUE = ITEMS.registerItem("adept_runeblade_statue",
            props -> new RunicBlockItem(ModBlocks.ADEPT_RUNEBLADE_STATUE.get(), props, "idle", "adept_runeblade_armor_statue")
                    .withTexture("textures/block/adept_armor_statue_texture.png")
                    .withGuiScale(0.5f)
                    .withGuiTranslation(0.0f,-0.5f,0.0f));
    public static final DeferredItem<RunicBlockItem> ADEPT_ARTIFICER_STATUE = ITEMS.registerItem("adept_artificer_statue",
            props -> new RunicBlockItem(ModBlocks.ADEPT_ARTIFICER_STATUE.get(), props, "idle", "adept_artificer_set_statue")
                    .withTexture("textures/block/adept_armor_statue_texture.png")
                    .withGuiScale(0.5f)
                    .withGuiTranslation(0.0f,-0.5f,0.0f));

    // Enhancement Runes - Ice
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ICE_ACOLYTE = ITEMS.registerItem(
        "enhancement_rune_ice_acolyte", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_ice"), 1, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ICE_ADEPT = ITEMS.registerItem(
        "enhancement_rune_ice_adept", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_ice"), 2, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ICE_ARCH = ITEMS.registerItem(
        "enhancement_rune_ice_arch", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_ice"), 3, props));

    // Enhancement Runes - Fire
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_FIRE_ACOLYTE = ITEMS.registerItem(
        "enhancement_rune_fire_acolyte", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_fire"), 1, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_FIRE_ADEPT = ITEMS.registerItem(
        "enhancement_rune_fire_adept", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_fire"), 2, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_FIRE_ARCH = ITEMS.registerItem(
        "enhancement_rune_fire_arch", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_fire"), 3, props));

    // Enhancement Runes - Earth
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_EARTH_ACOLYTE = ITEMS.registerItem(
        "enhancement_rune_earth_acolyte", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_earth"), 1, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_EARTH_ADEPT = ITEMS.registerItem(
        "enhancement_rune_earth_adept", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_earth"), 2, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_EARTH_ARCH = ITEMS.registerItem(
        "enhancement_rune_earth_arch", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_earth"), 3, props));

    // Enhancement Runes - Wind
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_WIND_ACOLYTE = ITEMS.registerItem(
        "enhancement_rune_wind_acolyte", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_wind"), 1, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_WIND_ADEPT = ITEMS.registerItem(
        "enhancement_rune_wind_adept", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_wind"), 2, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_WIND_ARCH = ITEMS.registerItem(
        "enhancement_rune_wind_arch", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_wind"), 3, props));

    // Enhancement Runes - Arcane
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANE_ACOLYTE = ITEMS.registerItem(
        "enhancement_rune_arcane_acolyte", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_arcane"), 1, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANE_ADEPT = ITEMS.registerItem(
        "enhancement_rune_arcane_adept", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_arcane"), 2, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANE_ARCH = ITEMS.registerItem(
        "enhancement_rune_arcane_arch", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "element_arcane"), 3, props));

    // Enhancement Runes - Arcanum (Wizard/Staff)
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANUM_ACOLYTE = ITEMS.registerItem(
        "enhancement_rune_arcanum_acolyte", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_arcanum"), 1, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANUM_ADEPT = ITEMS.registerItem(
        "enhancement_rune_arcanum_adept", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_arcanum"), 2, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ARCANUM_ARCH = ITEMS.registerItem(
        "enhancement_rune_arcanum_arch", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_arcanum"), 3, props));

    // Enhancement Runes - Order (Mage/Wand)
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ORDER_ACOLYTE = ITEMS.registerItem(
        "enhancement_rune_order_acolyte", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_order"), 1, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ORDER_ADEPT = ITEMS.registerItem(
        "enhancement_rune_order_adept", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_order"), 2, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_ORDER_ARCH = ITEMS.registerItem(
        "enhancement_rune_order_arch", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_order"), 3, props));

    // Enhancement Runes - Vigsalr (Runeblade/Weapon)
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_VIGSALR_ACOLYTE = ITEMS.registerItem(
        "enhancement_rune_vigsalr_acolyte", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_vigsalr"), 1, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_VIGSALR_ADEPT = ITEMS.registerItem(
        "enhancement_rune_vigsalr_adept", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_vigsalr"), 2, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_VIGSALR_ARCH = ITEMS.registerItem(
        "enhancement_rune_vigsalr_arch", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_vigsalr"), 3, props));

    // Enhancement Runes - Yotor (Artificer/Drone)
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_YOTOR_ACOLYTE = ITEMS.registerItem(
        "enhancement_rune_yotor_acolyte", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_yotor"), 1, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_YOTOR_ADEPT = ITEMS.registerItem(
        "enhancement_rune_yotor_adept", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_yotor"), 2, props));
    public static final DeferredItem<EnhancementRuneItem> ENHANCEMENT_RUNE_YOTOR_ARCH = ITEMS.registerItem(
        "enhancement_rune_yotor_arch", props -> new EnhancementRuneItem(
            net.minecraft.resources.Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "class_yotor"), 3, props));

    private ModItems() {}
}
