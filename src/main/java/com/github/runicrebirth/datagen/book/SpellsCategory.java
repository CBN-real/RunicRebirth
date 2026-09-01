package com.github.runicrebirth.datagen.book;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.datagen.book.entries.MilestoneEntryProvider;
import com.github.runicrebirth.datagen.book.entries.ModifierEntryProvider;
import com.github.runicrebirth.datagen.book.entries.SpellEntryProvider;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class SpellsCategory extends CategoryProvider {

    public static final String ID = "spells";

    public SpellsCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
            "a_b_c_i_j_k_d_e_f_______g_h__",  // y=0:  modifier row (i,j,k = boost mods)
            "______________________________",
            "__A___L_____E_________I_______________",
            "__B____1____F____2____J_______________",
            "__C_________G_________K_______________",
            "__D_________H_________________________",
            "______________________________",
            "______________________________",
            "______________________________",
        };
    }

    @Override
    protected void generateEntries() {
        // === BASIC MODIFIERS (top row, left cluster) ===
        var modSize = this.add(new ModifierEntryProvider(this,
            "mod_size", "Size +",
            "Increases the size of your spell by a fixed amount. Larger spells hit a wider area.",
            "textures/gui/shape_icons/plus_icon_small.png"
        ).generate('a'));

        var modRange = this.add(new ModifierEntryProvider(this,
            "mod_range", "Range",
            "Extends the range of your spell by 50%%. Projectiles travel faster and further.",
            "textures/gui/shape_icons/range_icon_small.png"
        ).generate('b'));

        var modTwoCasts = this.add(new ModifierEntryProvider(this,
            "mod_two_casts", "Two Casts",
            "Cast your spell twice in quick succession. Doubles your output at the cost of cooldown.",
            "textures/gui/shape_icons/two_casts_icon_small.png"
        ).generate('c'));

        // === DAMAGE TYPE BOOST MODIFIERS (top row, between basic and intermediate) ===
        var modSharpBoost = this.add(new ModifierEntryProvider(this,
            "mod_sharp_boost", "Sharp Boost",
            "Increases damage by 1.5x when applied to sharp-type spells. "
            + "No effect on blunt or spirit spells.",
            "textures/gui/shape_icons/sharp_boost_icon_small.png"
        ).generate('i'));

        var modBluntBoost = this.add(new ModifierEntryProvider(this,
            "mod_blunt_boost", "Blunt Boost",
            "Increases damage by 1.5x when applied to blunt-type spells. "
            + "No effect on sharp or spirit spells.",
            "textures/gui/shape_icons/blunt_boost_icon_small.png"
        ).generate('j'));

        var modMagicBoost = this.add(new ModifierEntryProvider(this,
            "mod_magic_boost", "Magic Boost",
            "Increases damage by 1.5x when applied to spirit-type spells. "
            + "No effect on sharp or blunt spells.",
            "textures/gui/shape_icons/magic_boost_icon_small.png"
        ).generate('k'));

        // === INTERMEDIATE MODIFIERS (top row, middle cluster) ===
        var modSizeTwo = this.add(new ModifierEntryProvider(this,
            "mod_size_two", "Size ++",
            "Doubles the size of your spell. A significant increase in area coverage.",
            "textures/gui/shape_icons/plus_two_icon_small.png"
        ).generate('d'));

        var modCooldown = this.add(new ModifierEntryProvider(this,
            "mod_cooldown", "Cooldown",
            "Halves the cooldown of your spell. Cast more frequently at the same power.",
            "textures/gui/shape_icons/cooldown_icon_small.png"
        ).generate('e'));

        var modCharges = this.add(new ModifierEntryProvider(this,
            "mod_charges", "Charges",
            "Converts multi-cast into a charge system. Store casts and release them on demand. "
            + "Requires a multi-cast modifier.",
            "textures/gui/shape_icons/charges_icon_small.png"
        ).generate('f'));

        // === ADVANCED MODIFIERS (top row, right cluster) ===
        var modSizeFour = this.add(new ModifierEntryProvider(this,
            "mod_size_four", "Size ++++",
            "Triples the size of your spell. Massive area coverage for devastating effect.",
            "textures/gui/shape_icons/plus_four_icon_small.png"
        ).generate('g'));

        var modFourCasts = this.add(new ModifierEntryProvider(this,
            "mod_four_casts", "Four Casts",
            "Cast your spell four times in rapid succession. Overwhelming barrage of arcane force.",
            "textures/gui/shape_icons/four_casts_icon_small.png"
        ).generate('h'));

        // === BASIC SPELLS (vertical column) ===
        var projectile = this.add(new SpellEntryProvider(this,
            "magic_projectile", "Magic Projectile",
            "5", "16", "BLUNT",
            "runicrebirth:magic_projectile",
            "textures/gui/shape_icons/line_icon.png", 2f,
            0.25f, 0.4f, 0f,
            spellAdv("magic_projectile"), "Hold a spell writer for the first time"
        ).generate('A'));

        var infusion = this.add(new SpellEntryProvider(this,
            "infusion", "Infusion",
            "N/A", "N/A", "N/A",
            "runicrebirth:infusion_circle",
            "textures/gui/shape_icons/infusion_icon.png", 0.5f,
            0f, 0f, 0f,
            spellAdv("magic_projectile"), "Hold a spell writer for the first time"
        ).withHideStats(true).generate('L'));
        infusion.withParent(this.parent(projectile));

        var beam = this.add(new SpellEntryProvider(this,
            "magic_beam", "Magic Beam",
            "3", "12", "SPIRIT",
            "runicrebirth:magic_beam",
            "textures/gui/shape_icons/circle_icon.png", 1f,
            -0.2f, 0.55f, 0f,
            spellAdv("magic_beam"), "Kill an entity with Magic Projectile from over 10 blocks away"
        ).generate('B'));
        beam.withParent(this.parent(projectile));

        var blast = this.add(new SpellEntryProvider(this,
            "magic_blast", "Magic Blast",
            "2.5", "Size-based", "BLUNT",
            "runicrebirth:magic_blast",
            "textures/gui/shape_icons/v_icon.png", 0.8f,
            0.2f, 0.65f, 0f,
            spellAdv("magic_blast"), "Kill an entity with magic from less than 5 blocks away"
        ).generate('C'));
        blast.withParent(this.parent(beam));

        var arrow = this.add(new SpellEntryProvider(this,
            "magic_arrow", "Magic Arrow",
            "6", "64", "SHARP",
            "runicrebirth:magic_arrow",
            "textures/gui/shape_icons/arrow_icon.png", 2.5f,
            0.29f, 0.6f, 0f,
            spellAdv("magic_arrow"), "Kill a skeleton with magic"
        ).generate('D'));
        arrow.withParent(this.parent(blast));

        // === ADEPT SPELLCASTING MILESTONE ===
        var adeptSpellcasting = this.add(new MilestoneEntryProvider(this,
            "adept_spellcasting", "Adept Spellcasting",
            "You have learned the fundamental spell forms. "
            + "Your growing mastery unlocks access to intermediate spells.\\n\\n"
            + "See **Adept Ascension** in the Foundation category for more details.",
            "runicrebirth:intermediate_circle", 0.8f,
            0f, 0f, 0f
        ).generate('1'));
        adeptSpellcasting.withParent(this.parent(arrow));

        // === INTERMEDIATE SPELLS (vertical column) ===
        var shield = this.add(new SpellEntryProvider(this,
            "magic_shield", "Magic Shield",
            "N/A", "Self", "BLUNT",
            "runicrebirth:magic_shield",
            "textures/gui/shape_icons/shield_icon.png", 0.6f,
            0f, 0f, 0f,
            spellAdv("magic_shield"), "Unlock conditions coming soon"
        ).generate('E'));
        shield.withParent(this.parent(adeptSpellcasting));

        var slash = this.add(new SpellEntryProvider(this,
            "magic_slash", "Magic Slash",
            "4", "16", "SHARP",
            "runicrebirth:magic_slash_demo",
            "textures/gui/shape_icons/slash_icon.png", 0.5f,
            1.1f, 0.4f, 0f,
            spellAdv("magic_slash"), "Unlock conditions coming soon"
        ).generate('F'));
        slash.withParent(this.parent(shield));

        var explosion = this.add(new SpellEntryProvider(this,
            "magic_explosion", "Magic Explosion",
            "7", "16", "BLUNT",
            "runicrebirth:magic_explosion",
            "textures/gui/shape_icons/explosion_icon.png", 0.4f,
            0.4f, -0.8f, 0f,
            spellAdv("magic_explosion"), "Unlock conditions coming soon"
        ).generate('G'));
        explosion.withParent(this.parent(slash));

        var meteor = this.add(new SpellEntryProvider(this,
            "magic_meteor", "Magic Meteor",
            "10 (+5 splash)", "16", "BLUNT",
            "runicrebirth:magic_meteor_demo",
            "textures/gui/shape_icons/meteor_icon.png", 0.4f,
            0f, 0f, 0f,
            spellAdv("magic_meteor"), "Unlock conditions coming soon"
        ).generate('H'));
        meteor.withParent(this.parent(explosion));

        // === ARCH SPELLCASTING MILESTONE ===
        var archSpellcasting = this.add(new MilestoneEntryProvider(this,
            "arch_spellcasting", "Arch Spellcasting",
            "The intermediate arts hold no more secrets for you. "
            + "Your power has grown enough to wield the most devastating spell forms.\\n\\n"
            + "See **Arch Ascension** in the Foundation category for more details.",
            "runicrebirth:advanced_circle", 0.6f,
            0f, 0f, 0f
        ).generate('2'));
        archSpellcasting.withParent(this.parent(meteor));

        // === ADVANCED SPELLS (vertical column) ===
        var binding = this.add(new SpellEntryProvider(this,
            "magic_binding", "Magic Binding",
            "6", "24", "SPIRIT",
            "runicrebirth:magic_binding",
            "textures/gui/shape_icons/binding_icon.png", 0.7f,
            0.75f, 0f, 0f,
            spellAdv("magic_binding"), "Unlock conditions coming soon"
        ).generate('I'));
        binding.withParent(this.parent(archSpellcasting));

        var hammer = this.add(new SpellEntryProvider(this,
            "magic_hammer", "Magic Hammer",
            "12 (+6 splash)", "16", "BLUNT",
            "runicrebirth:magic_hammer",
            "textures/gui/shape_icons/hammer_icon.png", 0.35f,
            -0.8f, -1.0f, 0f,
            spellAdv("magic_hammer"), "Unlock conditions coming soon"
        ).generate('J'));
        hammer.withParent(this.parent(binding));

        var ballista = this.add(new SpellEntryProvider(this,
            "magic_ballista", "Magic Ballista",
            "8", "64", "SHARP",
            "runicrebirth:magic_ballista_demo",
            "textures/gui/shape_icons/ballista_icon.png", 0.4f,
            0f, 0.5f, 0f,
            spellAdv("magic_ballista"), "Unlock conditions coming soon"
        ).generate('K'));
        ballista.withParent(this.parent(hammer));
    }

    @Override
    protected String categoryName() {
        return "Spells";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(Items.BLAZE_POWDER);
    }

    @Override
    public String categoryId() {
        return ID;
    }

    private static Identifier spellAdv(String spellId) {
        return Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spells/" + spellId);
    }
}
