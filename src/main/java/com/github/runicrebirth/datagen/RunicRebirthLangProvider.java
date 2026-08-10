package com.github.runicrebirth.datagen;

import com.github.runicrebirth.RunicRebirth;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RunicRebirthLangProvider extends LanguageProvider {

    private final LanguageProviderCache cache;

    public RunicRebirthLangProvider(PackOutput output, LanguageProviderCache cache) {
        super(output, RunicRebirth.MODID, "en_us");
        this.cache = cache;
    }

    @Override
    protected void addTranslations() {
        cache.data().forEach(this::add);

        add("itemGroup.runicrebirth", "Interactive Magic Tab");
        add("item.runicrebirth.basic_runic_longsword", "Basic Runic Longsword");
        add("item.runicrebirth.acolyte_wand", "Acolyte Wand");
        add("item.runicrebirth.adept_staff", "Adept Staff");
        add("item.runicrebirth.inscribed_wand", "Inscribed Wand");
        add("item.runicrebirth.ring_of_expansion", "Ring of Expansion");
        add("item.runicrebirth.arcane_acolyte_ring", "Arcane Acolyte Ring");
        add("item.runicrebirth.arcane_tether_ring", "Arcane Tether Ring");
        add("item.runicrebirth.magic_hand_ring", "Ring of the Magic Hand");
        add("key.runicrebirth.activate_ring", "Activate Ring");
        add("item.runicrebirth.runic_codex", "Runic Codex");
        add("item.runicrebirth.acolyte_wizard_hat", "Acolyte Wizard Hat");
        add("item.runicrebirth.acolyte_robes", "Acolyte Robes");
        add("item.runicrebirth.acolyte_pants", "Acolyte Pants");
        add("item.runicrebirth.acolyte_boots", "Acolyte Boots");
        add("item.runicrebirth.acolyte_artificer_headgear", "Acolyte Artificer Headgear");
        add("item.runicrebirth.acolyte_mage_hood", "Acolyte Mage Hood");
        add("item.runicrebirth.acolyte_runeblade_helmet", "Acolyte Runeblade Helm");

        add("item.runicrebirth.adept_runeblade_helmet", "Adept Runeblade Helmet");
        add("item.runicrebirth.adept_runeblade_chestplate", "Adept Runeblade Chestplate");
        add("item.runicrebirth.adept_runeblade_leggings", "Adept Runeblade Leggings");
        add("item.runicrebirth.adept_runeblade_boots", "Adept Runeblade Boots");
        add("item.runicrebirth.adept_wizard_hat", "Adept Wizard Hat");
        add("item.runicrebirth.adept_wizard_robes", "Adept Wizard Robes");
        add("item.runicrebirth.adept_wizard_pants", "Adept Wizard Pants");
        add("item.runicrebirth.adept_wizard_boots", "Adept Wizard Boots");
        add("item.runicrebirth.adept_mage_hood", "Adept Mage Hood");
        add("item.runicrebirth.adept_mage_robes", "Adept Mage Robes");
        add("item.runicrebirth.adept_mage_pants", "Adept Mage Pants");
        add("item.runicrebirth.adept_mage_boots", "Adept Mage Boots");
        add("item.runicrebirth.adept_artificer_headgear", "Adept Artificer Headgear");
        add("item.runicrebirth.adept_artificer_chestgear", "Adept Artificer Chestgear");
        add("item.runicrebirth.adept_artificer_pants", "Adept Artificer Pants");
        add("item.runicrebirth.adept_artificer_boots", "Adept Artificer Boots");

        add("item.runicrebirth.ring_of_leaping_gales", "Ring of Leaping Gales");
        add("item.runicrebirth.ring_of_phantom_mining", "Ring of Phantom Mining");
        add("item.runicrebirth.blink_ring", "Blink Ring");
        add("item.runicrebirth.thruster_ring", "Thruster Ring");
        add("item.runicrebirth.hover_ring", "Hover Ring");

        add("entity.runicrebirth.magic_projectile", "Magic Projectile");
        add("entity.runicrebirth.magic_arrow", "Magic Arrow");
        add("entity.runicrebirth.magic_slash", "Magic Slash");
        add("entity.runicrebirth.magic_meteor", "Magic Meteor");
        add("entity.runicrebirth.magic_shield", "Magic Shield");
        add("entity.runicrebirth.magic_hammer", "Magic Hammer");
        add("entity.runicrebirth.magic_binding", "Magic Binding");
        add("entity.runicrebirth.magic_ballista", "Magic Ballista");
        add("entity.runicrebirth.basic_circle", "Spell Circle");
        add("entity.runicrebirth.magic_blast", "Magic Blast");
        add("entity.runicrebirth.infusion_circle", "Infusion Circle");
        add("entity.runicrebirth.arcane_drone", "Arcane Drone");
        add("entity.runicrebirth.hammer_drone", "Hammer Drone");
        add("item.runicrebirth.arcane_drone", "Arcane Drone");
        add("item.runicrebirth.hammer_drone", "Hammer Drone");
        add("curios.identifier.runic_drone", "Runic Drone");
        add("entity.runicrebirth.runesteel_golem", "Runesteel Golem");
        add("entity.runicrebirth.zombified_runeblade_acolyte", "Zombified Runeblade Acolyte");
        add("entity.runicrebirth.skeletal_mage_acolyte", "Skeletal Mage Acolyte");
        add("entity.runicrebirth.skeletal_wizard_acolyte", "Skeletal Wizard Acolyte");
        add("entity.runicrebirth.zombified_artificer_acolyte", "Zombified Artificer Acolyte");

        add("spell_component.runicrebirth.magic_projectile", "Magic Projectile");
        add("spell_component.runicrebirth.magic_beam", "Magic Beam");
        add("spell_component.runicrebirth.magic_blast", "Magic Blast");
        add("spell_component.runicrebirth.magic_arrow", "Magic Arrow");
        add("spell_component.runicrebirth.magic_explosion", "Magic Explosion");
        add("spell_component.runicrebirth.magic_slash", "Magic Slash");
        add("spell_component.runicrebirth.magic_meteor", "Magic Meteor");
        add("spell_component.runicrebirth.magic_shield", "Magic Shield");
        add("spell_component.runicrebirth.magic_hammer", "Magic Hammer");
        add("spell_component.runicrebirth.magic_binding", "Magic Binding");
        add("spell_component.runicrebirth.magic_ballista", "Magic Ballista");
        add("spell_component.runicrebirth.infusion", "Infusion");
        add("spell_component.runicrebirth.sharp_boost", "Sharp Boost");
        add("spell_component.runicrebirth.blunt_boost", "Blunt Boost");
        add("spell_component.runicrebirth.magic_boost", "Magic Boost");
        add("spell_component.runicrebirth.additive_size", "Size +");
        add("spell_component.runicrebirth.size_plus", "Size +");
        add("spell_component.runicrebirth.size_plus_two", "Size ++");
        add("spell_component.runicrebirth.size_plus_four", "Size ++++");
        add("spell_component.runicrebirth.range", "Range");
        add("spell_component.runicrebirth.cooldown", "Cooldown");
        add("spell_component.runicrebirth.two_casts", "Two Casts");
        add("spell_component.runicrebirth.four_casts", "Four Casts");
        add("spell_component.runicrebirth.charges", "2x Charges");
        add("spell_component.runicrebirth.charges_three", "3x Charges");
        add("spell_component.runicrebirth.charges_four", "4x Charges");

        add("magic.runicrebirth.element.arcane", "Arcane");
        add("magic.runicrebirth.element.fire", "Fire");
        add("magic.runicrebirth.element.earth", "Earth");
        add("magic.runicrebirth.element.ice", "Ice");
        add("magic.runicrebirth.element.wind", "Wind");

        add("key.categories.runicrebirth", "Runic Rebirth");
        add("key.runicrebirth.switch_spell_stack", "Switch Spell Stack");
        add("screen.runicrebirth.drawing_canvas", "Drawing Canvas");
        add("screen.runicrebirth.canvas_hint", "Left-click to draw §f|§7 Right-click to submit §f|§7 Esc to cancel");

        add("block.runicrebirth.runic_stone", "Runic Stone");
        add("block.runicrebirth.runic_stone_slab", "Runic Stone Slab");
        add("block.runicrebirth.runic_stone_stairs", "Runic Stone Stairs");
        add("block.runicrebirth.runic_stone_pillar", "Runic Stone Pillar");
        add("block.runicrebirth.oculus_portal", "Dimensional Oculus Portal");
        add("block.runicrebirth.oculus_controller", "Dimensional Oculus Controller");
        add("block.runicrebirth.oculus_pillar", "Activated Pillar");
        add("block.runicrebirth.runesteel_pylon", "Runesteel Pylon");
        add("block.runicrebirth.return_portal", "Return Portal");
        add("block.runicrebirth.trial_spawner", "Trial Spawner");
        add("block.runicrebirth.dungeon_temporary_platform", "Dungeon Temporary Platform");
        add("block.runicrebirth.dungeon_pressure_plate", "Dungeon Pressure Plate");
        add("block.runicrebirth.dungeon_spike", "Dungeon Spike");
        add("block.runicrebirth.crumbling_platform", "Crumbling Platform");
        add("block.runicrebirth.dungeon_boulder_spawner", "Dungeon Boulder Spawner");
        add("block.runicrebirth.dungeon_swinging_axe", "Dungeon Swinging Axe");
        add("block.runicrebirth.dungeon_flamethrower", "Dungeon Flamethrower");
        add("entity.runicrebirth.dungeon_boulder", "Dungeon Boulder");
        add("block.runicrebirth.infusion_altar", "Infusion Altar");
        add("block.runicrebirth.runic_anvil", "Runic Anvil");

        add("item.runicrebirth.acolyte_runic_circuit", "Acolyte Runic Circuit");
        add("item.runicrebirth.adept_runic_circuit", "Adept Runic Circuit");
        add("item.runicrebirth.arch_runic_circuit", "Arch Runic Circuit");
        add("item.runicrebirth.runic_circuit.blank", "Right-click to inscribe");
        add("item.runicrebirth.arcane_spirit", "Arcane Spirit");
        add("item.runicrebirth.arcane_gemstone", "Arcane Gemstone");

        add("advancement.runicrebirth.elements.root.title", "Elemental Mastery");
        add("advancement.runicrebirth.elements.root.description", "Begin your journey to master the elements.");
        add("advancement.runicrebirth.elements.fire.title", "Flame Awakened");
        add("advancement.runicrebirth.elements.fire.description", "Complete the Fire Trial to unlock the Fire element.");
        add("advancement.runicrebirth.elements.ice.title", "Frost Awakened");
        add("advancement.runicrebirth.elements.ice.description", "Complete the Ice Trial to unlock the Ice element.");
        add("advancement.runicrebirth.elements.wind.title", "Gale Awakened");
        add("advancement.runicrebirth.elements.wind.description", "Complete the Wind Trial to unlock the Wind element.");
        add("advancement.runicrebirth.elements.earth.title", "Stone Awakened");
        add("advancement.runicrebirth.elements.earth.description", "Complete the Earth Trial to unlock the Earth element.");

        add("dungeon.runicrebirth.fire_trial", "Fire Trial");
        add("dungeon.runicrebirth.ice_trial", "Ice Trial");
        add("dungeon.runicrebirth.wind_trial", "Wind Trial");
        add("dungeon.runicrebirth.earth_trial", "Earth Trial");
        add("dungeon.runicrebirth.acolyte", "Acolyte Dungeon");

        add("death.attack.blunt_magic", "%1$s was crushed by magical force");
        add("death.attack.blunt_magic.player", "%1$s was crushed by %2$s");
        add("death.attack.sharp_magic", "%1$s was pierced by magical force");
        add("death.attack.sharp_magic.player", "%1$s was pierced by %2$s");
        add("death.attack.spirit_magic", "%1$s's soul was shattered");
        add("death.attack.spirit_magic.player", "%1$s's soul was shattered by %2$s");

        add("gui.runicrebirth.beg_spells", "Beg. Spells");
        add("gui.runicrebirth.int_spells", "Int. Spells");
        add("gui.runicrebirth.adv_spells", "Adv. Spells");
        add("gui.runicrebirth.beg_mods", "Beg. Mods");
        add("gui.runicrebirth.int_mods", "Int. Mods");
        add("gui.runicrebirth.adv_mods", "Adv. Mods");

        add("runicrebirth.spell.locked", "§cSpell Locked: %s");

        add("advancement.runicrebirth.spells.magic_projectile.title", "First Steps");
        add("advancement.runicrebirth.spells.magic_projectile.description", "Hold a spell writer for the first time");
        add("advancement.runicrebirth.spells.magic_beam.title", "Long Range Mastery");
        add("advancement.runicrebirth.spells.magic_beam.description", "Kill an entity with Magic Projectile from over 10 blocks away");
        add("advancement.runicrebirth.spells.magic_arrow.title", "Skeleton Slayer");
        add("advancement.runicrebirth.spells.magic_arrow.description", "Kill a skeleton with magic");
        add("advancement.runicrebirth.spells.magic_blast.title", "Close Quarters");
        add("advancement.runicrebirth.spells.magic_blast.description", "Kill an entity with magic from less than 5 blocks away");
        add("advancement.runicrebirth.spells.magic_explosion.title", "Magic Explosion");
        add("advancement.runicrebirth.spells.magic_explosion.description", "Unlock conditions coming soon");
        add("advancement.runicrebirth.spells.magic_slash.title", "Magic Slash");
        add("advancement.runicrebirth.spells.magic_slash.description", "Unlock conditions coming soon");
        add("advancement.runicrebirth.spells.magic_meteor.title", "Magic Meteor");
        add("advancement.runicrebirth.spells.magic_meteor.description", "Unlock conditions coming soon");
        add("advancement.runicrebirth.spells.magic_shield.title", "Magic Shield");
        add("advancement.runicrebirth.spells.magic_shield.description", "Unlock conditions coming soon");
        add("advancement.runicrebirth.spells.magic_hammer.title", "Magic Hammer");
        add("advancement.runicrebirth.spells.magic_hammer.description", "Unlock conditions coming soon");
        add("advancement.runicrebirth.spells.magic_binding.title", "Magic Binding");
        add("advancement.runicrebirth.spells.magic_binding.description", "Unlock conditions coming soon");
        add("advancement.runicrebirth.spells.magic_ballista.title", "Magic Ballista");
        add("advancement.runicrebirth.spells.magic_ballista.description", "Unlock conditions coming soon");

        add("item.runicrebirth.runic_key_ring", "Runic Key Ring");
        add("container.runicrebirth.runic_key_ring", "Runic Key Ring");
        add("container.runicrebirth.equipped_rings", "Equipped Rings");
        add("container.runicrebirth.key_ring_storage", "Key Ring");
    }
}
