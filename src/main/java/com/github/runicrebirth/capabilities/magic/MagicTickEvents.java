package com.github.runicrebirth.capabilities.magic;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.network.StackChangedS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public final class MagicTickEvents {

    private MagicTickEvents() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        StackChangedS2CPacket.sendTo(player);
    }

    @SubscribeEvent
    public static void onPlayerPostTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        MagicData data = MagicData.of(player);
        data.tick();
    }
}
