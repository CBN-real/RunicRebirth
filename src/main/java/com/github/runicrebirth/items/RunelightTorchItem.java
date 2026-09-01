package com.github.runicrebirth.items;

import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class RunelightTorchItem extends StandingAndWallBlockItem implements GeoItem {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RunelightTorchItem(Block floorBlock, Block wallBlock, Properties properties) {
        super(floorBlock, wallBlock, net.minecraft.core.Direction.DOWN, properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<RunelightTorchItem>("item", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private com.github.runicrebirth.client.renderers.blocks.RunelightTorchItemRenderer renderer;

            @Override
            public com.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new com.github.runicrebirth.client.renderers.blocks.RunelightTorchItemRenderer(
                        new com.github.runicrebirth.client.renderers.blocks.RunicBlockItemModel<>(
                            "block/runelight_torch",
                            "textures/entity/runic_templates/arcane_runic_template.png",
                            "block/runelight_torch"
                        )
                    );
                }
                return renderer;
            }
        });
    }
}
