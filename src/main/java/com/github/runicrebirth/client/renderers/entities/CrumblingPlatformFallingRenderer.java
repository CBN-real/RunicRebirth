package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.entities.CrumblingPlatformFallingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrumblingPlatformFallingRenderer extends EntityRenderer<CrumblingPlatformFallingEntity> {

    private final FallingBlockRenderer delegate;

    public CrumblingPlatformFallingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.delegate = new FallingBlockRenderer(ctx);
    }

    @Override
    public void render(CrumblingPlatformFallingEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        delegate.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ResourceLocation getTextureLocation(CrumblingPlatformFallingEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
