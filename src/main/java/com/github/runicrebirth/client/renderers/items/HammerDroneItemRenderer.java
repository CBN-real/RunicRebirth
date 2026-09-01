package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.items.curios.HammerDroneItem;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoItemRenderer;

public class HammerDroneItemRenderer extends GeoItemRenderer<HammerDroneItem> {

    public HammerDroneItemRenderer() {
        super(new HammerDroneItemModel());
    }

    @Override
    public RenderType getRenderType(com.geckolib.renderer.base.GeoRenderState renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
}
