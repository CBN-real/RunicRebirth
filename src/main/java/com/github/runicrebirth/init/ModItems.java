package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.BasicWandItem;
import com.github.runicrebirth.items.InscribedWandItem;
import com.github.runicrebirth.items.RunicBlockItem;
import com.github.runicrebirth.items.RunicCodexItem;
import com.github.runicrebirth.items.armor.AcolyteSetItem;
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

    public static final DeferredItem<BasicWandItem> BASIC_WAND = ITEMS.registerItem(
        "basic_wand", BasicWandItem::new, ItemPropertiesHelper.wand());

    public static final DeferredItem<InscribedWandItem> INSCRIBED_WAND = ITEMS.registerItem(
        "inscribed_wand", InscribedWandItem::new, ItemPropertiesHelper.wand());

    public static final DeferredItem<RingOfExpansionItem> RING_OF_EXPANSION = ITEMS.registerItem(
        "ring_of_expansion", RingOfExpansionItem::new, ItemPropertiesHelper.equipment().rarity(net.minecraft.world.item.Rarity.UNCOMMON));

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
            () -> new RunicBlockItem(ModBlocks.RUNESTEEL_PYLON.get(), new Item.Properties(), "idle", "runesteel_pylon"));
    public static final DeferredItem<RunicBlockItem> INFUSION_ALTAR = ITEMS.register("infusion_altar",
            () -> new RunicBlockItem(ModBlocks.INFUSION_ALTAR.get(), new Item.Properties(), "idle", "infusion_altar")
                    .withTexture("textures/entity/runic_templates/arcane_runic_template.png"));
    public static final DeferredItem<BlockItem> RETURN_PORTAL = ITEMS.registerSimpleBlockItem(ModBlocks.RETURN_PORTAL);
    public static final DeferredItem<BlockItem> TRIAL_SPAWNER = ITEMS.registerSimpleBlockItem(ModBlocks.TRIAL_SPAWNER);

    private ModItems() {}
}
