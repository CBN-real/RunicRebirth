package com.github.runicrebirth.client.animations;

import com.github.runicrebirth.RunicRebirth;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

public class MeditationAnimLayer {

    public static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "meditation_layer");

    public static final ResourceLocation MEDITATE_ANIM_ID =
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "meditate");

    public static void register() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                LAYER_ID,
                2000,
                player -> {
                    PlayerAnimationController controller = new PlayerAnimationController(player,
                            (c, state, setter) -> PlayState.STOP
                    );
                    controller.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
                    controller.setFirstPersonConfiguration(new FirstPersonConfiguration()
                            .setShowRightArm(true)
                            .setShowLeftArm(true)
                            .setShowRightItem(false)
                            .setShowLeftItem(false)
                    );
                    return controller;
                }
        );
    }

    public static void trigger(AbstractClientPlayer player) {
        IAnimation layer = PlayerAnimationAccess.getPlayerAnimationLayer(player, LAYER_ID);
        if (layer instanceof PlayerAnimationController controller) {
            controller.triggerAnimation(MEDITATE_ANIM_ID);
        }
    }

    public static void stop(AbstractClientPlayer player) {
        IAnimation layer = PlayerAnimationAccess.getPlayerAnimationLayer(player, LAYER_ID);
        if (layer instanceof PlayerAnimationController controller) {
            controller.stopTriggeredAnimation();
        }
    }
}
