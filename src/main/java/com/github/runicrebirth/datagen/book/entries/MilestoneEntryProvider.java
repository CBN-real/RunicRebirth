package com.github.runicrebirth.datagen.book.entries;

import com.github.runicrebirth.datagen.book.page.SpellPageModel;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEntityPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class MilestoneEntryProvider extends EntryProvider {

    private final String id;
    private final String name;
    private final String text;
    private final String entityId;
    private final float entityScale;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;

    public MilestoneEntryProvider(CategoryProvider parent, String id, String name, String text) {
        this(parent, id, name, text, null, 0f, 0f, 0f, 0f);
    }

    public MilestoneEntryProvider(CategoryProvider parent, String id, String name, String text,
                                  String entityId, float entityScale,
                                  float offsetX, float offsetY, float offsetZ) {
        super(parent);
        this.id = id;
        this.name = name;
        this.text = text;
        this.entityId = entityId;
        this.entityScale = entityScale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    protected void generatePages() {
        if (entityId != null) {
            this.page("stats", () -> SpellPageModel.create()
                .withSpellName(name)
                .withHideStats(true)
                .withOffsetX(offsetX)
                .withOffsetY(offsetY)
                .withOffsetZ(offsetZ));

            this.page("entity", () -> BookEntityPageModel.create()
                .withEntityId(entityId)
                .withScale(entityScale)
                .withRotate(false)
                .withDefaultRotation(-45f)
                .withText(this.context().pageText()));
            this.pageText(text);
        } else {
            this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
            this.pageTitle(name);
            this.pageText(text);
        }
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
        return Pair.of(0, 1);
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.NETHER_STAR);
    }

    @Override
    protected String entryId() {
        return id;
    }
}
