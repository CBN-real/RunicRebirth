package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.items.RunicShieldItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RunicShieldRenderer extends GeoItemRenderer<RunicShieldItem> {
    public RunicShieldRenderer() { super(new RunicShieldModel()); }

    @Override
    public RenderType getRenderType(RunicShieldItem animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
