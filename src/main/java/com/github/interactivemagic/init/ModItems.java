package com.github.interactivemagic.init;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.items.BasicWandItem;
import com.github.interactivemagic.items.InscribedWandItem;
import com.github.interactivemagic.items.curios.RingOfExpansionItem;
import com.github.interactivemagic.util.ItemPropertiesHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(InteractiveMagic.MODID);

    public static final DeferredItem<BasicWandItem> BASIC_WAND = ITEMS.registerItem(
        "basic_wand", BasicWandItem::new, ItemPropertiesHelper.wand());

    public static final DeferredItem<InscribedWandItem> INSCRIBED_WAND = ITEMS.registerItem(
        "inscribed_wand", InscribedWandItem::new, ItemPropertiesHelper.wand());

    public static final DeferredItem<RingOfExpansionItem> RING_OF_EXPANSION = ITEMS.registerItem(
        "ring_of_expansion", RingOfExpansionItem::new, ItemPropertiesHelper.equipment().rarity(net.minecraft.world.item.Rarity.UNCOMMON));


    private ModItems() {}
}
