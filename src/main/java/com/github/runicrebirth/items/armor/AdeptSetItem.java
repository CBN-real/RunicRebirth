package com.github.runicrebirth.items.armor;

import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.client.renderers.armor.MagicArmorRenderer;
import com.github.runicrebirth.client.renderers.models.MagicArmorGeoModel;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public class AdeptSetItem extends MagicArmorItem {

    public AdeptSetItem(Holder<ArmorMaterial> material, Type type, Properties props,
                        String armorName, String textureName, List<SpellModifier> modifiers) {
        super(material, type, props, armorName, textureName, 0f, 0f, 0f, modifiers);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected GeoArmorRenderer<?> supplyRenderer() {
        return new MagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName, textureName));
    }
}
