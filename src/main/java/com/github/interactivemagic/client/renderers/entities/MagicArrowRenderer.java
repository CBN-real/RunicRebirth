package com.github.interactivemagic.client.renderers.entities;

import com.github.interactivemagic.client.renderers.ModRenderTypes;
import com.github.interactivemagic.client.renderers.models.MagicArrowGeoModel;
import com.github.interactivemagic.entities.spells.MagicArrowEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class MagicArrowRenderer extends GeoEntityRenderer<MagicArrowEntity> {

    public MagicArrowRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicArrowGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(MagicArrowEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(texture);
    }

    @Override
    protected void applyRotations(MagicArrowEntity entity, PoseStack poseStack,
        float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float xRot = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(180-xRot));
    }

    @Override
    public void preRender(PoseStack poseStack, MagicArrowEntity entity, BakedGeoModel model,
        @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay,
        int colour) {
        float scale = entity.getProjectileSize();
        poseStack.scale(scale, scale, scale);
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
            partialTick, packedLight, packedOverlay, colour);
    }

}
