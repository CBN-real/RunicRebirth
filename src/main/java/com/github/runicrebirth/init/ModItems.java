package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.AdeptStaffItem;
import com.github.runicrebirth.items.AcolyteWandItem;
import com.github.runicrebirth.items.InscribedWandItem;
import com.github.runicrebirth.items.RunicBlockItem;
import com.github.runicrebirth.items.RunicCircuitItem;
import com.github.runicrebirth.items.RunicCodexItem;
import com.github.runicrebirth.items.armor.AcolyteSetItem;
import com.github.runicrebirth.items.armor.AdeptSetItem;
import com.github.runicrebirth.items.curios.AcolyteArcaneRingItem;
import com.github.runicrebirth.items.curios.RingOfExpansionItem;
import com.github.runicrebirth.spells.modifiers.AdditiveSizeModifier;
import com.github.runicrebirth.util.ItemPropertiesHelper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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

    public static final DeferredItem<InscribedWandItem> INSCRIBED_WAND = ITEMS.register(
        "inscribed_wand", () -> new InscribedWandItem(ItemPropertiesHelper.wand()));

    public static final DeferredItem<RingOfExpansionItem> RING_OF_EXPANSION = ITEMS.registerItem(
        "ring_of_expansion", RingOfExpansionItem::new, ItemPropertiesHelper.equipment().rarity(net.minecraft.world.item.Rarity.UNCOMMON));

    public static final DeferredItem<AcolyteArcaneRingItem> ACOLYTE_ARCANE_RING = ITEMS.registerItem(
        "acolyte_arcane_ring", AcolyteArcaneRingItem::new, ItemPropertiesHelper.equipment());

    public static final DeferredItem<RunicCodexItem> RUNIC_CODEX = ITEMS.registerItem(
        "runic_codex", RunicCodexItem::new, new Item.Properties().stacksTo(1));

    private static final int ACOLYTE_DURABILITY_MULT = 5;

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_WIZARD_HAT = ITEMS.registerItem(
        "acolyte_wizard_hat",
        props -> new AcolyteSetItem(ArmorItem.Type.HELMET, props, "acolyte_wizard_hat", true, List.of(new AdditiveSizeModifier(2f))),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_ROBES = ITEMS.registerItem(
        "acolyte_robes",
        props -> new AcolyteSetItem(ArmorItem.Type.CHESTPLATE, props, "acolyte_set", true, List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_PANTS = ITEMS.registerItem(
        "acolyte_pants",
        props -> new AcolyteSetItem(ArmorItem.Type.LEGGINGS, props, "acolyte_set", true, List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_BOOTS = ITEMS.registerItem(
        "acolyte_boots",
        props -> new AcolyteSetItem(ArmorItem.Type.BOOTS, props, "acolyte_set", true, List.of()),
        new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_ARTIFICER_HEADGEAR = ITEMS.registerItem(
        "acolyte_artificer_headgear",
        props -> new AcolyteSetItem(ArmorItem.Type.HELMET, props, "acolyte_artificer_headgear", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_MAGE_HOOD = ITEMS.registerItem(
        "acolyte_mage_hood",
        props -> new AcolyteSetItem(ArmorItem.Type.HELMET, props, "acolyte_mage_hood", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    public static final DeferredItem<AcolyteSetItem> ACOLYTE_RUNEBLADE_HELMET = ITEMS.registerItem(
        "acolyte_runeblade_helmet",
        props -> new AcolyteSetItem(ArmorItem.Type.HELMET, props, "acolyte_runeblade_helmet", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ACOLYTE_DURABILITY_MULT)));

    // --- Adept Tier Armor ---
    private static final int ADEPT_DURABILITY_MULT = 33;

    // Adept Runeblade Set
    public static final DeferredItem<AdeptSetItem> ADEPT_RUNEBLADE_HELMET = ITEMS.registerItem(
        "adept_runeblade_helmet",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT_RUNEBLADE, ArmorItem.Type.HELMET, props, "adept_runeblade_armor", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_RUNEBLADE_CHESTPLATE = ITEMS.registerItem(
        "adept_runeblade_chestplate",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT_RUNEBLADE, ArmorItem.Type.CHESTPLATE, props, "adept_runeblade_armor", "adept_armor", true, List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_RUNEBLADE_LEGGINGS = ITEMS.registerItem(
        "adept_runeblade_leggings",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT_RUNEBLADE, ArmorItem.Type.LEGGINGS, props, "adept_runeblade_armor", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_RUNEBLADE_BOOTS = ITEMS.registerItem(
        "adept_runeblade_boots",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT_RUNEBLADE, ArmorItem.Type.BOOTS, props, "adept_runeblade_armor", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Adept Wizard Set
    public static final DeferredItem<AdeptSetItem> ADEPT_WIZARD_HAT = ITEMS.registerItem(
        "adept_wizard_hat",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.HELMET, props, "adept_wizard_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_WIZARD_ROBES = ITEMS.registerItem(
        "adept_wizard_robes",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.CHESTPLATE, props, "adept_wizard_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_WIZARD_PANTS = ITEMS.registerItem(
        "adept_wizard_pants",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.LEGGINGS, props, "adept_wizard_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_WIZARD_BOOTS = ITEMS.registerItem(
        "adept_wizard_boots",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.BOOTS, props, "adept_wizard_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Adept Mage Set
    public static final DeferredItem<AdeptSetItem> ADEPT_MAGE_HOOD = ITEMS.registerItem(
        "adept_mage_hood",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.HELMET, props, "adept_mage_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_MAGE_ROBES = ITEMS.registerItem(
        "adept_mage_robes",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.CHESTPLATE, props, "adept_mage_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_MAGE_PANTS = ITEMS.registerItem(
        "adept_mage_pants",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.LEGGINGS, props, "adept_mage_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_MAGE_BOOTS = ITEMS.registerItem(
        "adept_mage_boots",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.BOOTS, props, "adept_mage_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(ADEPT_DURABILITY_MULT)));

    // Adept Artificer Set
    public static final DeferredItem<AdeptSetItem> ADEPT_ARTIFICER_HEADGEAR = ITEMS.registerItem(
        "adept_artificer_headgear",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.HELMET, props, "adept_artificer_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_ARTIFICER_CHESTGEAR = ITEMS.registerItem(
        "adept_artificer_chestgear",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.CHESTPLATE, props, "adept_artificer_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_ARTIFICER_PANTS = ITEMS.registerItem(
        "adept_artificer_pants",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.LEGGINGS, props, "adept_artificer_set", "adept_armor", false, List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ADEPT_DURABILITY_MULT)));

    public static final DeferredItem<AdeptSetItem> ADEPT_ARTIFICER_BOOTS = ITEMS.registerItem(
        "adept_artificer_boots",
        props -> new AdeptSetItem(ModArmorMaterials.ADEPT, ArmorItem.Type.BOOTS, props, "adept_artificer_set", "adept_armor", false, List.of()),
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

    // Dungeon items
    public static final DeferredItem<Item> ARCANE_SPIRIT = ITEMS.registerSimpleItem("arcane_spirit", new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> ARCANE_GEMSTONE = ITEMS.registerSimpleItem("arcane_gemstone", new Item.Properties().stacksTo(64));

    // Block items
    public static final DeferredItem<BlockItem> RUNIC_STONE = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE);
    public static final DeferredItem<BlockItem> RUNIC_STONE_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_SLAB);
    public static final DeferredItem<BlockItem> RUNIC_STONE_STAIRS = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_STAIRS);
    public static final DeferredItem<BlockItem> RUNIC_STONE_PILLAR = ITEMS.registerSimpleBlockItem(ModBlocks.RUNIC_STONE_PILLAR);
    public static final DeferredItem<RunicBlockItem> OCULUS_PORTAL = ITEMS.register("oculus_portal",
            () -> new RunicBlockItem(ModBlocks.OCULUS_PORTAL.get(), new Item.Properties(), "inactive", "oculus_portal").withHandRotationY(90));
    public static final DeferredItem<RunicBlockItem> OCULUS_CONTROLLER = ITEMS.register("oculus_controller",
            () -> new RunicBlockItem(ModBlocks.OCULUS_CONTROLLER.get(), new Item.Properties(), "idle", "oculus_controller"));
    public static final DeferredItem<RunicBlockItem> OCULUS_PILLAR = ITEMS.register("oculus_pillar",
            () -> new RunicBlockItem(ModBlocks.OCULUS_PILLAR.get(), new Item.Properties(), "idle", "oculus_pillar"));
    public static final DeferredItem<RunicBlockItem> RUNESTEEL_PYLON = ITEMS.register("runesteel_pylon",
            () -> new RunicBlockItem(ModBlocks.RUNESTEEL_PYLON.get(), new Item.Properties(), "idle", "runesteel_pylon")
                    .withGuiScale(1.5f).withGuiTranslation(0f, -0.15f, 0f));
    public static final DeferredItem<RunicBlockItem> INFUSION_ALTAR = ITEMS.register("infusion_altar",
            () -> new RunicBlockItem(ModBlocks.INFUSION_ALTAR.get(), new Item.Properties(), "idle", "infusion_altar")
                    .withTexture("textures/entity/runic_templates/arcane_runic_template.png"));
    public static final DeferredItem<RunicBlockItem> RUNIC_ANVIL = ITEMS.register("runic_anvil",
            () -> new RunicBlockItem(ModBlocks.RUNIC_ANVIL.get(), new Item.Properties(), "idle", "runic_anvil")
                    .withTexture("textures/entity/runic_templates/arcane_runic_template.png")
                    .withGuiTranslation(0f, -0.3f, 0f));
    public static final DeferredItem<BlockItem> RETURN_PORTAL = ITEMS.registerSimpleBlockItem(ModBlocks.RETURN_PORTAL);
    public static final DeferredItem<BlockItem> TRIAL_SPAWNER = ITEMS.registerSimpleBlockItem(ModBlocks.TRIAL_SPAWNER);

    private ModItems() {}
}
