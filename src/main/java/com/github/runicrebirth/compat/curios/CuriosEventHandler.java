package com.github.runicrebirth.compat.curios;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import top.theillusivec4.curios.api.SlotAttribute;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public final class CuriosEventHandler {

    private static final ResourceLocation SPELL_RING_MODIFIER_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "acolyte_artificer_headgear_spell_ring");

    private CuriosEventHandler() {}

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        if (event.getItemStack().getItem() != ModItems.ACOLYTE_ARTIFICER_HEADGEAR.get()) return;
        event.addModifier(
            SlotAttribute.getOrCreate("spell_ring"),
            new AttributeModifier(SPELL_RING_MODIFIER_ID, 1, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.HEAD
        );
    }
}
