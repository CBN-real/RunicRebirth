package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.AbstractSectBannerBlock;
import com.github.runicrebirth.blocks.entity.AbstractSectBannerBlockEntity;
import com.github.runicrebirth.blocks.entity.SectBannerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;

public class SectBannerRenderer extends AbstractRunicBlockRenderer<SectBannerBlockEntity> {

    public SectBannerRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new SectBannerModel());
    }

    @Override
    public void render(SectBannerBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        applyBannerPose(poseStack, be.getBlockState(), getModelBaseY());
        super.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    protected float getModelBaseY() { return 0f; }

    static void applyBannerPose(PoseStack poseStack, BlockState state, float baseY) {
        AttachFace face = state.getValue(AbstractSectBannerBlock.FACE);
        Direction facing = state.getValue(AbstractSectBannerBlock.FACING);
        float yDeg = facingToYDeg(facing) + baseY;

        poseStack.pushPose();
        if (face == AttachFace.CEILING) {
            // Flip upside-down from block top; negate yDeg because local Y axis is inverted after flip
//            poseStack.translate(0.5, 1.0, 0.5);
//            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
//            poseStack.mulPose(Axis.YP.rotationDegrees(-yDeg));
        } else {
            // FLOOR and WALL: upright, rotate around Y through block horizontal center
//            poseStack.translate(0.5, 0.0, 0.5);
//            poseStack.mulPose(Axis.YP.rotationDegrees(yDeg));
//            poseStack.translate(-0.5, 0.0, -0.5);
        }
    }

    // sect_banner geo has fabric at z≈-2, facing north by default
    static float facingToYDeg(Direction facing) {
        return switch (facing) {
            case NORTH -> 180f;
            case WEST  -> 180f;
            case SOUTH -> 180f;
            case EAST  -> 180f;
            default    -> 180f;
        };
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SectBannerBlockEntity animatable, GeoBone bone,
            RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
            boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if ("banner_fabric".equals(bone.getName()) && !isReRender) {
            renderPatternLayers(poseStack, bufferSource, animatable, bone, packedLight, packedOverlay);
        }
    }

    static void renderPatternLayers(PoseStack poseStack, MultiBufferSource bufferSource,
            AbstractSectBannerBlockEntity be, GeoBone bone, int packedLight, int packedOverlay) {
        BannerPatternLayers patterns = be.getPatterns();
        if (patterns == null || patterns.layers().isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(bone.getPivotX() / 16.0, bone.getPivotY() / 16.0, bone.getPivotZ() / 16.0);

        for (BannerPatternLayers.Layer layer : patterns.layers()) {
            layer.pattern().unwrapKey().ifPresent(key -> {
                String patternPath = key.location().getPath();
                ResourceLocation tex = ResourceLocation.withDefaultNamespace(
                        "textures/entity/banner/" + patternPath + ".png");
                renderColoredQuad(poseStack, bufferSource, tex,
                        dyeToArgb(layer.color()), packedLight, packedOverlay);
            });
        }
        poseStack.popPose();
    }

    static void renderColoredQuad(PoseStack poseStack, MultiBufferSource bufferSource,
            ResourceLocation texture, int argb, int packedLight, int packedOverlay) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityTranslucentCull(texture));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f mat = pose.pose();

        int r = FastColor.ARGB32.red(argb);
        int g = FastColor.ARGB32.green(argb);
        int b = FastColor.ARGB32.blue(argb);
        int a = FastColor.ARGB32.alpha(argb);

        float hw = 0.5f, hh = 0.75f;
        vc.addVertex(mat, -hw, -hh, 0).setColor(r,g,b,a).setUv(0,1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0,0,1);
        vc.addVertex(mat,  hw, -hh, 0).setColor(r,g,b,a).setUv(1,1).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0,0,1);
        vc.addVertex(mat,  hw,  hh, 0).setColor(r,g,b,a).setUv(1,0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0,0,1);
        vc.addVertex(mat, -hw,  hh, 0).setColor(r,g,b,a).setUv(0,0).setOverlay(packedOverlay).setLight(packedLight).setNormal(pose, 0,0,1);
    }

    static int dyeToArgb(DyeColor color) {
        return FastColor.ARGB32.opaque(color.getTextureDiffuseColor());
    }
}
