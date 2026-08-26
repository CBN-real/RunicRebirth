package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.FrozenEffectGeoModel;
import com.github.runicrebirth.entities.spells.FrozenEffectEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FrozenEffectRenderer extends GeoEntityRenderer<FrozenEffectEntity> {

    public FrozenEffectRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FrozenEffectGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(FrozenEffectEntity entity, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void render(FrozenEffectEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Entity target = entity.level().getEntity(entity.getTargetEntityId());
        float width = target != null ? target.getBbWidth() : 1.0f;
        poseStack.scale(width + 0.2f, width + 0.2f, width + 0.2f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void applyRotations(FrozenEffectEntity entity, PoseStack poseStack,
                                   float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        // no rotation — always faces fixed direction on top of the frozen target
    }
}
