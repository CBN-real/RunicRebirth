package com.github.runicrebirth.advancement;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.advancement.triggers.ModCriteriaTriggers;
import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import com.github.runicrebirth.damage.SpellDamageSource;
import com.github.runicrebirth.entities.ArcaneDroneEntity;
import com.github.runicrebirth.entities.HammerDroneEntity;
import com.github.runicrebirth.unlock.UnlockBonusCalculator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public final class SpellUnlockEvents {

    private SpellUnlockEvents() {}

    @SubscribeEvent
    public static void onMagicKill(LivingDeathEvent event) {
        if (!(event.getSource() instanceof SpellDamageSource sds)) return;

        Entity causingEntity = sds.getEntity();
        if (!(causingEntity instanceof ServerPlayer player)) return;

        LivingEntity victim = event.getEntity();
        double distance = player.distanceTo(victim);
        Identifier spellTypeId = sds.spellTypeId();
        Identifier victimType = BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType());

        ModCriteriaTriggers.MAGIC_KILL.get().trigger(player, spellTypeId, distance, victimType);

        List<String> modifiers = ModifierKillTracker.getModifiers(player);
        if (!modifiers.isEmpty()) {
            DungeonData data = DungeonData.of(player);
            for (String modPath : modifiers) {
                int total = data.incrementModifierKills(modPath);
                ModCriteriaTriggers.MODIFIER_KILL.get().trigger(player, modPath, total);
            }
        }
    }

    @SubscribeEvent
    public static void onWeaponKill(LivingDeathEvent event) {
        if (event.getSource() instanceof SpellDamageSource) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof IMagicWeapon)) return;

        DungeonData data = DungeonData.of(player);
        int total = data.incrementMagicWeaponKills();
        ModCriteriaTriggers.WEAPON_KILL.get().trigger(player, total);
    }

    @SubscribeEvent
    public static void onDroneKill(LivingDeathEvent event) {
        if (!(event.getSource() instanceof SpellDamageSource sds)) return;
        if (!(sds.getEntity() instanceof ServerPlayer player)) return;
        Identifier sid = sds.spellTypeId();
        if (sid == null) return;
        boolean isDrone = sid.equals(HammerDroneEntity.DRONE_ID)
                       || sid.equals(ArcaneDroneEntity.ARCANE_DRONE_ID);
        if (!isDrone) return;

        DungeonData data = DungeonData.of(player);
        int total = data.incrementDroneKills();
        ModCriteriaTriggers.DRONE_KILL.get().trigger(player, total);
    }

    @SubscribeEvent
    public static void onWeaponAttack(LivingIncomingDamageEvent event) {
        if (event.getSource() instanceof SpellDamageSource) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!(player.getMainHandItem().getItem() instanceof IMagicWeapon)) return;
        float mult = UnlockBonusCalculator.getAuraDamageMultiplier(player);
        if (mult != 1.0f) {
            event.setAmount(event.getAmount() * mult);
        }
    }
}
