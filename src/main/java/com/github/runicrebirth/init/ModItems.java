package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.BasicWandItem;
import com.github.runicrebirth.items.InscribedWandItem;
import com.github.runicrebirth.items.armor.ApprenticeSetItem;
import com.github.runicrebirth.items.curios.RingOfExpansionItem;
import com.github.runicrebirth.spells.modifiers.AdditiveSizeModifier;
import com.github.runicrebirth.util.ItemPropertiesHelper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
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

    private static final int APPRENTICE_DURABILITY_MULT = 5;

    public static final DeferredItem<ApprenticeSetItem> APPRENTICE_SET_HELMET = ITEMS.registerItem(
        "apprentice_set_helmet",
        props -> new ApprenticeSetItem(ArmorItem.Type.HELMET, props, List.of(new AdditiveSizeModifier(2))),
        new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(APPRENTICE_DURABILITY_MULT)));

    public static final DeferredItem<ApprenticeSetItem> APPRENTICE_SET_CHESTPLATE = ITEMS.registerItem(
        "apprentice_set_chestplate",
        props -> new ApprenticeSetItem(ArmorItem.Type.CHESTPLATE, props, List.of()),
        new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(APPRENTICE_DURABILITY_MULT)));

    public static final DeferredItem<ApprenticeSetItem> APPRENTICE_SET_LEGGINGS = ITEMS.registerItem(
        "apprentice_set_leggings",
        props -> new ApprenticeSetItem(ArmorItem.Type.LEGGINGS, props, List.of()),
        new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(APPRENTICE_DURABILITY_MULT)));

    public static final DeferredItem<ApprenticeSetItem> APPRENTICE_SET_BOOTS = ITEMS.registerItem(
        "apprentice_set_boots",
        props -> new ApprenticeSetItem(ArmorItem.Type.BOOTS, props, List.of()),
        new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(APPRENTICE_DURABILITY_MULT)));

    private ModItems() {}
}
