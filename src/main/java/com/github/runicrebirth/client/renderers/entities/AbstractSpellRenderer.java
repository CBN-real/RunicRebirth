package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.NormalOverrideVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
    public RenderType getRenderType(T entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone,
        RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        ResourceLocation texture = getTextureLocation(animatable);
        String name = bone.getName();

        if (name.endsWith("_es")) {
            RenderType swirlType = RenderType.energySwirl(texture, 0, 0);
            VertexConsumer swirlBuffer = bufferSource.getBuffer(swirlType);
            super.renderRecursively(poseStack, animatable, bone, swirlType, bufferSource, swirlBuffer,
                isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
            return;
        }

        RenderType cutType = ModRenderTypes.entityTranslucentNoCullNoShade(texture);
        VertexConsumer cutBuffer = new NormalOverrideVertexConsumer(bufferSource.getBuffer(cutType));
        super.renderRecursively(poseStack, animatable, bone, cutType, bufferSource, cutBuffer,
            isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, colour);
    }
}
