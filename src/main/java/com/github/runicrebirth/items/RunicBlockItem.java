package com.github.runicrebirth.items;

import java.util.function.Consumer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

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
    private String geoPath;
    private String animationPath;

  public RunicBlockItem(Block block, Properties properties, String animationName, String blockName) {
        super(block, properties);
        this.defaultAnimation = RawAnimation.begin().thenLoop(animationName);
        this.blockName = blockName;
    }

    public RunicBlockItem withTexture(String texturePath) {
        this.texturePath = texturePath;
        return this;
    }

    public RunicBlockItem withGeoPath(String geoPath) {
        this.geoPath = geoPath;
        return this;
    }

    public RunicBlockItem withAnimationPath(String animationPath) {
        this.animationPath = animationPath;
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
        controllers.add(new AnimationController<RunicBlockItem>("item", 0,
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
            public com.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null) {
                    String tex = texturePath != null ? texturePath : "textures/block/" + blockName + ".png";
                    String geo = geoPath != null ? geoPath : "block/" + blockName;
                    String anim = animationPath != null ? animationPath : "block/" + blockName;
                    this.renderer = new com.github.runicrebirth.client.renderers.blocks.RunicBlockItemRenderer(
                        new com.github.runicrebirth.client.renderers.blocks.RunicBlockItemModel<>(
                            geo,
                            tex,
                            anim));
                }
                return this.renderer;
            }
        });
    }
}
