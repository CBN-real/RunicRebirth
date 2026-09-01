package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.client.BookDisplayState;
import com.github.runicrebirth.client.renderers.ModDataTickets;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.NormalOverrideVertexConsumer;
import com.github.runicrebirth.entities.spells.AbstractCircleEntity;
import com.github.runicrebirth.RunicRebirth;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.PerBoneRender;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.function.BiConsumer;

public abstract class AbstractCircleRenderer<T extends AbstractCircleEntity, R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {

    private static final int FULL_BRIGHT = 0xF000F0;

    protected AbstractCircleRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        this.shadowRadius = 0f;
        withRenderLayer(new BoneRouterLayer<>(this));
    }

    @Override
    public void addRenderData(T entity, Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(entity, relatedObject, renderState, partialTick);

        boolean inBook = !entity.isAddedToLevel();
        float scale = entity.getCircleScale();
        SpellType spellType = SpellTypeRegistry.get(
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, entity.getSpellTypeId()));
        if (spellType != null) {
            scale = Math.max(1.0f, spellType.spellHeight() * scale);
        }

        renderState.addGeckolibData(ModDataTickets.RENDER_IN_BOOK, inBook);
        renderState.addGeckolibData(ModDataTickets.CIRCLE_RENDER_SCALE, scale);
        renderState.addGeckolibData(ModDataTickets.ENTITY_Y_ROT_LERP, Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot()));
        renderState.addGeckolibData(ModDataTickets.ENTITY_X_ROT_LERP, Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot()));
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        // All bone geometry is submitted per-bone by BoneRouterLayer below; suppress the default single-pass submission.
        return null;
    }

    @Override
    public void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.preRenderPass(renderPassInfo, renderTasks);

        if (Boolean.TRUE.equals(renderPassInfo.getGeckolibData(ModDataTickets.RENDER_IN_BOOK))) {
            PoseStack poseStack = renderPassInfo.poseStack();

            poseStack.mulPose(Axis.XP.rotationDegrees(30));

            float ox = BookDisplayState.getOffsetX();
            float oy = BookDisplayState.getOffsetY();
            float oz = BookDisplayState.getOffsetZ();
            if (ox != 0f || oy != 0f || oz != 0f) {
                poseStack.translate(ox, oy, oz);
            }
        }
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.getOrDefaultGeckolibData(ModDataTickets.CIRCLE_RENDER_SCALE, 1f);

        super.scaleModelForRender(renderPassInfo, widthScale * scale, heightScale * scale);
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        float yRot = renderPassInfo.getOrDefaultGeckolibData(ModDataTickets.ENTITY_Y_ROT_LERP, 0f);
        float xRot = renderPassInfo.getOrDefaultGeckolibData(ModDataTickets.ENTITY_X_ROT_LERP, 0f);

        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));
    }

    /// Routes each bone to a different [RenderType]/[VertexConsumer], replicating the GeckoLib4
    /// per-bone `renderRecursively` override this renderer used before GeckoLib5 removed that hook.
    private static class BoneRouterLayer<T extends AbstractCircleEntity, R extends EntityRenderState & GeoRenderState> extends GeoRenderLayer<T, Void, R> {
        BoneRouterLayer(GeoEntityRenderer<T, R> renderer) {
            super(renderer);
        }

        @Override
        public void addPerBoneRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
            for (GeoBone bone : renderPassInfo.model().boneLookup().get().values()) {
                consumer.accept(bone, this::renderBone);
            }
        }

        private void renderBone(RenderPassInfo<R> passInfo, GeoBone bone, SubmitNodeCollector renderTasks) {
            Identifier texture = getTextureResource(passInfo.renderState());
            boolean inBook = Boolean.TRUE.equals(passInfo.getGeckolibData(ModDataTickets.RENDER_IN_BOOK));

            RenderType type;
            boolean wrapNormal = false;

            if (inBook) {
                type = ModRenderTypes.entityUnlit(texture);
            }
            else if (bone.name().startsWith("rune_")) {
                type = RenderTypes.entityTranslucent(texture);
            }
            else {
                type = ModRenderTypes.entityTranslucentNoCullNoShade(texture);
                wrapNormal = true;
            }

            submitBone(passInfo, bone, renderTasks, type, wrapNormal);
        }

        private void submitBone(RenderPassInfo<R> passInfo, GeoBone bone, SubmitNodeCollector renderTasks,
            RenderType type, boolean wrapNormal) {
            int overlay = passInfo.packedOverlay();
            int color = passInfo.renderColor();

            renderTasks.submitCustomGeometry(passInfo.poseStack(), type, (pose, vertexConsumer) -> {
                PoseStack poseStack = passInfo.poseStack();

                poseStack.pushPose();
                poseStack.last().set(pose);

                VertexConsumer vc = wrapNormal ? new NormalOverrideVertexConsumer(vertexConsumer) : vertexConsumer;
                bone.render(passInfo, poseStack, vc, FULL_BRIGHT, overlay, color);

                poseStack.popPose();
            });
        }
    }
}
