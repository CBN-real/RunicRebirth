package com.github.runicrebirth.datagen.book;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.datagen.book.entries.WeaponEntryProvider;
import com.github.runicrebirth.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import net.minecraft.resources.Identifier;

public class WeaponsCategory extends CategoryProvider {

    public static final String ID = "weapons";

    public WeaponsCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
            "__A__B__",
            "__C__D__",
        };
    }

    @Override
    protected void generateEntries() {
        this.add(new WeaponEntryProvider(this,
            "basic_runic_longsword", "Basic Runic Longsword",
            ModItems.BASIC_RUNIC_LONGSWORD.get(),
            rl("basic_runic_longsword")
        ).generate('A'));

        this.add(new WeaponEntryProvider(this,
            "runic_dagger", "Runic Dagger",
            ModItems.RUNIC_DAGGER.get(),
            rl("runic_dagger")
        ).generate('B'));

        this.add(new WeaponEntryProvider(this,
            "runic_warstaff", "Runic Warstaff",
            ModItems.RUNIC_WARSTAFF.get(),
            rl("runic_warstaff")
        ).generate('C'));

        this.add(new WeaponEntryProvider(this,
            "runic_shield", "Runic Shield",
            ModItems.RUNIC_SHIELD.get(),
            rl("runic_shield")
        ).generate('D'));
    }

    private static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(RunicRebirth.MODID, path);
    }

    @Override
    protected String categoryName() {
        return "Weapons";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(ModItems.BASIC_RUNIC_LONGSWORD.get());
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
