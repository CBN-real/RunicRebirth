package com.github.runicrebirth.datagen.book.entries;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.Identifier;

// TODO: Verify BookAdvancementConditionModel API unchanged in 26.1.x Modonomicon
public class ModifierEntryProvider extends EntryProvider {

    private final String id;
    private final String name;
    private final String description;
    private final String iconTexture;

    public ModifierEntryProvider(CategoryProvider parent, String id, String name,
                                 String description, String iconTexture) {
        super(parent);
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconTexture = iconTexture;
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
            .withTitle(this.context().pageTitle())
            .withText(this.context().pageText()));
        this.pageTitle(name);
        this.pageText(description);
    }

    @Override
    protected String entryName() {
        return name;
    }

    @Override
    protected String entryDescription() {
        return description;
    }

    @Override
    protected com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite entryBackground() {
        return com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite.EMPTY;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(
            Identifier.fromNamespaceAndPath("runicrebirth", iconTexture), 16, 16);
    }

    @Override
    protected String entryId() {
        return id;
    }
}
