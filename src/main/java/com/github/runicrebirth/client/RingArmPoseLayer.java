package com.github.runicrebirth.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RingArmPoseLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public RingArmPoseLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        boolean active = entity == Minecraft.getInstance().player
            ? ClientMagicData.isRingCastAnimActive()
            : ClientMagicData.isRingCastAnimActiveFor(entity.getId());
        if (!active) return;

        PlayerModel<AbstractClientPlayer> model = this.getParentModel();

        var prevArmPose = model.rightArmPose;
        model.rightArmPose = RingCastArmPose.RING_CAST.getValue();

        float headPitchRad = model.head.xRot;
        float xRot = ((-(float) Math.PI / 3f) + headPitchRad * 0.75f - 0.6f);
        float yRot = model.head.yRot;

        model.rightArm.xRot = xRot;
        model.rightArm.yRot = yRot;
        model.rightArm.visible = true;
        model.rightSleeve.xRot = xRot;
        model.rightSleeve.yRot = yRot;
        model.rightSleeve.visible = true;

        var skin = entity.getSkin().texture();
        VertexConsumer armConsumer = buffer.getBuffer(RenderType.entitySolid(skin));
        model.rightArm.render(poseStack, armConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        VertexConsumer sleeveConsumer = buffer.getBuffer(RenderType.entityTranslucent(skin));
        model.rightSleeve.render(poseStack, sleeveConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        model.rightArm.visible = false;
        model.rightSleeve.visible = false;
        model.rightArmPose = prevArmPose;
    }
}
