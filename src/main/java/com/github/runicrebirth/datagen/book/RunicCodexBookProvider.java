package com.github.runicrebirth.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class RunicCodexBookProvider extends SingleBookSubProvider {

    public static final String BOOK_ID = "runic_codex";

    public RunicCodexBookProvider(String modId, BiConsumer<String, String> lang) {
        super(BOOK_ID, modId, lang);
    }

    @Override
    protected void registerDefaultMacros() {
    }

    @Override
    protected void generateCategories() {
        this.add(new FoundationCategory(this).generate());
        this.add(new SpellsCategory(this).generate());
    }

    @Override
    protected String bookName() {
        return "Runic Codex";
    }

    @Override
    protected String bookTooltip() {
        return "A comprehensive guide to Runic Rebirth";
    }

    @Override
    protected BookModel additionalSetup(BookModel book) {
        return book.withGenerateBookItem(false)
            .withAllowOpenBooksWithInvalidLinks(true);
            //.withBookContentTexture(ResourceLocation.fromNamespaceAndPath("runicrebirth", "textures/gui/book_content.png"));
    }
}
