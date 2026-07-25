package com.github.runicrebirth.items;

import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.registry.ModifierRegistry;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.SpellComponent;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.api.spells.WandStacksData;
import com.github.runicrebirth.init.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class RunicCircuitItem extends Item {

    private static final String[] TIER_NAMES = { "", "Acolyte", "Adept", "Arch" };

    private final int tier;

    public RunicCircuitItem(Properties properties, int tier) {
        super(properties.stacksTo(1));
        this.tier = tier;
    }

    public static boolean isInscribed(ItemStack stack) {
        WandStacksData.StackEntry entry = stack.get(ModDataComponents.CIRCUIT_SPELL.get());
        return entry != null && !entry.components().isEmpty();
    }

    public static int getModifierSlots(ItemStack stack) {
        Integer storedTier = stack.get(ModDataComponents.CIRCUIT_TIER.get());
        return storedTier != null ? storedTier : 1;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (isInscribed(stack)) {
            return Component.literal("Inscribed Runic Circuit");
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int storedTier = getModifierSlots(stack);
        String tierName = (storedTier >= 1 && storedTier <= 3) ? TIER_NAMES[storedTier] : "Acolyte";
        tooltipComponents.add(Component.literal("Tier: " + tierName));

        if (isInscribed(stack)) {
            WandStacksData.StackEntry entry = stack.get(ModDataComponents.CIRCUIT_SPELL.get());
            if (entry != null) {
                for (WandStacksData.ComponentRef ref : entry.components()) {
                    SpellComponent component = ref.kind() == WandStacksData.ComponentRef.KIND_TYPE
                        ? SpellTypeRegistry.get(ref.id())
                        : ModifierRegistry.get(ref.id());
                    if (component != null) {
                        if (component instanceof SpellType) {
                            tooltipComponents.add(Component.literal("Spell: ").append(component.displayName()));
                        } else {
                            tooltipComponents.add(Component.literal("Modifier: ").append(component.displayName()));
                        }
                    }
                }
                if (entry.elementId() != null) {
                    Element element = ElementRegistry.get(entry.elementId());
                    if (element != null) {
                        int color = element.displayColor();
                        tooltipComponents.add(Component.literal("Element: ")
                            .append(Component.translatable("magic.runicrebirth.element." + entry.elementId().getPath())
                                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)))));
                    }
                }
            }
        } else {
            tooltipComponents.add(Component.translatable("item.runicrebirth.runic_circuit.blank"));
        }

        tooltipComponents.add(Component.literal("Modifier Slots: " + storedTier));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
