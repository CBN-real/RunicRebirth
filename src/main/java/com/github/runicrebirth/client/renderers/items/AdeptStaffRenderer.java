package com.github.runicrebirth.client.renderers.items;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.items.AdeptStaffItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AdeptStaffRenderer extends GeoItemRenderer<AdeptStaffItem> {

    public AdeptStaffRenderer() {
        super(new AdeptStaffModel());
    }

    @Override
    public RenderType getRenderType(AdeptStaffItem animatable, ResourceLocation texture,
                                     @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.MultiBufferSource bufferSource,
                                     float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

  @Override
  public void renderRecursively(PoseStack poseStack, AdeptStaffItem animatable, GeoBone bone,
      RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
      boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
      ResourceLocation texture = getTextureLocation(animatable);
      String name = bone.getName();

      if (name.endsWith("_tr")) {
        RenderType trType = ModRenderTypes.entityUnlit(texture);
        VertexConsumer trBuffer = bufferSource.getBuffer(trType);
        super.renderRecursively(poseStack, animatable, bone, trType, bufferSource, trBuffer,
            isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
        return;
      }

      super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
          isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
