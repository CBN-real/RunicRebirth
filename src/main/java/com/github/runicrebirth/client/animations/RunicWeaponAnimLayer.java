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

public class RunicWeaponAnimLayer {

    public static final ResourceLocation LAYER_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "runic_weapon_layer");

    public static final ResourceLocation SWORD_SWING_ANIM_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "sword_swing");
    public static final ResourceLocation DAGGER_THROW_ANIM_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "dagger_throw");
    public static final ResourceLocation WARSTAFF_SPIN_ANIM_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "warstaff_spin");

    public static void register() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
            LAYER_ID,
            1500,
            player -> {
                PlayerAnimationController controller = new PlayerAnimationController(player,
                    (c, state, animSetter) -> PlayState.STOP
                );
                controller.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
                controller.setFirstPersonConfiguration(new FirstPersonConfiguration()
                    .setShowRightArm(true)
                    .setShowLeftArm(true)
                    .setShowRightItem(true)
                    .setShowLeftItem(false)
                );
                return controller;
            }
        );
    }

    public static void trigger(AbstractClientPlayer player, ResourceLocation animId) {
        IAnimation layer = PlayerAnimationAccess.getPlayerAnimationLayer(player, LAYER_ID);
        if (layer instanceof PlayerAnimationController controller) {
            controller.triggerAnimation(animId);
        }
    }
}
