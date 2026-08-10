package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.DrawingCanvasGeoModel;
import com.github.runicrebirth.entities.DrawingCanvasEntity;
import com.github.runicrebirth.init.ModParticles;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.github.runicrebirth.particle.ScaledParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
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

  private static final double[][] PARTICLE_MODEL_OFFSETS = {
      {-5.966, 4.55418, 5.58382},
      {-7.196,  2.08855, 6.37382},
      {-7.376,  -0.38418, 6.77382},
      {-6.686,  -2.68418, 6.37382},
      {-5.556,  -4.94418, 5.13382}
  };

  private static final String[] PARTICLE_ELEMENTS = {
      "arcane", "fire", "ice", "earth", "wind"
  };

  public DrawingCanvasRenderer(EntityRendererProvider.Context context) {
    super(context, new DrawingCanvasGeoModel());
    this.shadowRadius = 0f;
  }

  @Override
  public RenderType getRenderType(DrawingCanvasEntity entity, ResourceLocation texture,
      @Nullable MultiBufferSource bufferSource, float partialTick) {
    return RenderType.entityCutoutNoCull(texture);
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
      RenderType cutout = RenderType.entityCutoutNoCull(texture);
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

  @Override
  public void render(DrawingCanvasEntity entity, float entityYaw, float partialTick,
      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
    super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

    if (entity.tickCount % 5 != 0 || entity.getPhaseOrdinal() != 1) return;

    Vec3 entityPos = entity.position();
    float yawRad = (float) Math.toRadians(180.0 - entity.getYRot());
    float pitchRad = (float) Math.toRadians(-entity.getXRot());
    float cosY = Mth.cos(yawRad);
    float sinY = Mth.sin(yawRad);
    float cosP = Mth.cos(pitchRad);
    float sinP = Mth.sin(pitchRad);

    for (int i = 0; i < PARTICLE_ELEMENTS.length; i++) {
      ParticleOptions particle = getSparkParticleForElement(PARTICLE_ELEMENTS[i]);
      if (particle == null) continue;

      double mx = PARTICLE_MODEL_OFFSETS[i][0] / 16.0;
      double my = PARTICLE_MODEL_OFFSETS[i][1] / 16.0;
      double mz = PARTICLE_MODEL_OFFSETS[i][2] / 16.0;

      double rx = mx;
      double ry = my * cosP - mz * sinP;
      double rz = my * sinP + mz * cosP;

      double wx = entityPos.x + rx * cosY + rz * sinY;
      double wy = entityPos.y + ry;
      double wz = entityPos.z - rx * sinY + rz * cosY;

      entity.level().addParticle(particle, wx, wy, wz, 0, 0.00, 0);
    }
  }

  @Nullable
  private ParticleOptions getParticleForElement(String element) {
    return switch (element) {
      case "arcane" -> new ScaledParticleOption(ModParticles.ARCANE_ELEMENT.get(), 1.0f);
      case "fire" -> new ScaledParticleOption(ModParticles.FIRE_ELEMENT.get(), 1.0f);
      case "ice" -> new ScaledParticleOption(ModParticles.ICE_ELEMENT.get(), 1.0f);
      case "earth" -> new ScaledParticleOption(ModParticles.EARTH_ELEMENT.get(), 1.0f);
      case "wind" -> new ScaledParticleOption(ModParticles.WIND_ELEMENT.get(), 1.0f);
      default -> null;
    };
  }

  @Nullable
  private ParticleOptions getSparkParticleForElement(String element) {
    return switch (element) {
      case "arcane" -> new ScaledParticleOption(ModParticles.ARCANE_TINY.get(), 1.0f);
      case "fire" -> new ScaledParticleOption(ModParticles.FIRE_TINY.get(), 1.0f);
      case "ice" -> new ScaledParticleOption(ModParticles.ICE_TINY.get(), 1.0f);
      case "earth" -> new ScaledParticleOption(ModParticles.EARTH_TINY.get(), 1.0f);
      case "wind" -> new ScaledParticleOption(ModParticles.WIND_TINY.get(), 1.0f);
      default -> null;
    };
  }
}