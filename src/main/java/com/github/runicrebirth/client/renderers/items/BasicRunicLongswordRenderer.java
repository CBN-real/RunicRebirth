package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.items.BasicRunicLongsword;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoItemRenderer;

public class BasicRunicLongswordRenderer extends GeoItemRenderer<BasicRunicLongsword> {

    public BasicRunicLongswordRenderer() {
        super(new BasicRunicLongswordModel());
    }

    @Override
    public RenderType getRenderType(com.geckolib.renderer.base.GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
