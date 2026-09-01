package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.items.RunelightTorchItem;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class RunelightTorchItemRenderer extends GeoItemRenderer<RunelightTorchItem> {

    public RunelightTorchItemRenderer(GeoModel<RunelightTorchItem> model) {
        super(model);
    }

    @Override
    public @Nullable RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
}
