package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.ThrownRunicDaggerGeoModel;
import com.github.runicrebirth.entities.ThrownRunicDaggerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class ThrownRunicDaggerRenderer extends GeoEntityRenderer<ThrownRunicDaggerEntity> {

    public ThrownRunicDaggerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ThrownRunicDaggerGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(ThrownRunicDaggerEntity entity, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public boolean shouldRender(ThrownRunicDaggerEntity entity,
                                 net.minecraft.client.renderer.culling.Frustum frustum,
                                 double x, double y, double z) {
        return true;
    }

    @Override
    public void preRender(PoseStack poseStack, ThrownRunicDaggerEntity animatable, BakedGeoModel model,
                          @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        model.getBone("runic_dagger").ifPresent(b -> b.setHidden(false));
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    protected void applyRotations(ThrownRunicDaggerEntity entity, PoseStack poseStack,
            float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float yaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yaw));
        ThrownRunicDaggerEntity.Phase phase = entity.getPhase();
        if (phase == ThrownRunicDaggerEntity.Phase.STUCK_BLOCK
                || phase == ThrownRunicDaggerEntity.Phase.STUCK_ENTITY) {
            float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90f));
        }
    }
}
