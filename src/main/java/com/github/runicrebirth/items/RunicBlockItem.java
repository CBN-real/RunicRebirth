package com.github.runicrebirth.items;

import java.util.function.Consumer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RunicBlockItem extends BlockItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation defaultAnimation;
    private final String blockName;
    private String texturePath;
    private float handRotationY = 0;
    private float guiTranslationX = 0;
    private float guiTranslationY = 0;
    private float guiTranslationZ = 0;
    private float guiScale = 1;

    public RunicBlockItem(Block block, Properties properties, String animationName, String blockName) {
        super(block, properties);
        this.defaultAnimation = RawAnimation.begin().thenLoop(animationName);
        this.blockName = blockName;
    }

    public RunicBlockItem withTexture(String texturePath) {
        this.texturePath = texturePath;
        return this;
    }

    public RunicBlockItem withHandRotationY(float degrees) {
        this.handRotationY = degrees;
        return this;
    }

    public float getHandRotationY() {
        return handRotationY;
    }

    public RunicBlockItem withGuiTranslation(float x, float y, float z) {
        this.guiTranslationX = x;
        this.guiTranslationY = y;
        this.guiTranslationZ = z;
        return this;
    }

    public float getGuiTranslationX() { return guiTranslationX; }
    public float getGuiTranslationY() { return guiTranslationY; }
    public float getGuiTranslationZ() { return guiTranslationZ; }

    public RunicBlockItem withGuiScale(float scale) {
        this.guiScale = scale;
        return this;
    }

    public float getGuiScale() { return guiScale; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "item", 0,
            state -> state.setAndContinue(defaultAnimation)));
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private com.github.runicrebirth.client.renderers.blocks.RunicBlockItemRenderer renderer;

            @Override
            @OnlyIn(Dist.CLIENT)
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    String tex = texturePath != null ? texturePath : "textures/block/" + blockName + ".png";
                    this.renderer = new com.github.runicrebirth.client.renderers.blocks.RunicBlockItemRenderer(
                        new com.github.runicrebirth.client.renderers.blocks.RunicBlockItemModel<>(
                            "geo/block/" + blockName + ".geo.json",
                            tex,
                            "animations/block/" + blockName + ".animation.json"));
                }
                return this.renderer;
            }
        });
    }
}
