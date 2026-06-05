package com.github.runicrebirth.compat.modonomicon;

import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;
import com.klikli_dev.modonomicon.data.BookConditionJsonLoader;
import com.klikli_dev.modonomicon.data.BookPageJsonLoader;
import com.klikli_dev.modonomicon.data.LoaderRegistry;

public final class ModonomiconCompat {

    private ModonomiconCompat() {}

    public static void registerPageLoaders() {
        LoaderRegistry.registerPageLoader(
            SpellPage.PAGE_TYPE,
            (BookPageJsonLoader<?>) SpellPage::fromJson,
            SpellPage::fromNetwork
        );
        LoaderRegistry.registerConditionLoader(
            SpellUnlockCondition.TYPE,
            (BookConditionJsonLoader<?>) SpellUnlockCondition::fromJson,
            SpellUnlockCondition::fromNetwork
        );
    }

    public static void registerPageRenderers() {
        PageRendererRegistry.registerPageRenderer(
            SpellPage.PAGE_TYPE,
            page -> new SpellPageRenderer((SpellPage) page)
        );
    }
}
