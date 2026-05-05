package com.github.interactivemagic.client.renderers.entities;

import com.github.interactivemagic.client.renderers.ModRenderTypes;
import com.github.interactivemagic.client.renderers.models.MagicShieldGeoModel;
import com.github.interactivemagic.entities.spells.MagicShieldEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class MagicShieldRenderer extends GeoEntityRenderer<MagicShieldEntity> {

    public MagicShieldRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicShieldGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(MagicShieldEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoDepth(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, MagicShieldEntity entity, BakedGeoModel model,
        @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay,
        int colour) {
        float scale = entity.getShieldSize();
        poseStack.scale(scale, scale, scale);
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
            partialTick, packedLight, packedOverlay, colour);
    }
}
