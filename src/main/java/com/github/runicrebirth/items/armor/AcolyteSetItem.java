package com.github.runicrebirth.items.armor;

import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.client.renderers.armor.MagicArmorRenderer;
import com.github.runicrebirth.client.renderers.models.MagicArmorGeoModel;
import com.github.runicrebirth.init.ModArmorMaterials;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public class AcolyteSetItem extends MagicArmorItem {

    public AcolyteSetItem(Type type, Properties props, String armorName, List<SpellModifier> modifiers) {
        super(ModArmorMaterials.ACOLYTE, type, props, armorName, armorName, 0f, 0f, 0f, modifiers);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected GeoArmorRenderer<?> supplyRenderer() {
        return new MagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName, textureName));
    }
}
