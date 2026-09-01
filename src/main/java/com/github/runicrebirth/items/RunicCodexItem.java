package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookAddress;
import com.klikli_dev.modonomicon.data.BookDataManager;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;

public class RunicCodexItem extends Item implements GeoItem {

    public static final Identifier BOOK_ID =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "runic_codex");

    private static final RawAnimation OPEN_AND_HOLD =
        RawAnimation.begin().thenPlay("open_codex").thenLoop("hold_codex");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static long lastRenderTick = -1;

    public RunicCodexItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            var book = BookDataManager.get().getBook(BOOK_ID);
            if (book != null) {
                BookGuiManager.get().openBook(BookAddress.defaultFor(BOOK_ID));
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<RunicCodexItem>("controller", 5, state -> {
            long currentTick = Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.getGameTime() : 0;

            if (lastRenderTick >= 0 && currentTick - lastRenderTick > 20) {
                state.controller().reset();
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
            public com.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new com.github.runicrebirth.client.renderers.items.RunicCodexRenderer();
                }
                return this.renderer;
            }
        });
    }
}
