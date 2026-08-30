package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.DrawingCanvasGeoModel;
import com.github.runicrebirth.entities.DrawingCanvasEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Map;
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
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class DrawingCanvasRenderer extends GeoEntityRenderer<DrawingCanvasEntity> {

  private static final Map<String, Integer> ELEMENT_COLORS = Map.of(
      "arcane_circle", 0xFFAE78FF,
      "fire_circle",   0xFFFF6600,
      "ice_circle",    0xFF345EC3,
      "earth_circle",  0xFF8B6914,
      "air_circle",    0xFFD7DBE5
  );

  public DrawingCanvasRenderer(EntityRendererProvider.Context context) {
    super(context, new DrawingCanvasGeoModel());
    this.shadowRadius = 0f;
  }

  @Override
  public RenderType getRenderType(DrawingCanvasEntity entity, ResourceLocation texture,
      @Nullable MultiBufferSource bufferSource, float partialTick) {
    return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
  }


  @Override
  public void renderRecursively(PoseStack poseStack, DrawingCanvasEntity animatable, GeoBone bone,
      RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
      boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

    String name = bone.getName();

    if (ELEMENT_COLORS.containsKey(name)) {
      ResourceLocation texture = getTextureLocation(animatable);
      RenderType emissive = RenderType.entityTranslucent(texture);
      VertexConsumer emissiveBuffer = bufferSource.getBuffer(emissive);
      super.renderRecursively(poseStack, animatable, bone, emissive, bufferSource, emissiveBuffer,
          isReRender, partialTick, packedLight, packedOverlay, ELEMENT_COLORS.get(name));
      return;
    }

    if ("drawing_circle".equals(name) || name.startsWith("rune_")
        || "spell_symbol".equals(name) || "spell_symbols".equals(name) || "spell_symbols2".equals(name)) {
      ResourceLocation texture = getTextureLocation(animatable);
      RenderType emissive = RenderType.entityTranslucent(texture);
      VertexConsumer emissiveBuffer = bufferSource.getBuffer(emissive);
      super.renderRecursively(poseStack, animatable, bone, emissive, bufferSource, emissiveBuffer,
          isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
      return;
    }

    if (name.equals("adv_rings")) {
      ResourceLocation texture = getTextureLocation(animatable);
      RenderType cutout = ModRenderTypes.entityTranslucentNoCullNoShade(texture);
      VertexConsumer cutoutBuffer = bufferSource.getBuffer(cutout);
      super.renderRecursively(poseStack, animatable, bone, cutout, bufferSource, cutoutBuffer,
          isReRender, partialTick, packedLight, packedOverlay, 0xFFEDDAAA);
      return;
    }

    super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
        isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
  }

  @Override
  protected void applyRotations(DrawingCanvasEntity entity, PoseStack poseStack,
      float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
    float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
    float xRot = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
    poseStack.mulPose(Axis.YP.rotationDegrees(180f - yRot));
    poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));
  }

}