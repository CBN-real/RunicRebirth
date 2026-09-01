package com.github.runicrebirth.datagen.book;

import com.github.runicrebirth.datagen.book.entries.SpotlightEntryProvider;
import com.github.runicrebirth.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEntityPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class DungeonTrapsCategory extends CategoryProvider {

    public static final String ID = "dungeon_traps";

    public DungeonTrapsCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
            "__A__B__",
            "__C__D__",
            "__E__F__",
            "__G_____",
        };
    }

    @Override
    protected void generateEntries() {
        this.add(new SpotlightEntryProvider(this,
            "dungeon_swinging_axe", "Swinging Axe",
            ModItems.DUNGEON_SWINGING_AXE.get()
        ).generate('A'));

        this.add(new SpotlightEntryProvider(this,
            "dungeon_flamethrower", "Flamethrower",
            ModItems.DUNGEON_FLAMETHROWER.get()
        ).generate('B'));

        // Boulder spawner: display the boulder entity instead of the spawner block
        this.add(new EntryProvider(this) {
            @Override
            protected void generatePages() {
                this.page("entity", () -> BookEntityPageModel.create()
                    .withEntityId("runicrebirth:dungeon_boulder")
                    .withScale(1.5f)
                    .withRotate(true)
                    .withDefaultRotation(-20f));
            }
            @Override protected String entryName() { return "Boulder Spawner"; }
            @Override protected String entryDescription() { return ""; }
            @Override protected com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite entryBackground() { return com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite.EMPTY; }
            @Override protected BookIconModel entryIcon() { return BookIconModel.create(ModItems.DUNGEON_BOULDER_SPAWNER.get()); }
            @Override protected String entryId() { return "dungeon_boulder_spawner"; }
        }.generate('C'));

        this.add(new SpotlightEntryProvider(this,
            "dungeon_temporary_platform", "Temporary Platform",
            ModItems.DUNGEON_TEMPORARY_PLATFORM.get()
        ).generate('D'));

        this.add(new SpotlightEntryProvider(this,
            "dungeon_spike", "Spike",
            ModItems.DUNGEON_SPIKE.get()
        ).generate('E'));

        this.add(new SpotlightEntryProvider(this,
            "crumbling_platform", "Crumbling Platform",
            ModItems.CRUMBLING_PLATFORM.get()
        ).generate('F'));

        this.add(new SpotlightEntryProvider(this,
            "dungeon_pressure_plate", "Pressure Plate Trap",
            ModItems.DUNGEON_PRESSURE_PLATE.get()
        ).generate('G'));
    }

    @Override
    protected String categoryName() {
        return "Dungeon Traps";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.IRON_SWORD);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
