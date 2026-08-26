package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ShieldItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class RunicShieldItem extends ShieldItem implements GeoItem, IMagicWeapon {

    public static final ResourceLocation COOLDOWN_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "runic_shield");
    public static final int COOLDOWN_TICKS = 100;

    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RunicShieldItem(Properties properties) {
        super(properties.stacksTo(1).durability(336));
    }

    @Override
    public void activate(ServerPlayer player) {}

    @Override
    public ResourceLocation getWeaponCooldownId() { return COOLDOWN_ID; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5,
            state -> { state.setAnimation(ANIM_IDLE); return PlayState.CONTINUE; }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private com.github.runicrebirth.client.renderers.items.RunicShieldRenderer renderer;

            @Override
            @OnlyIn(Dist.CLIENT)
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new com.github.runicrebirth.client.renderers.items.RunicShieldRenderer();
                }
                return renderer;
            }
        });
    }
}
