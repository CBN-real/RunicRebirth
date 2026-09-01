package com.github.runicrebirth.items.armor;

import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.client.renderers.armor.MagicArmorRenderer;
import com.github.runicrebirth.client.renderers.models.MagicArmorGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

// props must include DataComponents.EQUIPPABLE (with slot), MAX_DAMAGE, ENCHANTABLE, ATTRIBUTE_MODIFIERS, REPAIRABLE
// TODO: Register ResourceKey<EquipmentAsset> for acolyte armor and pass via Equippable.builder(slot).setAsset(...).build()
public class AcolyteSetItem extends MagicArmorItem {

    public AcolyteSetItem(Properties props, String armorName, List<SpellModifier> modifiers) {
        super(props, armorName, armorName, 0f, 0f, 0f, modifiers);
    }

    @Override
    protected GeoArmorRenderer<?, ?> supplyRenderer() {
        return new MagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName, textureName));
    }
}
