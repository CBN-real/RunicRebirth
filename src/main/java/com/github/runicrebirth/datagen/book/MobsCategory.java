package com.github.runicrebirth.datagen.book;

import com.github.runicrebirth.datagen.book.entries.MobEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import net.minecraft.world.item.Items;

public class MobsCategory extends CategoryProvider {

    public static final String ID = "mobs";

    public MobsCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
            "__A__B__",
            "__C__D__",
            "__E_____",
        };
    }

    @Override
    protected void generateEntries() {
        this.add(new MobEntryProvider(this,
            "runesteel_golem", "Runesteel Golem",
            80, "runicrebirth:runesteel_golem", 0.6f
        ).generate('A'));

        this.add(new MobEntryProvider(this,
            "zombified_runeblade_acolyte", "Zombified Runeblade Acolyte",
            20, "runicrebirth:zombified_runeblade_acolyte", 1.0f
        ).generate('B'));

        this.add(new MobEntryProvider(this,
            "skeletal_mage_acolyte", "Skeletal Mage Acolyte",
            18, "runicrebirth:skeletal_mage_acolyte", 1.0f
        ).generate('C'));

        this.add(new MobEntryProvider(this,
            "skeletal_wizard_acolyte", "Skeletal Wizard Acolyte",
            20, "runicrebirth:skeletal_wizard_acolyte", 1.0f
        ).generate('D'));

        this.add(new MobEntryProvider(this,
            "zombified_artificer_acolyte", "Zombified Artificer Acolyte",
            24, "runicrebirth:zombified_artificer_acolyte", 1.0f
        ).generate('E'));
    }

    @Override
    protected String categoryName() {
        return "Mobs";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.BONE);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
