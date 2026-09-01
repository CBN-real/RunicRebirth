package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.MagicHandGeoModel;
import com.github.runicrebirth.entities.MagicHandEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.world.phys.Vec3;

import java.util.WeakHashMap;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicHandRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<MagicHandEntity, R> {

    private static final double LERP_STIFFNESS = 14.0;
    private static final DataTicket<Vec3> SMOOTH_OFFSET = DataTicket.create("runicrebirth:hand_smooth_offset", Vec3.class);

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
    public void extractRenderState(MagicHandEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
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
        renderState.addGeckolibData(SMOOTH_OFFSET, new Vec3(s.x - tx, s.y - ty, s.z - tz));
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        Vec3 offset = renderPassInfo.getOrDefaultGeckolibData(SMOOTH_OFFSET, Vec3.ZERO);
        if (offset != null) {
            renderPassInfo.poseStack().translate(offset.x, offset.y, offset.z);
        }
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        // no rotation - geo root bone handles orientation
    }
}
