package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.items.RunicWarstaffItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RunicWarstaffRenderer extends GeoItemRenderer<RunicWarstaffItem> {
    public RunicWarstaffRenderer() { super(new RunicWarstaffModel()); }

    @Override
    public RenderType getRenderType(RunicWarstaffItem animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
