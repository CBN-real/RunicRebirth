package com.github.interactivemagic.items.armor;

import com.github.interactivemagic.api.spells.SpellModifier;
import com.github.interactivemagic.client.renderers.armor.DyeableMagicArmorRenderer;
import com.github.interactivemagic.client.renderers.models.MagicArmorGeoModel;
import com.github.interactivemagic.init.ModArmorMaterials;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public class ApprenticeSetItem extends MagicArmorItem {

    public static final int DEFAULT_DYE_COLOR = 0x8B6BB0;

    public ApprenticeSetItem(Type type, Properties props, List<SpellModifier> modifiers) {
        super(ModArmorMaterials.APPRENTICE, type, props,
                "apprentice_set", 0f, 0f, 0f, modifiers);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected GeoArmorRenderer<?> supplyRenderer() {
        return new DyeableMagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName), DEFAULT_DYE_COLOR);
    }
}
