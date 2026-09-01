package com.github.runicrebirth.client.renderers.armor;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.items.armor.MagicArmorItem;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicArmorRenderer<T extends MagicArmorItem, R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<T, R> {

    public MagicArmorRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
}
