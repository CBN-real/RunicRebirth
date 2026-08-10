package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.items.BasicRunicLongsword;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BasicRunicLongswordRenderer extends GeoItemRenderer<BasicRunicLongsword> {

    public BasicRunicLongswordRenderer() {
        super(new BasicRunicLongswordModel());
    }

    @Override
    public RenderType getRenderType(BasicRunicLongsword animatable, ResourceLocation texture,
                                    @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.MultiBufferSource bufferSource,
                                    float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
