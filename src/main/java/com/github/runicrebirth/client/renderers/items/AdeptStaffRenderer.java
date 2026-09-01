package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.items.AdeptStaffItem;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoItemRenderer;

public class AdeptStaffRenderer extends GeoItemRenderer<AdeptStaffItem> {

    public AdeptStaffRenderer() {
        super(new AdeptStaffModel());
    }

    @Override
    public RenderType getRenderType(com.geckolib.renderer.base.GeoRenderState renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
}
