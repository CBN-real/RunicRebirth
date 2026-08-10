package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.items.RunelightTorchItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

@OnlyIn(Dist.CLIENT)
public class RunelightTorchItemRenderer extends GeoItemRenderer<RunelightTorchItem> {

    public RunelightTorchItemRenderer(GeoModel<RunelightTorchItem> model) {
        super(model);
    }

    @Override
    public RenderType getRenderType(RunelightTorchItem animatable, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
}
