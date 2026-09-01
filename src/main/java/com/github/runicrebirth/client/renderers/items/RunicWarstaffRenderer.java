package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.items.RunicWarstaffItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoItemRenderer;

public class RunicWarstaffRenderer extends GeoItemRenderer<RunicWarstaffItem> {
    public RunicWarstaffRenderer() { super(new RunicWarstaffModel()); }

    @Override
    public RenderType getRenderType(com.geckolib.renderer.base.GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
