package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.data.BookDataManager;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

public class RunicCodexItem extends Item implements GeoItem {

    public static final ResourceLocation BOOK_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "runic_codex");

    private static final RawAnimation OPEN_AND_HOLD =
        RawAnimation.begin().thenPlay("open_codex").thenLoop("hold_codex");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @OnlyIn(Dist.CLIENT)
    private static long lastRenderTick = -1;

    public RunicCodexItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            var book = BookDataManager.get().getBook(BOOK_ID);
            if (book != null) {
                BookGuiManager.get().openBook(BookAddress.defaultFor(BOOK_ID));
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {
            long currentTick = Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.getGameTime() : 0;

            if (lastRenderTick >= 0 && currentTick - lastRenderTick > 20) {
                state.getController().forceAnimationReset();
            }
            lastRenderTick = currentTick;

            state.setAnimation(OPEN_AND_HOLD);
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
            private GeoItemRenderer<RunicCodexItem> renderer;

            @Override
            @OnlyIn(Dist.CLIENT)
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new com.github.runicrebirth.client.renderers.items.RunicCodexRenderer();
                }
                return this.renderer;
            }
        });
    }
}
