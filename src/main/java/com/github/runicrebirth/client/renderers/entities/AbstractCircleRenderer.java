package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.NormalOverrideVertexConsumer;
import com.github.runicrebirth.entities.spells.AbstractCircleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractCircleRenderer<T extends AbstractCircleEntity> extends GeoEntityRenderer<T> {

    protected AbstractCircleRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(T entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone,
        RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        ResourceLocation texture = getTextureLocation(animatable);

        if (bone.getName().startsWith("rune_")) {
            RenderType runeType = RenderType.entityTranslucent(texture);
            VertexConsumer runeBuffer = bufferSource.getBuffer(runeType);
            super.renderRecursively(poseStack, animatable, bone, runeType, bufferSource, runeBuffer,
                isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
            return;
        }

        RenderType cutType = ModRenderTypes.entityTranslucentNoCullNoShade(texture);
        VertexConsumer cutBuffer = new NormalOverrideVertexConsumer(bufferSource.getBuffer(cutType));
        super.renderRecursively(poseStack, animatable, bone, cutType, bufferSource, cutBuffer,
            isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
    }

    @Override
    protected void applyRotations(T entity, PoseStack poseStack,
        float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float xRot = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));
    }
}
