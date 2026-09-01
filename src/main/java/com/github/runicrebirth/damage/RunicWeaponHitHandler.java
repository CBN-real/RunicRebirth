package com.github.runicrebirth.damage;

import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.init.ModDataComponents;
import com.github.runicrebirth.rune.ElementRuneType;
import com.github.runicrebirth.rune.EnhancementRuneData;
import com.github.runicrebirth.rune.RuneType;
import com.github.runicrebirth.rune.RuneTypeRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

@EventBusSubscriber
public class RunicWeaponHitHandler {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof IMagicWeapon)) return;

        List<EnhancementRuneData> runes = mainHand.get(ModDataComponents.ENHANCEMENT_RUNES.get());
        if (runes == null || runes.isEmpty()) return;

        LivingEntity target = event.getEntity();
        for (EnhancementRuneData rune : runes) {
            RuneType type = RuneTypeRegistry.get(rune.runeTypeId());
            if (!(type instanceof ElementRuneType ert)) continue;
            float chance = rune.stats().getOrDefault("element_chance", 0f);
            if (player.level().getRandom().nextFloat() < chance) {
                ert.element().applyStatusEffects(target);
            }
        }
    }
}
