package com.github.runicrebirth.client.renderers.entities;

import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.client.renderers.models.MagicBallistaGeoModel;
import com.github.runicrebirth.entities.spells.MagicArrowEntity;
import com.github.runicrebirth.entities.spells.MagicBallistaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.BakedGeoModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderState;

public class MagicBallistaRenderer<R extends EntityRenderState & GeoRenderState> extends AbstractSpellRenderer<MagicBallistaEntity, R> {

    public MagicBallistaRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicBallistaGeoModel());
    }

    @Override
    protected void applyRotations(com.geckolib.renderer.base.RenderPassInfo<R> renderPassInfo, PoseStack poseStack, float nativeScale) {
      float yRot = renderPassInfo.getOrDefaultGeckolibData(com.geckolib.constant.DataTickets.ENTITY_BODY_YAW, 0f);
      float xRot = renderPassInfo.getOrDefaultGeckolibData(AbstractSpellRenderer.SPELL_XROT, 0f);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(180-xRot));
    }


}
