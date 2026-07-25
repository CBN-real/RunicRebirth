package com.github.runicrebirth.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class SpellWriterArmPose {
    public static final EnumProxy<HumanoidModel.ArmPose> CASTING = new EnumProxy<>(
        HumanoidModel.ArmPose.class, false,
        (IArmPoseTransformer) (model, entity, arm) -> {
            var armPart = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
            float headPitch = model.head.xRot;
            armPart.xRot = (-(float) Math.PI / 3f) + headPitch * 0.75f - 1f;
            armPart.yRot = arm == HumanoidArm.RIGHT ? -0.15f : 0.15f;
        }
    );
}
