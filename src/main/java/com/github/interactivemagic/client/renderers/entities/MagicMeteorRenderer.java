package com.github.interactivemagic.client.renderers.entities;

import com.github.interactivemagic.client.renderers.ModRenderTypes;
import com.github.interactivemagic.client.renderers.models.MagicMeteorGeoModel;
import com.github.interactivemagic.entities.spells.MagicMeteorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class MagicMeteorRenderer extends GeoEntityRenderer<MagicMeteorEntity> {

    public MagicMeteorRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicMeteorGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(MagicMeteorEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoDepth(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, MagicMeteorEntity entity, BakedGeoModel model,
        @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
        boolean isReRender, float partialTick, int packedLight, int packedOverlay,
        int colour) {
        Vec3 motion = entity.getDeltaMovement();
        if (motion.lengthSqr() > 0.001) {
            float xRot = (float) (Mth.atan2(motion.y, motion.horizontalDistance()) * Mth.RAD_TO_DEG);
            poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        }
        float scale = entity.getProjectileSize();
        poseStack.scale(scale, scale, scale);
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender,
            partialTick, packedLight, packedOverlay, colour);
    }
}
