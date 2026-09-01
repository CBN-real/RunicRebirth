package com.github.runicrebirth.datagen.book;

import com.github.runicrebirth.datagen.book.entries.MilestoneEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class FoundationCategory extends CategoryProvider {

    public static final String ID = "foundation";

    public FoundationCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
            "___i___",
            "_______",
            "___a___",
            "_______",
            "___r___",
        };
    }

    @Override
    protected void generateEntries() {
        var intro = this.add(new IntroductionEntry(this).generate('i'));

        var adeptAscension = this.add(new MilestoneEntryProvider(
            this, "adept_ascension", "Adept Ascension",
            "You have mastered the basics of spellcasting. "
            + "New spell forms and modifiers are now available to you. "
            + "Seek out the intermediate spells in the **Spells** category."
        ).generate('a'));
        adeptAscension.withParent(this.parent(intro));

        var archAscension = this.add(new MilestoneEntryProvider(
            this, "arch_ascension", "Arch Ascension",
            "Your mastery of the arcane arts has reached its peak. "
            + "The most powerful spell forms are now within your grasp. "
            + "Seek out the advanced spells in the **Spells** category."
        ).generate('r'));
        archAscension.withParent(this.parent(adeptAscension));
    }

    @Override
    protected String categoryName() {
        return "Foundation";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.BOOK);
    }

    @Override
    public String categoryId() {
        return ID;
    }

    private static class IntroductionEntry extends EntryProvider {

        public IntroductionEntry(CategoryProvider parent) {
            super(parent);
        }

        @Override
        protected void generatePages() {
            this.page("welcome", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
            this.pageTitle("Welcome");
            this.pageText(
                "Zelose was here"
            );

            this.page("systems", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
            this.pageTitle("How Magic Works");
            this.pageText(
                "Hi"
            );
        }

        @Override
        protected String entryName() {
            return "Introduction";
        }

        @Override
        protected String entryDescription() {
            return "An introduction to the Runic Codex.";
        }

        @Override
        protected com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite entryBackground() {
            return com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite.EMPTY;
        }

        @Override
        protected BookIconModel entryIcon() {
            return BookIconModel.create(Items.WRITABLE_BOOK);
        }

        @Override
        protected String entryId() {
            return "introduction";
        }
    }
}
