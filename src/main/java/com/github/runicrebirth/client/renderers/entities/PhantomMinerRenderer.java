package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.entities.PhantomMinerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhantomMinerRenderer extends EntityRenderer<PhantomMinerEntity> {

  private static final int SWING_CYCLE = 8;

  public PhantomMinerRenderer(EntityRendererProvider.Context context) {
    super(context);
    this.shadowRadius = 0f;
  }

  @Override
  public void render(PhantomMinerEntity entity, float entityYaw, float partialTick,
      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
    ItemStack stack = entity.getDisplayItem();
    if (stack.isEmpty()) return;

    Direction face = entity.getFace();
    poseStack.pushPose();

    // Rotate item to face toward the block (entity is in front of block face, faces inward)
    float yaw = switch (face) {
      case NORTH -> 0f;
      case SOUTH -> 180f;
      case EAST -> 90f;
      case WEST -> 270f;
      case UP, DOWN -> -entity.getDisplayYaw();
    };
    poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

    // For UP/DOWN faces, tilt item to face the block surface
    if (face == Direction.UP) {
      poseStack.mulPose(Axis.XP.rotationDegrees(90f));
    } else if (face == Direction.DOWN) {
      poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
    }

    // Mining swing: quick strike forward (0° → 70°), slow return (70° → 0°)
    float tickF = entity.tickCount + partialTick;
    float cyclePos = tickF % SWING_CYCLE;
    float swingAngle;
    if (cyclePos < 7f) {
      swingAngle = (cyclePos / 7f) * 70f;
    } else {
      swingAngle = ((SWING_CYCLE - cyclePos) / 5f) * 70f;
    }
    if (face == Direction.EAST || face == Direction.WEST) {
      poseStack.mulPose(Axis.XP.rotationDegrees(-swingAngle));
    } else {
      poseStack.mulPose(Axis.XP.rotationDegrees(-swingAngle));
    }


    poseStack.scale(0.5f, 0.5f, 0.5f);

    Minecraft.getInstance().getItemRenderer().renderStatic(
        stack,
        ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
        packedLight,
        OverlayTexture.NO_OVERLAY,
        poseStack,
        bufferSource,
        entity.level(),
        entity.getId()
    );

    poseStack.popPose();
    super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
  }

  @Override
  public ResourceLocation getTextureLocation(PhantomMinerEntity entity) {
    return ResourceLocation.withDefaultNamespace("textures/misc/white.png");
  }
}