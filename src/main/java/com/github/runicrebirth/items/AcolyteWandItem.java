package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicWand;
import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.client.drawing.DrawingCanvasScreen;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.SingletonGeoAnimatable;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import net.minecraft.world.item.ItemDisplayContext;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;

public class AcolyteWandItem extends SpellWriter implements GeoItem, IMagicWand {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle_animation");
    private static final RawAnimation CASTING_FP = RawAnimation.begin().thenLoop("casting_animation");
    private static final RawAnimation CASTING_TP = RawAnimation.begin().thenLoop("casting_animation_tp");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AcolyteWandItem(Properties properties) {
        super(properties.stacksTo(1));
    }

  @Override
  public boolean isPerspectiveAware() {
    return true;
  }

  @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
      controllers.add(new AnimationController<AcolyteWandItem>("controller", 5, state -> {
        var perspective = state.getData(com.geckolib.constant.DataTickets.ITEM_RENDER_PERSPECTIVE);
        if (perspective == net.minecraft.world.item.ItemDisplayContext.GUI
            || perspective == net.minecraft.world.item.ItemDisplayContext.GROUND
            || perspective == net.minecraft.world.item.ItemDisplayContext.FIXED) {
          state.setAnimation(IDLE);
          return PlayState.CONTINUE;
        }
        LivingEntity holder = Minecraft.getInstance().player;
        boolean isLocal = holder != null && holder == Minecraft.getInstance().player;
        net.minecraft.world.InteractionHand hand = (perspective == net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_LEFT_HAND
            || perspective == net.minecraft.world.item.ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
            ? net.minecraft.world.InteractionHand.OFF_HAND : net.minecraft.world.InteractionHand.MAIN_HAND;
        boolean drawing = isLocal && hand == net.minecraft.world.InteractionHand.MAIN_HAND
            && Minecraft.getInstance().screen instanceof DrawingCanvasScreen;
        boolean localCasting = isLocal && ClientMagicData.isCastAnimActive(hand);
        boolean remoteCasting = !isLocal && holder != null && ClientMagicData.isCastAnimActiveFor(holder.getId(), hand);
        boolean casting = drawing || localCasting || remoteCasting;
        boolean firstPerson = isLocal && Minecraft.getInstance().options.getCameraType().isFirstPerson();
        state.setAnimation(casting ? (firstPerson ? CASTING_FP : CASTING_TP) : IDLE);
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
            private GeoItemRenderer<AcolyteWandItem> renderer;

            @Override
            public com.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new com.github.runicrebirth.client.renderers.items.AcolyteWandRenderer();
                }
                return this.renderer;
            }
        });
    }
}
