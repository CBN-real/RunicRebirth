package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.DungeonBoulderGeoModel;
import com.github.runicrebirth.entities.DungeonBoulderEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DungeonBoulderRenderer extends GeoEntityRenderer<DungeonBoulderEntity> {

    public DungeonBoulderRenderer(EntityRendererProvider.Context context) {
        super(context, new DungeonBoulderGeoModel());
        this.shadowRadius = 1.5f;
    }

    @Override
    protected void applyRotations(DungeonBoulderEntity animatable, PoseStack poseStack,
            float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        // Apply travel direction yaw directly; skip super to avoid GeckoLib's 180-yRot flip
        float yaw = animatable.getTravelDirection().get2DDataValue() * 90f;
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
    }
}
