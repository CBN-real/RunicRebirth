package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.items.RunicDaggerItem;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoItemRenderer;

public class RunicDaggerRenderer extends GeoItemRenderer<RunicDaggerItem> {

    public RunicDaggerRenderer() { super(new RunicDaggerModel()); }

    @Override
    public RenderType getRenderType(com.geckolib.renderer.base.GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
