package com.github.runicrebirth.datagen.book;

import com.github.runicrebirth.datagen.book.entries.SpotlightEntryProvider;
import com.github.runicrebirth.init.ModItems;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;

public class SpellRingsCategory extends CategoryProvider {

    public static final String ID = "spell_rings";

    public SpellRingsCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
            "__A__B__C",
            "__D__E__F",
            "__G__H__I",
        };
    }

    @Override
    protected void generateEntries() {
        this.add(new SpotlightEntryProvider(this,
            "ring_of_expansion", "Ring of Expansion",
            ModItems.RING_OF_EXPANSION.get()
        ).generate('A'));

        this.add(new SpotlightEntryProvider(this,
            "arcane_acolyte_ring", "Arcane Acolyte Ring",
            ModItems.ARCANE_ACOLYTE_RING.get()
        ).generate('B'));

        this.add(new SpotlightEntryProvider(this,
            "arcane_tether_ring", "Arcane Tether Ring",
            ModItems.ARCANE_TETHER_RING.get()
        ).generate('C'));

        this.add(new SpotlightEntryProvider(this,
            "magic_hand_ring", "Magic Hand Ring",
            ModItems.MAGIC_HAND_RING.get()
        ).generate('D'));

        this.add(new SpotlightEntryProvider(this,
            "ring_of_leaping_gales", "Ring of Leaping Gales",
            ModItems.RING_OF_LEAPING_GALES.get()
        ).generate('E'));

        this.add(new SpotlightEntryProvider(this,
            "ring_of_phantom_mining", "Ring of Phantom Mining",
            ModItems.RING_OF_PHANTOM_MINING.get()
        ).generate('F'));

        this.add(new SpotlightEntryProvider(this,
            "blink_ring", "Blink Ring",
            ModItems.BLINK_RING.get()
        ).generate('G'));

        this.add(new SpotlightEntryProvider(this,
            "thruster_ring", "Thruster Ring",
            ModItems.THRUSTER_RING.get()
        ).generate('H'));

        this.add(new SpotlightEntryProvider(this,
            "hover_ring", "Hover Ring",
            ModItems.HOVER_RING.get()
        ).generate('I'));
    }

    @Override
    protected String categoryName() {
        return "Spell Rings";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(ModItems.RING_OF_EXPANSION.get());
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
