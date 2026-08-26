package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.MagicMeteorGeoModel;
import com.github.runicrebirth.entities.spells.MagicMeteorCircleEntity;
import com.github.runicrebirth.entities.spells.MagicMeteorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

@OnlyIn(Dist.CLIENT)
public class MagicMeteorRenderer extends AbstractSpellRenderer<MagicMeteorEntity> {

    public MagicMeteorRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicMeteorGeoModel());
    }

    @Override
    public RenderType getRenderType(MagicMeteorEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoDepth(texture);
    }

  @Override
  protected void applyRotations(MagicMeteorEntity entity, PoseStack poseStack, float ageInTicks,
      float rotationYaw, float partialTick, float nativeScale) {
      float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
      float xRot = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(38f + xRot));
  }
}
