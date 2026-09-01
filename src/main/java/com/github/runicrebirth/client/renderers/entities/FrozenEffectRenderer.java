package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.FrozenEffectGeoModel;
import com.github.runicrebirth.entities.spells.FrozenEffectEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class FrozenEffectRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<FrozenEffectEntity, R> {

    private static final DataTicket<Float> FROZEN_SCALE = DataTicket.create("runicrebirth:frozen_scale", Float.class);
    public static final DataTicket<Integer> FROZEN_DURATION = DataTicket.create("runicrebirth:frozen_duration", Integer.class);
    public static final DataTicket<Integer> FROZEN_TICK = DataTicket.create("runicrebirth:frozen_tick", Integer.class);

    public FrozenEffectRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FrozenEffectGeoModel());
        this.shadowRadius = 0f;
    }

    @Override
    public void extractRenderState(FrozenEffectEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        Entity target = entity.level().getEntity(entity.getTargetEntityId());
        float width = target != null ? target.getBbWidth() : 1.0f;
        renderState.addGeckolibData(FROZEN_SCALE, width + 0.2f);
        renderState.addGeckolibData(FROZEN_DURATION, entity.freezeDurationTicks);
        renderState.addGeckolibData(FROZEN_TICK, entity.tickCount);
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.getOrDefaultGeckolibData(FROZEN_SCALE, 1.2f);
        renderPassInfo.poseStack().scale(scale, scale, scale);
        super.scaleModelForRender(renderPassInfo, widthScale, heightScale);
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        // no rotation - always faces fixed direction on top of the frozen target
    }
}
