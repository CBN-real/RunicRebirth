package com.github.runicrebirth.client;

import net.minecraft.client.model.HumanoidModel;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class SpellWriterArmPose {
    public static final EnumProxy<HumanoidModel.ArmPose> CASTING = new EnumProxy<>(
        HumanoidModel.ArmPose.class, false, false, (IArmPoseTransformer) (model, state, arm) -> {}
    );
}
