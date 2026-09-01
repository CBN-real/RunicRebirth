package com.github.runicrebirth.datagen.book.entries;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.ItemLike;

public class SpotlightEntryProvider extends EntryProvider {

    private final String id;
    private final String name;
    private final ItemLike item;

    public SpotlightEntryProvider(CategoryProvider parent, String id, String name, ItemLike item) {
        super(parent);
        this.id = id;
        this.name = name;
        this.item = item;
    }

    @Override
    protected void generatePages() {
        this.page("display", () -> BookSpotlightPageModel.create()
            .withItem(item)
            .withTitle(name));
    }

    @Override
    protected String entryName() {
        return name;
    }

    @Override
    protected String entryDescription() {
        return "";
    }

    @Override
    protected com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite entryBackground() {
        return com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite.EMPTY;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(item);
    }

    @Override
    protected String entryId() {
        return id;
    }
}
