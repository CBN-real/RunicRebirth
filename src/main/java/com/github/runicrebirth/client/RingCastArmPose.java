package com.github.runicrebirth.client;

import net.minecraft.client.model.HumanoidModel;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class RingCastArmPose {
    public static final EnumProxy<HumanoidModel.ArmPose> RING_CAST = new EnumProxy<>(
        HumanoidModel.ArmPose.class, false, false, (IArmPoseTransformer) (model, state, arm) -> {}
    );
}
