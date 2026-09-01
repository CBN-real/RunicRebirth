package com.github.runicrebirth.datagen.book.entries;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEntityPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class MobEntryProvider extends EntryProvider {

    private final String id;
    private final String name;
    private final int hitPoints;
    private final String entityId;
    private final float scale;

    public MobEntryProvider(CategoryProvider parent, String id, String name,
                            int hitPoints, String entityId, float scale) {
        super(parent);
        this.id = id;
        this.name = name;
        this.hitPoints = hitPoints;
        this.entityId = entityId;
        this.scale = scale;
    }

    @Override
    protected void generatePages() {
        this.page("info", () -> BookTextPageModel.create()
            .withTitle(this.context().pageTitle())
            .withText(this.context().pageText()));
        this.pageTitle(name);
        this.pageText("Hit Points: " + hitPoints);

        this.page("entity", () -> BookEntityPageModel.create()
            .withEntityId(entityId)
            .withScale(scale)
            .withRotate(true)
            .withDefaultRotation(-20f));
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
        return BookIconModel.create(Items.BONE);
    }

    @Override
    protected String entryId() {
        return id;
    }
}
