package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.BookDisplayState;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.NormalOverrideVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSpellRenderer<T extends Entity & GeoEntity> extends GeoEntityRenderer<T> {

    protected AbstractSpellRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        this.shadowRadius = 0f;
    }

    @Override
    public void preRender(PoseStack poseStack, T entity, BakedGeoModel model,
        @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay,
        int colour) {
        if (!entity.isAddedToLevel()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(30));
            float ox = BookDisplayState.getOffsetX();
            float oy = BookDisplayState.getOffsetY();
            float oz = BookDisplayState.getOffsetZ();
            if (ox != 0f || oy != 0f || oz != 0f) {
                poseStack.translate(ox, oy, oz);
            }
        }
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
            partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public RenderType getRenderType(T entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
      if (!entity.isAddedToLevel()) {
        return ModRenderTypes.entityUnlit(texture);
      }
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

  @Override
  public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone,
      RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
      boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

    ResourceLocation texture = getTextureLocation(animatable);
    String name = bone.getName();
    boolean inBook = !animatable.isAddedToLevel();

    if (inBook) {
      RenderType unlitType = ModRenderTypes.entityUnlit(texture);
      VertexConsumer unlitBuffer = bufferSource.getBuffer(unlitType);
      super.renderRecursively(poseStack, animatable, bone, unlitType, bufferSource, unlitBuffer,
          isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
      return;
    }

    if (name.endsWith("_es")) {
      RenderType swirlType = RenderType.energySwirl(texture, 0, 0);
      VertexConsumer swirlBuffer = bufferSource.getBuffer(swirlType);
      super.renderRecursively(poseStack, animatable, bone, swirlType, bufferSource, swirlBuffer,
          isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
      return;
    }
    if (name.endsWith("_tr")) {
      RenderType trType = RenderType.entityTranslucent(texture);
      VertexConsumer trBuffer = bufferSource.getBuffer(trType);
      super.renderRecursively(poseStack, animatable, bone, trType, bufferSource, trBuffer,
          isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
      return;
    }

    {
      RenderType cutType = ModRenderTypes.entityTranslucentNoCullNoShade(texture);
      VertexConsumer cutBuffer = new NormalOverrideVertexConsumer(bufferSource.getBuffer(cutType));
      super.renderRecursively(poseStack, animatable, bone, cutType, bufferSource, cutBuffer,
          isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
    }
  }
}
