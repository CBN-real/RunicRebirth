package com.github.runicrebirth.items.armor;

import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.client.renderers.armor.DyeableMagicArmorRenderer;
import com.github.runicrebirth.client.renderers.models.MagicArmorGeoModel;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import com.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

// props must include DataComponents.EQUIPPABLE (with slot), MAX_DAMAGE, ENCHANTABLE, ATTRIBUTE_MODIFIERS, REPAIRABLE
// TODO: ItemColor-based dyeable tinting is removed in 1.21.4 â€” update tinting approach if needed
public abstract class DyeableMagicArmorItem extends MagicArmorItem {

    private final int defaultDyeColor;

    public DyeableMagicArmorItem(Properties props,
                                  String armorName, String textureName,
                                  float magicRes, float bluntRes, float sharpRes,
                                  List<SpellModifier> modifiers, int defaultDyeColor) {
        super(props, armorName, textureName, magicRes, bluntRes, sharpRes, modifiers);
        this.defaultDyeColor = defaultDyeColor;
    }

    public int getDefaultDyeColor() {
        return defaultDyeColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltipAdder, tooltipFlag);
        if (!stack.has(DataComponents.DYED_COLOR)) {
            tooltipAdder.accept(Component.translatable("tooltip.runicrebirth.dyable").withStyle(ChatFormatting.GRAY));
        }
    }



  @Override
    protected GeoArmorRenderer<?, ?> supplyRenderer() {
        return new DyeableMagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName, textureName), defaultDyeColor);
    }
}
