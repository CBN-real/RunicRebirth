package com.github.runicrebirth.items.armor;

import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.client.renderers.armor.DyeableMagicArmorRenderer;
import com.github.runicrebirth.client.renderers.models.MagicArmorGeoModel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public abstract class DyeableMagicArmorItem extends MagicArmorItem {

    private final int defaultDyeColor;

    public DyeableMagicArmorItem(Holder<ArmorMaterial> material, Type type, Properties props,
                                  String armorName, String textureName,
                                  float magicRes, float bluntRes, float sharpRes,
                                  List<SpellModifier> modifiers, int defaultDyeColor) {
        super(material, type, props, armorName, textureName, magicRes, bluntRes, sharpRes, modifiers);
        this.defaultDyeColor = defaultDyeColor;
    }

    public int getDefaultDyeColor() {
        return defaultDyeColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (!stack.has(DataComponents.DYED_COLOR)) {
            tooltipComponents.add(Component.translatable("tooltip.runicrebirth.dyable").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected GeoArmorRenderer<?> supplyRenderer() {
        return new DyeableMagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName, textureName), defaultDyeColor);
    }
}
