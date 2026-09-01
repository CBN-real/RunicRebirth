package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.entities.PhantomMinerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class PhantomMinerRenderer extends EntityRenderer<PhantomMinerEntity, PhantomMinerRenderer.State> {

    public static class State extends EntityRenderState {}

    public PhantomMinerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0f;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PhantomMinerEntity entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

}
