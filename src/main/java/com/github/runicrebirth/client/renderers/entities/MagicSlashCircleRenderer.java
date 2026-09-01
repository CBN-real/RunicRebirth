package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.models.MagicSlashCircleGeoModel;
import com.github.runicrebirth.entities.spells.MagicSlashCircleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicSlashCircleRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicSlashCircleEntity, R> {

    private static final DataTicket<Float> SLASH_CIRCLE_SCALE = DataTicket.create("runicrebirth:slash_circle_scale", Float.class);

    public MagicSlashCircleRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicSlashCircleGeoModel());
    }

    @Override
    public void extractRenderState(MagicSlashCircleEntity entity, R renderState, float partialTick) {
        super.extractRenderState(entity, renderState, partialTick);
        renderState.addGeckolibData(SLASH_CIRCLE_SCALE, entity.getCircleScale());
    }

    @Override
    public @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    @Override
    protected void applyRotations(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
        float yRot = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_BODY_YAW, 0f);
        float xRot = renderPassInfo.getOrDefaultGeckolibData(AbstractSpellRenderer.SPELL_XROT, 0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-xRot));
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.getOrDefaultGeckolibData(SLASH_CIRCLE_SCALE, 1f);
        renderPassInfo.poseStack().scale(scale, scale, scale);
        super.scaleModelForRender(renderPassInfo, widthScale, heightScale);
    }
}
