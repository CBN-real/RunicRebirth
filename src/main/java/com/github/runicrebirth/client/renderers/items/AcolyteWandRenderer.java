package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.items.AcolyteWandItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AcolyteWandRenderer extends GeoItemRenderer<AcolyteWandItem> {

    public AcolyteWandRenderer() {
        super(new AcolyteWandModel());
    }

    @Override
    public RenderType getRenderType(AcolyteWandItem animatable, ResourceLocation texture,
                                     @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.MultiBufferSource bufferSource,
                                     float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

}
