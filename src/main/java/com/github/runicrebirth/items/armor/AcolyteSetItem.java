package com.github.runicrebirth.items.armor;

import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.client.renderers.armor.DyeableMagicArmorRenderer;
import com.github.runicrebirth.client.renderers.armor.MagicArmorRenderer;
import com.github.runicrebirth.client.renderers.models.MagicArmorGeoModel;
import com.github.runicrebirth.init.ModArmorMaterials;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public class AcolyteSetItem extends MagicArmorItem {

    public static final int DEFAULT_DYE_COLOR = 0xFF8B6BB0;

    private final boolean dyeable;

    public AcolyteSetItem(Type type, Properties props, String armorName, boolean dyeable, List<SpellModifier> modifiers) {
        super(ModArmorMaterials.ACOLYTE, type, props, armorName, armorName, 0f, 0f, 0f, modifiers);
        this.dyeable = dyeable;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected GeoArmorRenderer<?> supplyRenderer() {
        if (dyeable) {
            return new DyeableMagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName, textureName), DEFAULT_DYE_COLOR);
        }
        return new MagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName, textureName));
    }
}
