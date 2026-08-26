package com.github.runicrebirth.client;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.init.ModDataComponents;
import com.github.runicrebirth.rune.ElementRuneType;
import com.github.runicrebirth.rune.EnhancementRuneData;
import com.github.runicrebirth.rune.RuneType;
import com.github.runicrebirth.rune.RuneTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = RunicRebirth.MODID, value = Dist.CLIENT)
public final class RuneTooltipClientEvents {

    private static final String[] TIER_NAMES = { "", "Acolyte", "Adept", "Arch" };
    private static final ChatFormatting[] TIER_HEADER_COLORS = {
        ChatFormatting.WHITE, ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.GOLD
    };

    private RuneTooltipClientEvents() {}

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<EnhancementRuneData> runes = stack.get(ModDataComponents.ENHANCEMENT_RUNES.get());
        if (runes == null || runes.isEmpty()) return;

        List<Component> tooltip = event.getToolTip();
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Enhancements:").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));

        for (EnhancementRuneData rune : runes) {
            RuneType type = RuneTypeRegistry.get(rune.runeTypeId());
            if (type == null) continue;

            int tierIdx = Math.min(rune.tier(), 3);
            String tierName = TIER_NAMES[tierIdx];
            ChatFormatting tierColor = TIER_HEADER_COLORS[tierIdx];

            String header = type.displayName() + " Enhancement (" + tierName + ")";
            tooltip.add(Component.literal(header).withStyle(tierColor));

            for (var entry : rune.stats().entrySet()) {
                String key = entry.getKey();
                float val = entry.getValue();
                String line = "  " + formatStatKey(key) + ": " + formatStatValue(key, val);
                tooltip.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private static String formatStatKey(String key) {
        return switch (key) {
            case "element_chance" -> "Effect Chance";
            case "bonus_damage" -> "Bonus Damage";
            case "effect_radius" -> "Effect Radius";
            case "size_modifier" -> "Size Modifier";
            case "extra_casts" -> "Extra Casts";
            case "spell_damage" -> "Spell Damage";
            case "cooldown_reduction" -> "Cooldown Reduction";
            case "extra_charges" -> "Extra Charges";
            case "aura_damage" -> "Aura Damage";
            case "durability_increase" -> "Durability Increase";
            case "active_cooldown_reduction" -> "Active Cooldown Reduction";
            case "active_damage" -> "Active Damage";
            default -> key;
        };
    }

    private static String formatStatValue(String key, float value) {
        return switch (key) {
            case "element_chance", "bonus_damage", "size_modifier", "spell_damage",
                 "cooldown_reduction", "durability_increase", "active_cooldown_reduction", "active_damage" ->
                String.format("%.0f%%", value * 100f);
            case "effect_radius" -> String.format("%.1f blocks", value);
            case "extra_casts", "extra_charges", "aura_damage" ->
                String.format("+%.0f", value);
            default -> String.format("%.2f", value);
        };
    }
}
