package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.items.RunicShieldItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoItemRenderer;

public class RunicShieldRenderer extends GeoItemRenderer<RunicShieldItem> {
    public RunicShieldRenderer() { super(new RunicShieldModel()); }

    @Override
    public RenderType getRenderType(com.geckolib.renderer.base.GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
