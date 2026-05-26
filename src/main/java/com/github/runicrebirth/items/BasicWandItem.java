package com.github.runicrebirth.items;

import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.client.drawing.DrawingCanvasScreen;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Entry-level wand. Extends SpellWriter for canvas + stack behavior.
 * Casting animation plays while the local player has the DrawingCanvasScreen open OR is within
 * the post-cast animation window (tracked via ClientMagicData.isCastAnimActive, which triggers
 * on valid→empty active stack transitions).
 */
public class BasicWandItem extends SpellWriter implements GeoItem {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_animation");
    private static final RawAnimation CASTING = RawAnimation.begin().thenPlayAndHold("casting_animation");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BasicWandItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {
            Entity raw = state.getData(software.bernie.geckolib.constant.DataTickets.ENTITY);
            LivingEntity holder = raw instanceof LivingEntity le ? le : Minecraft.getInstance().player;
            boolean isLocal = holder != null && holder == Minecraft.getInstance().player;
            boolean drawing = isLocal && Minecraft.getInstance().screen instanceof DrawingCanvasScreen;
            boolean castingPost = isLocal && ClientMagicData.isCastAnimActive();
            boolean casting = drawing || castingPost;
            state.setAnimation(casting ? CASTING : IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<BasicWandItem> renderer;

            @Override
            @OnlyIn(Dist.CLIENT)
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new com.github.runicrebirth.client.renderers.items.BasicWandRenderer();
                }
                return this.renderer;
            }
        });
    }
}
