package com.github.runicrebirth.client.renderers.blocks;

import com.github.runicrebirth.blocks.entity.TatteredSectBannerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;

public class TatteredSectBannerRenderer extends AbstractRunicBlockRenderer<TatteredSectBannerBlockEntity> {

    public TatteredSectBannerRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new TatteredSectBannerModel());
    }

    // tattered geo has top/stand bones with 180° Y baked in → fabric faces south by default
    private float getModelBaseY() { return 180f; }

    @Override
    public void render(TatteredSectBannerBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        SectBannerRenderer.applyBannerPose(poseStack, be.getBlockState(), getModelBaseY());
        super.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, TatteredSectBannerBlockEntity animatable, GeoBone bone,
            RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
            boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource,
                buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if ("banner_fabric".equals(bone.getName()) && !isReRender) {
            SectBannerRenderer.renderPatternLayers(poseStack, bufferSource, animatable, bone, packedLight, packedOverlay);
        }
    }
}
