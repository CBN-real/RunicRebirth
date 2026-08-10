package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.RunelightWallTorchBlock;
import com.github.runicrebirth.blocks.entity.RunelightTorchBlockEntity;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.NormalOverrideVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Optional;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

@OnlyIn(Dist.CLIENT)
public class RunelightTorchRenderer extends GeoBlockRenderer<RunelightTorchBlockEntity> {

    public RunelightTorchRenderer(BlockEntityRendererProvider.Context context) {
        super(new RunelightTorchModel());
    }

    @Override
    public AABB getRenderBoundingBox(RunelightTorchBlockEntity blockEntity) {
        return AABB.INFINITE;
    }

    @Override
    public RenderType getRenderType(RunelightTorchBlockEntity animatable, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    public void render(RunelightTorchBlockEntity blockEntity, float partialTick, PoseStack poseStack,
        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        BlockState state = blockEntity.getBlockState();
        if (state.hasProperty(RunelightWallTorchBlock.FACING)) {
            poseStack.pushPose();
            applyWallTransform(poseStack, state.getValue(RunelightWallTorchBlock.FACING));
            super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        } else {
            super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    // Rotates and tilts the model to lean against the given wall face.
    // Torch block "facing" = direction the torch points (away from wall).
    private void applyWallTransform(PoseStack poseStack, Direction facing) {
        float yRot = switch (facing) {
            case NORTH -> 0f;
            case SOUTH -> 180f;
            case WEST  -> 90f;
            case EAST  -> 270f;
            default    -> 0f;
        };
        // Rotate around block center to align lean direction, then tilt 22.5° toward wall
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.translate(0.0, 0.0, -0.5 + (2.0 / 16.0)); // push base toward wall
        poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));   // tilt outward from wall
        poseStack.translate(-0.5, -0.1, 0.39);

    }

    @Override
    public void renderRecursively(PoseStack poseStack, RunelightTorchBlockEntity animatable, GeoBone bone,
        RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        ResourceLocation texture = getTextureLocation(animatable);

        // Default: translucent no-cull no-shade with flat normals
        RenderType noShadeType = RenderType.entityTranslucent(texture);
        VertexConsumer noShadeBuffer = new NormalOverrideVertexConsumer(bufferSource.getBuffer(noShadeType));
        super.renderRecursively(poseStack, animatable, bone, noShadeType, bufferSource, noShadeBuffer,
            isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
    }

  @Override
  public void preRender(PoseStack poseStack, RunelightTorchBlockEntity animatable,
      BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
      int packedOverlay, int colour) {

      for (int i = 1; i <=8; i++) {
          Optional<GeoBone> bone = model.getBone("rune_" + i + "_s1");
          if (i != animatable.getSelectedRune()) {
            bone.ifPresent(geoBone -> geoBone.setHidden(true));
          } else {
            bone.ifPresent(geoBone -> geoBone.setHidden(false));
          }
      }

    super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
        packedLight, packedOverlay, colour);
  }
}
