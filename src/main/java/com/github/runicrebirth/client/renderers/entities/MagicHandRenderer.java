package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.MagicHandGeoModel;
import com.github.runicrebirth.entities.MagicHandEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
public class MagicHandRenderer extends GeoEntityRenderer<MagicHandEntity> {

    private static final double LERP_STIFFNESS = 14.0;

    private static final class SmoothState {
        double x, y, z;
        long lastNanos;
    }

    private static final WeakHashMap<MagicHandEntity, SmoothState> SMOOTH = new WeakHashMap<>();

    public MagicHandRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicHandGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public RenderType getRenderType(MagicHandEntity entity, ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource, float partialTick) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    protected void applyRotations(MagicHandEntity entity, PoseStack poseStack,
        float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        // no entity rotation applied — geo root bone handles orientation
    }

    @Override
    public void render(MagicHandEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Vanilla-interpolated entity position (baseline target)
        double tx = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double ty = Mth.lerp(partialTick, entity.yOld, entity.getY());
        double tz = Mth.lerp(partialTick, entity.zOld, entity.getZ());

        SmoothState s = SMOOTH.get(entity);
        if (s == null) {
            s = new SmoothState();
            s.x = tx; s.y = ty; s.z = tz;
            s.lastNanos = System.nanoTime();
            SMOOTH.put(entity, s);
        } else {
            long now = System.nanoTime();
            double dt = Math.min((now - s.lastNanos) * 1e-9, 0.1);
            s.lastNanos = now;
            double alpha = dt > 0 ? 1.0 - Math.exp(-LERP_STIFFNESS * dt) : 1.0;
            s.x += (tx - s.x) * alpha;
            s.y += (ty - s.y) * alpha;
            s.z += (tz - s.z) * alpha;
        }

        // Translate from vanilla-interpolated pos to smooth display pos
        poseStack.translate(s.x - tx, s.y - ty, s.z - tz);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
