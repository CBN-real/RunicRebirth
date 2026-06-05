package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.NormalOverrideVertexConsumer;
import com.github.runicrebirth.items.RunicBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

@OnlyIn(Dist.CLIENT)
public class RunicBlockItemRenderer extends GeoItemRenderer<RunicBlockItem> {

    public RunicBlockItemRenderer(GeoModel<RunicBlockItem> model) {
        super(model);
    }

    @Override
    public void preRender(PoseStack poseStack, RunicBlockItem animatable, BakedGeoModel model,
        @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer,
            isReRender, partialTick, packedLight, packedOverlay, colour);
        if (!isReRender && animatable.getHandRotationY() != 0 && isHandContext()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(animatable.getHandRotationY()));
        }
    }

    private boolean isHandContext() {
        return this.renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            || this.renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
            || this.renderPerspective == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            || this.renderPerspective == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }

    @Override
    public RenderType getRenderType(RunicBlockItem animatable, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, RunicBlockItem animatable, GeoBone bone,
        RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        ResourceLocation texture = getTextureLocation(animatable);

        RenderType noShadeType = ModRenderTypes.entityTranslucentNoCullNoShade(texture);
        VertexConsumer finalBuffer;
        if (this.renderPerspective == ItemDisplayContext.GUI) {
            noShadeType = ModRenderTypes.entityUnlit(texture);
            finalBuffer = bufferSource.getBuffer(noShadeType);
        } else {
            finalBuffer = new NormalOverrideVertexConsumer(bufferSource.getBuffer(noShadeType));
        }
        super.renderRecursively(poseStack, animatable, bone, noShadeType, bufferSource, finalBuffer,
            isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
    }
}
