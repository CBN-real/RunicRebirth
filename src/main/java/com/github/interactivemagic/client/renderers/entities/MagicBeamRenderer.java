package com.github.interactivemagic.client.renderers.entities;

import com.github.interactivemagic.client.renderers.models.MagicBeamGeoModel;
import com.github.interactivemagic.entities.spells.MagicBeamEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class MagicBeamRenderer extends GeoEntityRenderer<MagicBeamEntity> {

    public MagicBeamRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicBeamGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public boolean shouldRender(MagicBeamEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public RenderType getRenderType(MagicBeamEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, MagicBeamEntity entity, BakedGeoModel model,
        @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(180+pitch));

        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
            partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, MagicBeamEntity animatable, GeoBone bone,
        RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if ("inner".equals(bone.getName())) {
            ResourceLocation texture = getTextureLocation(animatable);
            RenderType swirlType = RenderType.energySwirl(texture, 0, 0);
            VertexConsumer swirlBuffer = bufferSource.getBuffer(swirlType);
            super.renderRecursively(poseStack, animatable, bone, swirlType, bufferSource, swirlBuffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);
            return;
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
            isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, MagicBeamEntity entity, BakedGeoModel model,
        @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        float distance = entity.getBeamDistance();
        if (distance <= 0) return;

        for (float d = 0; d < distance; d += 1.0f) {
            poseStack.pushPose();
            poseStack.translate(0, 0, -d);
            super.actuallyRender(poseStack, entity, model, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);
            poseStack.popPose();
        }
    }

}
