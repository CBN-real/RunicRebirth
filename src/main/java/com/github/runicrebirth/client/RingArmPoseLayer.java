package com.github.runicrebirth.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public class RingArmPoseLayer extends RenderLayer<AvatarRenderState, PlayerModel> {

    public RingArmPoseLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords,
                       AvatarRenderState state, float yRot, float xRot) {
        boolean ringActive = Minecraft.getInstance().player != null && Minecraft.getInstance().player.getId() == state.id
            ? ClientMagicData.isRingCastAnimActive()
            : ClientMagicData.isRingCastAnimActiveFor(state.id);

        HumanoidModel.ArmPose castingPose = SpellWriterArmPose.CASTING.getValue();
        HumanoidModel.ArmPose ringPose = RingCastArmPose.RING_CAST.getValue();
        PlayerModel model = this.getParentModel();

        if (ringActive) {
            state.rightArmPose = ringPose;
            model.setupAnim(state);
            float headPitch = model.head.xRot;
            model.rightArm.xRot = (-(float) Math.PI / 3f) + headPitch * 0.75f - 0.6f;
            model.rightArm.yRot = model.head.yRot;
            return;
        }

        boolean rightCasting = state.rightArmPose == castingPose;
        boolean leftCasting = state.leftArmPose == castingPose;
        if (!rightCasting && !leftCasting) return;

        float headPitch = model.head.xRot;
        if (rightCasting) {
            model.rightArm.xRot = (-(float) Math.PI / 3f) + headPitch * 0.75f - 1f;
            model.rightArm.yRot = -0.15f;
        }
        if (leftCasting) {
            model.leftArm.xRot = (-(float) Math.PI / 3f) + headPitch * 0.75f - 1f;
            model.leftArm.yRot = 0.15f;
        }
    }
}
