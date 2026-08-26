package com.github.runicrebirth.items;

import com.github.runicrebirth.init.ModDataComponents;
import com.github.runicrebirth.rune.RuneType;
import com.github.runicrebirth.rune.RuneTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class EnhancementRuneItem extends Item {

    private static final String[] TIER_NAMES = { "", "Acolyte", "Adept", "Arch" };
    private static final ChatFormatting[] TIER_COLORS = {
        ChatFormatting.WHITE, ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.GOLD
    };

    private final ResourceLocation runeTypeId;
    private final int tier;

    public EnhancementRuneItem(ResourceLocation runeTypeId, int tier, Properties properties) {
        super(properties.stacksTo(1));
        this.runeTypeId = runeTypeId;
        this.tier = tier;
    }

    public RuneType getRuneType() { return RuneTypeRegistry.get(runeTypeId); }
    public int getTier() { return tier; }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (!stack.has(ModDataComponents.RUNE_STATS.get())) {
            RuneType type = getRuneType();
            if (type == null) return;
            Map<String, Float> rolled = type.rollStats(tier, level.getRandom());
            CompoundTag tag = new CompoundTag();
            rolled.forEach(tag::putFloat);
            stack.set(ModDataComponents.RUNE_STATS.get(), tag);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltips, TooltipFlag flag) {
        int tierIdx = Math.min(tier, 3);
        tooltips.add(Component.literal(TIER_NAMES[tierIdx]).withStyle(TIER_COLORS[tierIdx]));

        CompoundTag stats = stack.get(ModDataComponents.RUNE_STATS.get());
        if (stats == null || stats.isEmpty()) {
            tooltips.add(Component.literal("Place in inventory to roll stats").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        for (String key : stats.getAllKeys()) {
            float val = stats.getFloat(key);
            String displayKey = formatStatKey(key);
            String displayVal = formatStatValue(key, val);
            tooltips.add(Component.literal(displayKey + ": " + displayVal).withStyle(ChatFormatting.GRAY));
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
