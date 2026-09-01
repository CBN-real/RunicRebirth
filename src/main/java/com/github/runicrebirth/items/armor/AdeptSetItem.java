package com.github.runicrebirth.items.armor;

import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.client.renderers.armor.MagicArmorRenderer;
import com.github.runicrebirth.client.renderers.models.MagicArmorGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public class AdeptSetItem extends MagicArmorItem {

    public AdeptSetItem(Properties props, String armorName, String textureName, List<SpellModifier> modifiers) {
        super(props, armorName, textureName, 0f, 0f, 0f, modifiers);
    }

    @Override
    protected GeoArmorRenderer<?, ?> supplyRenderer() {
        return new MagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName, textureName));
    }
}
