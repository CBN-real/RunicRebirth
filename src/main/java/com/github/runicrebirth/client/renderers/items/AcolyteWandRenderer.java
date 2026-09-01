package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.items.AcolyteWandItem;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoItemRenderer;

public class AcolyteWandRenderer extends GeoItemRenderer<AcolyteWandItem> {

    public AcolyteWandRenderer() {
        super(new AcolyteWandModel());
    }

    @Override
    public RenderType getRenderType(com.geckolib.renderer.base.GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

}
