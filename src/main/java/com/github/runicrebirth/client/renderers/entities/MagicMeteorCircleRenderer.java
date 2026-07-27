package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicMeteorCircleGeoModel;
import com.github.runicrebirth.entities.spells.MagicMeteorCircleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class MagicMeteorCircleRenderer extends AbstractSpellRenderer<MagicMeteorCircleEntity> {

    public MagicMeteorCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicMeteorCircleGeoModel());
    }

    @Override
    protected void applyRotations(MagicMeteorCircleEntity entity, PoseStack poseStack,
        float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float xRot = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-38f + xRot));
    }
}
