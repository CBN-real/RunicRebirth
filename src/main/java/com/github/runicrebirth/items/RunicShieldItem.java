package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicWeapon;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ShieldItem;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class RunicShieldItem extends ShieldItem implements GeoItem, IMagicWeapon {

    public static final Identifier COOLDOWN_ID =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "runic_shield");
    public static final int COOLDOWN_TICKS = 100;

    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RunicShieldItem(Properties properties) {
        super(properties.stacksTo(1).durability(336));
    }

    @Override
    public void activate(ServerPlayer player) {}

    @Override
    public Identifier getWeaponCooldownId() { return COOLDOWN_ID; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<RunicShieldItem>("controller", 5,
            state -> { state.setAnimation(ANIM_IDLE); return PlayState.CONTINUE; }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private com.github.runicrebirth.client.renderers.items.RunicShieldRenderer renderer;

            @Override
            public com.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new com.github.runicrebirth.client.renderers.items.RunicShieldRenderer();
                }
                return renderer;
            }
        });
    }
}
