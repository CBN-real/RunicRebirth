package com.github.runicrebirth.datagen;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.datagen.book.RunicCodexBookProvider;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.NeoBookProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = RunicRebirth.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class DataGenHandler {

    private DataGenHandler() {}

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var langCache = new LanguageProviderCache("en_us");

        generator.addProvider(event.includeServer(),
            NeoBookProvider.of(event, new RunicCodexBookProvider(RunicRebirth.MODID, langCache)));

        generator.addProvider(event.includeClient(),
            new RunicRebirthLangProvider(generator.getPackOutput(), langCache));
    }
}
