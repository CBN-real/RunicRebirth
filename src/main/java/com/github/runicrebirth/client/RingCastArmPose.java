package com.github.runicrebirth.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class RingCastArmPose {
    public static final EnumProxy<HumanoidModel.ArmPose> RING_CAST = new EnumProxy<>(
        HumanoidModel.ArmPose.class, false,
        (IArmPoseTransformer) (model, entity, arm) -> {
            if (arm != HumanoidArm.RIGHT) return;
            float headPitch = model.head.xRot;
            model.rightArm.xRot = (-(float) Math.PI / 3f) + headPitch * 0.75f - 0.6f;
            model.rightArm.yRot = model.head.yRot;
        }
    );
}
