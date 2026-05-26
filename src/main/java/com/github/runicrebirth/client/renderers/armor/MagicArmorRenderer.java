package com.github.runicrebirth.client.renderers.armor;

import com.github.runicrebirth.items.armor.MagicArmorItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@OnlyIn(Dist.CLIENT)
public class MagicArmorRenderer<T extends MagicArmorItem> extends GeoArmorRenderer<T> {

    public MagicArmorRenderer(GeoModel<T> model) {
        super(model);
    }


}
