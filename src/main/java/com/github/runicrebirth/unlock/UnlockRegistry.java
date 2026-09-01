package com.github.runicrebirth.unlock;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class UnlockRegistry {

    private static final Map<Identifier, UnlockEntry> BY_ID = new LinkedHashMap<>();
    private static final Map<UnlockCategory, List<UnlockEntry>> BY_CATEGORY = new EnumMap<>(UnlockCategory.class);

    static {
        for (UnlockCategory cat : UnlockCategory.values()) {
            BY_CATEGORY.put(cat, new ArrayList<>());
        }

        // SPELLS category
        register(new UnlockEntry(rl("spell_magic_projectile"), "Magic Projectile", 5, null,
                UnlockCategory.SPELLS, rl("textures/gui/shape_icons/line_icon.png"), 2, 0, List.of(), List.of()));
        register(new UnlockEntry(rl("spell_magic_beam"), "Magic Beam", 10, rl("spell_magic_projectile"),
                UnlockCategory.SPELLS, rl("textures/gui/shape_icons/circle_icon.png"), 2, 1, List.of(), List.of()));
        register(new UnlockEntry(rl("spell_magic_blast"), "Magic Blast", 10, rl("spell_magic_beam"),
                UnlockCategory.SPELLS, rl("textures/gui/shape_icons/v_icon.png"), 2, 2, List.of(), List.of()));
        register(new UnlockEntry(rl("spell_magic_arrow"), "Magic Arrow", 10, rl("spell_magic_blast"),
                UnlockCategory.SPELLS, rl("textures/gui/shape_icons/arrow_icon.png"), 2, 3, List.of(), List.of()));

        // ACCESSORIES placeholder
        register(new UnlockEntry(rl("accessory_starter"), "Arcane Accessory Knowledge", 5, null,
                UnlockCategory.ACCESSORIES, rl("textures/gui/shape_icons/plus_icon_small.png"), 0, 0, List.of(), List.of()));

        // WIZARD starter
        register(new UnlockEntry(rl("wizard_starter"), "Wizard's Path", 5, null,
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 0, 0, List.of(), List.of()));

        // Runic Mastery I-V (row 0)
        register(new UnlockEntry(rl("wizard_runic_mastery_1"), "Runic Mastery I", 10, rl("wizard_starter"),
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 1, 0,
                List.of(rl("unlock/spell_writer_1")), List.of()));
        register(new UnlockEntry(rl("wizard_runic_mastery_2"), "Runic Mastery II", 20, rl("wizard_runic_mastery_1"),
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 2, 0,
                List.of(rl("unlock/spell_writer_2")), List.of()));
        register(new UnlockEntry(rl("wizard_runic_mastery_3"), "Runic Mastery III", 50, rl("wizard_runic_mastery_2"),
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 3, 0,
                List.of(rl("unlock/spell_writer_3"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("wizard_runic_mastery_4"), "Runic Mastery IV", 75, rl("wizard_runic_mastery_3"),
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 4, 0,
                List.of(rl("unlock/spell_writer_4"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("wizard_runic_mastery_5"), "Runic Mastery V", 150, rl("wizard_runic_mastery_4"),
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 5, 0,
                List.of(rl("unlock/spell_writer_5"), rl("unlock/arch_practitioner")), List.of()));

        // Arcanum Calligraphy I-V (row 1)
        register(new UnlockEntry(rl("arcanum_calligraphy_1"), "Arcanum Calligraphy I", 15, rl("wizard_starter"),
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 1, 1,
                List.of(rl("unlock/size_matters_1")), List.of()));
        register(new UnlockEntry(rl("arcanum_calligraphy_2"), "Arcanum Calligraphy II", 30, rl("arcanum_calligraphy_1"),
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 2, 1,
                List.of(rl("unlock/size_matters_2")), List.of()));
        register(new UnlockEntry(rl("arcanum_calligraphy_3"), "Arcanum Calligraphy III", 100, rl("arcanum_calligraphy_2"),
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 3, 1,
                List.of(rl("unlock/size_matters_3"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("arcanum_calligraphy_4"), "Arcanum Calligraphy IV", 200, rl("arcanum_calligraphy_3"),
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 4, 1,
                List.of(rl("unlock/size_matters_4"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("arcanum_calligraphy_5"), "Arcanum Calligraphy V", 500, rl("arcanum_calligraphy_4"),
                UnlockCategory.WIZARD, rl("textures/gui/shape_icons/plus_icon_small.png"), 5, 1,
                List.of(rl("unlock/size_matters_5"), rl("unlock/arch_practitioner")), List.of()));

        // MAGE starter
        register(new UnlockEntry(rl("mage_starter"), "Mage's Path", 5, null,
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 0, 0, List.of(), List.of()));

        // Earth's Flow I-V (row 0)
        register(new UnlockEntry(rl("mage_earths_flow_1"), "Earth's Flow I", 15, rl("mage_starter"),
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 1, 0,
                List.of(rl("unlock/spell_slinger_1")), List.of()));
        register(new UnlockEntry(rl("mage_earths_flow_2"), "Earth's Flow II", 30, rl("mage_earths_flow_1"),
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 2, 0,
                List.of(rl("unlock/spell_slinger_2")), List.of()));
        register(new UnlockEntry(rl("mage_earths_flow_3"), "Earth's Flow III", 100, rl("mage_earths_flow_2"),
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 3, 0,
                List.of(rl("unlock/spell_slinger_3"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("mage_earths_flow_4"), "Earth's Flow IV", 150, rl("mage_earths_flow_3"),
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 4, 0,
                List.of(rl("unlock/spell_slinger_4"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("mage_earths_flow_5"), "Earth's Flow V", 300, rl("mage_earths_flow_4"),
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 5, 0,
                List.of(rl("unlock/spell_slinger_5"), rl("unlock/arch_practitioner")), List.of()));

        // Earth's Abundance I-V (row 1)
        register(new UnlockEntry(rl("mage_earths_abundance_1"), "Earth's Abundance I", 10, rl("mage_starter"),
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 1, 1,
                List.of(rl("unlock/quantity_over_quality_1")), List.of()));
        register(new UnlockEntry(rl("mage_earths_abundance_2"), "Earth's Abundance II", 20, rl("mage_earths_abundance_1"),
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 2, 1,
                List.of(rl("unlock/quantity_over_quality_2")), List.of()));
        register(new UnlockEntry(rl("mage_earths_abundance_3"), "Earth's Abundance III", 50, rl("mage_earths_abundance_2"),
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 3, 1,
                List.of(rl("unlock/quantity_over_quality_3"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("mage_earths_abundance_4"), "Earth's Abundance IV", 75, rl("mage_earths_abundance_3"),
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 4, 1,
                List.of(rl("unlock/quantity_over_quality_4"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("mage_earths_abundance_5"), "Earth's Abundance V", 150, rl("mage_earths_abundance_4"),
                UnlockCategory.MAGE, rl("textures/gui/shape_icons/plus_icon_small.png"), 5, 1,
                List.of(rl("unlock/quantity_over_quality_5"), rl("unlock/arch_practitioner")), List.of()));

        // RUNEBLADE starter
        register(new UnlockEntry(rl("runeblade_starter"), "Runeblade's Path", 5, null,
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 0, 0, List.of(), List.of()));

        // Martial Aura I-V (row 0)
        register(new UnlockEntry(rl("runeblade_martial_aura_1"), "Martial Aura I", 15, rl("runeblade_starter"),
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 1, 0,
                List.of(rl("unlock/weapon_slayer_1")), List.of()));
        register(new UnlockEntry(rl("runeblade_martial_aura_2"), "Martial Aura II", 30, rl("runeblade_martial_aura_1"),
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 2, 0,
                List.of(rl("unlock/weapon_slayer_2")), List.of()));
        register(new UnlockEntry(rl("runeblade_martial_aura_3"), "Martial Aura III", 100, rl("runeblade_martial_aura_2"),
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 3, 0,
                List.of(rl("unlock/weapon_slayer_3"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("runeblade_martial_aura_4"), "Martial Aura IV", 250, rl("runeblade_martial_aura_3"),
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 4, 0,
                List.of(rl("unlock/weapon_slayer_4"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("runeblade_martial_aura_5"), "Martial Aura V", 500, rl("runeblade_martial_aura_4"),
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 5, 0,
                List.of(rl("unlock/weapon_slayer_5"), rl("unlock/arch_practitioner")), List.of()));

        // Vigsál Maestro I-V (row 1)
        register(new UnlockEntry(rl("runeblade_vigsalmaestro_1"), "Vígsál Maestro I", 10, rl("runeblade_starter"),
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 1, 1,
                List.of(rl("unlock/weapon_ability_1")), List.of()));
        register(new UnlockEntry(rl("runeblade_vigsalmaestro_2"), "Vígsál Maestro II", 20, rl("runeblade_vigsalmaestro_1"),
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 2, 1,
                List.of(rl("unlock/weapon_ability_2")), List.of()));
        register(new UnlockEntry(rl("runeblade_vigsalmaestro_3"), "Vígsál Maestro III", 50, rl("runeblade_vigsalmaestro_2"),
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 3, 1,
                List.of(rl("unlock/weapon_ability_3"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("runeblade_vigsalmaestro_4"), "Vígsál Maestro IV", 75, rl("runeblade_vigsalmaestro_3"),
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 4, 1,
                List.of(rl("unlock/weapon_ability_4"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("runeblade_vigsalmaestro_5"), "Vígsál Maestro V", 150, rl("runeblade_vigsalmaestro_4"),
                UnlockCategory.RUNEBLADE, rl("textures/gui/shape_icons/plus_icon_small.png"), 5, 1,
                List.of(rl("unlock/weapon_ability_5"), rl("unlock/arch_practitioner")), List.of()));

        // ARTIFICER starter
        register(new UnlockEntry(rl("artificer_starter"), "Artificer's Path", 5, null,
                UnlockCategory.ARTIFICER, rl("textures/gui/shape_icons/plus_icon_small.png"), 0, 0, List.of(), List.of()));

        // Utility of the Hammer I-V (row 0)
        register(new UnlockEntry(rl("artificer_utility_hammer_1"), "Utility of the Hammer I", 10, rl("artificer_starter"),
                UnlockCategory.ARTIFICER, rl("textures/gui/shape_icons/plus_icon_small.png"), 1, 0,
                List.of(rl("unlock/ring_master_1")), List.of()));
        register(new UnlockEntry(rl("artificer_utility_hammer_2"), "Utility of the Hammer II", 20, rl("artificer_utility_hammer_1"),
                UnlockCategory.ARTIFICER, rl("textures/gui/shape_icons/plus_icon_small.png"), 2, 0,
                List.of(rl("unlock/ring_master_2")), List.of()));
        register(new UnlockEntry(rl("artificer_utility_hammer_3"), "Utility of the Hammer III", 50, rl("artificer_utility_hammer_2"),
                UnlockCategory.ARTIFICER, rl("textures/gui/shape_icons/plus_icon_small.png"), 3, 0,
                List.of(rl("unlock/ring_master_3"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("artificer_utility_hammer_4"), "Utility of the Hammer IV", 75, rl("artificer_utility_hammer_3"),
                UnlockCategory.ARTIFICER, rl("textures/gui/shape_icons/plus_icon_small.png"), 4, 0,
                List.of(rl("unlock/ring_master_4"), rl("unlock/runic_adept")), List.of()));
        register(new UnlockEntry(rl("artificer_utility_hammer_5"), "Utility of the Hammer V", 150, rl("artificer_utility_hammer_4"),
                UnlockCategory.ARTIFICER, rl("textures/gui/shape_icons/plus_icon_small.png"), 5, 0,
                List.of(rl("unlock/ring_master_5"), rl("unlock/arch_practitioner")), List.of()));

        // Commander of Yotor I-III (row 1)
        register(new UnlockEntry(rl("artificer_commander_yotor_1"), "Commander of Yotor I", 50, rl("artificer_starter"),
                UnlockCategory.ARTIFICER, rl("textures/gui/shape_icons/plus_icon_small.png"), 1, 1,
                List.of(rl("unlock/one_man_army_1")), List.of()));
        register(new UnlockEntry(rl("artificer_commander_yotor_2"), "Commander of Yotor II", 150, rl("artificer_commander_yotor_1"),
                UnlockCategory.ARTIFICER, rl("textures/gui/shape_icons/plus_icon_small.png"), 2, 1,
                List.of(rl("unlock/one_man_army_2")), List.of()));
        register(new UnlockEntry(rl("artificer_commander_yotor_3"), "Commander of Yotor III", 500, rl("artificer_commander_yotor_2"),
                UnlockCategory.ARTIFICER, rl("textures/gui/shape_icons/plus_icon_small.png"), 3, 1,
                List.of(rl("unlock/one_man_army_3"), rl("unlock/runic_adept")), List.of()));
    }

    private UnlockRegistry() {}

    private static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath("runicrebirth", path);
    }

    private static void register(UnlockEntry entry) {
        BY_ID.put(entry.getId(), entry);
        BY_CATEGORY.get(entry.getCategory()).add(entry);
    }

    public static Optional<UnlockEntry> byId(Identifier id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    public static List<UnlockEntry> forCategory(UnlockCategory cat) {
        return BY_CATEGORY.getOrDefault(cat, List.of());
    }

    public static Collection<UnlockEntry> getAll() {
        return BY_ID.values();
    }
}
