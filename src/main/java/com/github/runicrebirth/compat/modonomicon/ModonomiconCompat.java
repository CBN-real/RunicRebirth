package com.github.runicrebirth.compat.modonomicon;

import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.registry.BookConditionTypeRegistry;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;

public final class ModonomiconCompat {

    private ModonomiconCompat() {}

    public static void registerPageLoaders() {
        if (SpellPage.BOOK_PAGE_TYPE == null) {
            SpellPage.BOOK_PAGE_TYPE = BookPageTypeRegistry.register(SpellPage.PAGE_TYPE, SpellPage.MAP_CODEC, SpellPage.STREAM_CODEC);
        }
        if (SpellUnlockCondition.BOOK_CONDITION_TYPE == null) {
            SpellUnlockCondition.BOOK_CONDITION_TYPE = BookConditionTypeRegistry.register(SpellUnlockCondition.TYPE, SpellUnlockCondition.MAP_CODEC, SpellUnlockCondition.STREAM_CODEC);
        }
    }

    public static void registerPageRenderers() {
        PageRendererRegistry.registerPageRenderer(
            SpellPage.PAGE_TYPE,
            page -> new SpellPageRenderer((SpellPage) page)
        );
    }
}
