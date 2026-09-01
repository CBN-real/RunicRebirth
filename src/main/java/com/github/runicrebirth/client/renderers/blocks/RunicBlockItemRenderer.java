package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.items.RunicBlockItem;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class RunicBlockItemRenderer extends GeoItemRenderer<RunicBlockItem> {
    // TODO GeckoLib 5: preRender() hand/GUI transform logic needs to move to addRenderData() or adjustRenderPose().
    // Original body preserved for reference:
    // @Override
    //     public void preRender(PoseStack poseStack, RunicBlockItem animatable, BakedGeoModel model,
    //         @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
    //         boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
    //         super.preRender(...);
    //         if (!isReRender && animatable.getHandRotationY() != 0 && isHandContext()) {
    //             poseStack.mulPose(Axis.YP.rotationDegrees(animatable.getHandRotationY()));
    //             ...
    //         }
    //     }

    public RunicBlockItemRenderer(GeoModel<RunicBlockItem> model) {
        super(model);
    }

    @Override
    public @Nullable RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }
}
