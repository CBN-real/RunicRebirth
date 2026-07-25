package com.github.runicrebirth.client;

import com.github.runicrebirth.client.drawing.DrawingCanvasScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

public class SpellWriterClientExtensions implements IClientItemExtensions {

    public static boolean currentEntityCasting = false;

    @Nullable
    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        boolean casting;
        if (entity == Minecraft.getInstance().player) {
            boolean drawing = Minecraft.getInstance().screen instanceof DrawingCanvasScreen;
            casting = drawing || ClientMagicData.isCastAnimActive();
        } else {
            casting = ClientMagicData.isCastAnimActiveFor(entity.getId());
        }
        currentEntityCasting = casting;
        if (casting) return SpellWriterArmPose.CASTING.getValue();
        return null;
    }
}
