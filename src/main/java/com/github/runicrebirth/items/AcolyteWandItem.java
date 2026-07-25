package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.client.drawing.DrawingCanvasScreen;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AcolyteWandItem extends SpellWriter implements GeoItem {

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
      controllers.add(new AnimationController<>(this, "controller", 5, state -> {
        var perspective = state.getData(software.bernie.geckolib.constant.DataTickets.ITEM_RENDER_PERSPECTIVE);
        if (perspective == net.minecraft.world.item.ItemDisplayContext.GUI
            || perspective == net.minecraft.world.item.ItemDisplayContext.GROUND
            || perspective == net.minecraft.world.item.ItemDisplayContext.FIXED) {
          state.setAnimation(IDLE);
          return PlayState.CONTINUE;
        }
        Entity raw = state.getData(software.bernie.geckolib.constant.DataTickets.ENTITY);
        LivingEntity holder = raw instanceof LivingEntity le ? le : Minecraft.getInstance().player;
        boolean isLocal = holder != null && holder == Minecraft.getInstance().player;
        boolean drawing = isLocal && Minecraft.getInstance().screen instanceof DrawingCanvasScreen;
        boolean localCasting = isLocal && ClientMagicData.isCastAnimActive();
        boolean remoteCasting = !isLocal && holder != null && ClientMagicData.isCastAnimActiveFor(holder.getId());
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
            @OnlyIn(Dist.CLIENT)
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new com.github.runicrebirth.client.renderers.items.AcolyteWandRenderer();
                }
                return this.renderer;
            }
        });
    }
}
