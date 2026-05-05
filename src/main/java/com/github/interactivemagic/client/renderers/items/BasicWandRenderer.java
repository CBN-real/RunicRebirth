package com.github.interactivemagic.client.renderers.items;

import com.github.interactivemagic.items.BasicWandItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BasicWandRenderer extends GeoItemRenderer<BasicWandItem> {

    public BasicWandRenderer() {
        super(new BasicWandModel());
    }

    @Override
    public RenderType getRenderType(BasicWandItem animatable, ResourceLocation texture,
                                     @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.MultiBufferSource bufferSource,
                                     float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
