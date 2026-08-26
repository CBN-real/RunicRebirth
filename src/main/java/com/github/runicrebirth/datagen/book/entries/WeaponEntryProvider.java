package com.github.runicrebirth.datagen.book.entries;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class WeaponEntryProvider extends EntryProvider {

    private final String id;
    private final String name;
    private final ItemLike item;
    private final ResourceLocation recipeId;

    public WeaponEntryProvider(CategoryProvider parent, String id, String name,
                               ItemLike item, ResourceLocation recipeId) {
        super(parent);
        this.id = id;
        this.name = name;
        this.item = item;
        this.recipeId = recipeId;
    }

    @Override
    protected void generatePages() {
        this.page("display", () -> BookSpotlightPageModel.create()
            .withItem(item)
            .withTitle(name));

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
            .withRecipeId1(recipeId));
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
    protected Pair<Integer, Integer> entryBackground() {
        return Pair.of(0, 0);
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
