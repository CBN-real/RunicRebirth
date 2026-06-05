package com.github.runicrebirth.advancement;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.advancement.triggers.ModCriteriaTriggers;
import com.github.runicrebirth.damage.SpellDamageSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

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
        ResourceLocation spellTypeId = sds.spellTypeId();
        ResourceLocation victimType = BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType());

        ModCriteriaTriggers.MAGIC_KILL.get().trigger(player, spellTypeId, distance, victimType);
    }
}
