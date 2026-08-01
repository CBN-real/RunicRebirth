package com.github.runicrebirth.capabilities.magic;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.curios.RingOfPhantomMiningItem;
import com.github.runicrebirth.network.CooldownSyncS2CPacket;
import com.github.runicrebirth.network.PhantomMiningSyncS2CPacket;
import com.github.runicrebirth.network.StackChangedS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public final class MagicTickEvents {

    private MagicTickEvents() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        StackChangedS2CPacket.sendTo(player);
        int pm = MagicData.of(player).phantomMiningTicks();
        if (pm > 0) PhantomMiningSyncS2CPacket.sendTo(player, pm);
    }

    @SubscribeEvent
    public static void onPlayerPostTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        MagicData data = MagicData.of(player);
        data.tick();
        CooldownSyncS2CPacket.sendTo(player, data.cooldowns());
        if (data.consumePhantomMiningExpired()) {
            PhantomMiningSyncS2CPacket.sendTo(player, 0);
            data.startCooldown(RingOfPhantomMiningItem.COOLDOWN_ID, RingOfPhantomMiningItem.COOLDOWN_TICKS);
        } else if (data.phantomMiningTicks() > 0 && player.tickCount % 20 == 0) {
            PhantomMiningSyncS2CPacket.sendTo(player, data.phantomMiningTicks());
        }
    }

    @SubscribeEvent
    public static void onMainHandChanged(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getSlot() != EquipmentSlot.MAINHAND) return;
        StackChangedS2CPacket.sendTo(player);
    }
}
