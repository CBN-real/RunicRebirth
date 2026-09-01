package com.github.runicrebirth.client.renderers.entities;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.cuboid.CuboidGeoBone;
import com.geckolib.cache.model.cuboid.GeoCube;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.PerBoneRender;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.DrawingCanvasGeoModel;
import com.github.runicrebirth.entities.DrawingCanvasEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public class DrawingCanvasRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<DrawingCanvasEntity, R> {

    private static final Map<String, Integer> ELEMENT_COLORS = Map.of(
        "arcane_circle", 0xFFAE78FF,
        "fire_circle",   0xFFFF6600,
        "ice_circle",    0xFF345EC3,
        "earth_circle",  0xFF8B6914,
        "air_circle",    0xFFD7DBE5
    );

    private static final DataTicket<Float> CANVAS_XROT = DataTicket.create("runicrebirth_canvas_xrot", Float.class);

    private static boolean isFullBrightBone(String name) {
        return "drawing_circle".equals(name) || name.startsWith("rune_")
            || "spell_symbol".equals(name) || "spell_symbols".equals(name) || "spell_symbols2".equals(name);
    }

    private static boolean isSpecialBone(String name) {
        return ELEMENT_COLORS.containsKey(name) || isFullBrightBone(name) || "adv_rings".equals(name);
    }

    public DrawingCanvasRenderer(EntityRendererProvider.Context context) {
        super(context, new DrawingCanvasGeoModel());
        this.shadowRadius = 0f;
        withRenderLayer(new CanvasBoneColorLayer<>(this));
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return ModRenderTypes.entityTranslucentNoCullNoShade(texture);
    }

    @Override
    public void addRenderData(DrawingCanvasEntity animatable, @Nullable Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(CANVAS_XROT, Mth.rotLerp(partialTick, animatable.xRotO, animatable.getXRot()));
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {
        R renderState = renderPassInfo.renderState();
        PoseStack poseStack = renderPassInfo.poseStack();
        float yRot = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_BODY_YAW, 0f);
        float xRot = renderState.getOrDefaultGeckolibData(CANVAS_XROT, 0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));
        poseStack.translate(0, 0.01f, 0);
    }

    private static class CanvasBoneColorLayer<R extends GeoRenderState> extends GeoRenderLayer<DrawingCanvasEntity, Void, R> {

        CanvasBoneColorLayer(GeoRenderer<DrawingCanvasEntity, Void, R> renderer) {
            super(renderer);
        }

        @Override
        public void preRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
            if (!renderPassInfo.willRender()) return;
            renderPassInfo.addBoneUpdater((info, snapshots) -> {
                for (String name : info.model().boneLookup().get().keySet()) {
                    if (isSpecialBone(name)) {
                        snapshots.ifPresent(name, snapshot -> snapshot.skipRender(true));
                    }
                }
            });
        }

        @Override
        public void addPerBoneRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
            if (!renderPassInfo.willRender()) return;
            Identifier texture = renderer.getTextureLocation(renderPassInfo.renderState());

            for (Map.Entry<String, GeoBone> entry : renderPassInfo.model().boneLookup().get().entrySet()) {
                String name = entry.getKey();
                GeoBone bone = entry.getValue();
                if (!(bone instanceof CuboidGeoBone)) continue;

                if (ELEMENT_COLORS.containsKey(name)) {
                    int color = ELEMENT_COLORS.get(name);
                    RenderType rt = RenderTypes.entityTranslucent(texture);
                    consumer.accept(bone, (info, b, tasks) -> renderBone(info, b, tasks, rt, LightCoordsUtil.FULL_BRIGHT, color));
                } else if (isFullBrightBone(name)) {
                    RenderType rt = RenderTypes.entityTranslucent(texture);
                    consumer.accept(bone, (info, b, tasks) -> renderBone(info, b, tasks, rt, LightCoordsUtil.FULL_BRIGHT, info.renderColor()));
                } else if ("adv_rings".equals(name)) {
                    RenderType rt = ModRenderTypes.entityTranslucentNoCullNoShade(texture);
                    consumer.accept(bone, (info, b, tasks) -> renderBone(info, b, tasks, rt, info.packedLight(), 0xFFEDDAAA));
                }
            }
        }

        private void renderBone(RenderPassInfo<R> renderPassInfo, GeoBone bone, SubmitNodeCollector renderTasks, RenderType renderType, int packedLight, int color) {
            renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, buffer) -> {
                PoseStack poseStack = renderPassInfo.poseStack();
                poseStack.pushPose();
                poseStack.last().set(pose);
                bone.translateAwayFromPivotPoint(poseStack);
                for (GeoCube cube : ((CuboidGeoBone) bone).cubes) {
                    poseStack.pushPose();
                    cube.render(poseStack, buffer, packedLight, renderPassInfo.packedOverlay(), color);
                    poseStack.popPose();
                }
                poseStack.popPose();
            });
        }
    }
}
