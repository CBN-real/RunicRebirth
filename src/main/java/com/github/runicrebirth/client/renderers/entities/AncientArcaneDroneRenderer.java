package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.AncientArcaneDroneGeoModel;
import com.github.runicrebirth.entities.mobs.AncientArcaneDroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AncientArcaneDroneRenderer extends GeoEntityRenderer<AncientArcaneDroneEntity> {

    public AncientArcaneDroneRenderer(EntityRendererProvider.Context context) {
        super(context, new AncientArcaneDroneGeoModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public RenderType getRenderType(AncientArcaneDroneEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    protected void applyRotations(AncientArcaneDroneEntity entity, PoseStack poseStack,
        float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float yaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yaw));
    }
}
