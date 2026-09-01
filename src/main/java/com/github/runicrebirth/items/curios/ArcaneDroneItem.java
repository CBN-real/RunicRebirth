package com.github.runicrebirth.items.curios;

import com.github.runicrebirth.api.item.IRunicDrone;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.entities.ArcaneDroneEntity;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.items.MagicItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ArcaneDroneItem extends MagicItem implements ICurioItem, GeoItem, IRunicDrone {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ArcaneDroneItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<ArcaneDroneItem>("controller", 0,
            state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private com.github.runicrebirth.client.renderers.items.ArcaneDroneItemRenderer renderer;

            @Override
            public com.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new com.github.runicrebirth.client.renderers.items.ArcaneDroneItemRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide()) return;
        if (!(slotContext.entity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        MagicData data = MagicData.of(player);
        int existing = data.arcaneDroneEntityId();
        if (existing != -1) {
            Entity e = level.getEntity(existing);
            if (e != null) e.discard();
            data.clearArcaneDroneEntityId();
        }

        Vec3 pos = player.position().add(0, player.getBbHeight() + 0.5, 0);
        ArcaneDroneEntity drone = new ArcaneDroneEntity(ModEntities.ARCANE_DRONE.get(), level);
        drone.setOwner(player.getUUID());
        drone.setPos(pos.x, pos.y, pos.z);
        level.addFreshEntity(drone);
        data.setArcaneDroneEntityId(drone.getId());
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide()) return;
        if (!(slotContext.entity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        MagicData data = MagicData.of(player);
        int id = data.arcaneDroneEntityId();
        if (id != -1) {
            Entity e = level.getEntity(id);
            if (e != null) e.discard();
            data.clearArcaneDroneEntityId();
        }
    }
}
