package com.github.runicrebirth.datagen;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.compat.modonomicon.ModonomiconCompat;
import com.github.runicrebirth.datagen.book.RunicCodexBookProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.NeoBookProvider;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class DataGenHandler {

    private DataGenHandler() {}

    public static void onGatherDataClient(GatherDataEvent.Client event) {
        gatherData(event);
    }

    public static void onGatherDataServer(GatherDataEvent.Server event) {
        gatherData(event);
    }

    private static void gatherData(GatherDataEvent event) {
        // FMLCommonSetupEvent does not fire in datagen mode; register custom page/condition types here.
        ModonomiconCompat.registerPageLoaders();
        var langCache = new LanguageProviderCache("en_us");
        var researchCache = new ResearchCache();

        event.addProvider(NeoBookProvider.of(event, langCache, researchCache, new RunicCodexBookProvider(RunicRebirth.MODID)));
        event.addProvider(new RunicRebirthLangProvider(event.getGenerator().getPackOutput(), langCache));
    }
}
