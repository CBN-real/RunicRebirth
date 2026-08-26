package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicSlashCircleGeoModel;
import com.github.runicrebirth.entities.spells.MagicSlashCircleEntity;
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

@OnlyIn(Dist.CLIENT)
public class MagicSlashCircleRenderer extends AbstractSpellRenderer<MagicSlashCircleEntity> {

    public MagicSlashCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicSlashCircleGeoModel());
    }

    @Override
    public RenderType getRenderType(MagicSlashCircleEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    protected void applyRotations(MagicSlashCircleEntity entity, PoseStack poseStack,
        float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float xRot = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));
    }

    @Override
    public void preRender(PoseStack poseStack, MagicSlashCircleEntity entity, BakedGeoModel model,
        @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
        float partialTick, int packedLight, int packedOverlay, int colour) {
      float scale = entity.getCircleScale();
      poseStack.scale(scale, scale, scale);
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick,
          packedLight, packedOverlay, colour);
    }
}
