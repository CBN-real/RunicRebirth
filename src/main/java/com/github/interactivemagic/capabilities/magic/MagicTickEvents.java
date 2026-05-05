package com.github.interactivemagic.capabilities.magic;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.config.ServerConfig;
import com.github.interactivemagic.network.StackChangedS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = InteractiveMagic.MODID)
public final class MagicTickEvents {

    private MagicTickEvents() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        MagicData data = MagicData.of(player);
        data.ensureStacks(ServerConfig.SPELL_STACK_COUNT.get());
        StackChangedS2CPacket.sendTo(player);
    }

    @SubscribeEvent
    public static void onPlayerPostTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        MagicData data = MagicData.of(player);
        data.ensureStacks(ServerConfig.SPELL_STACK_COUNT.get());
        data.tick();
    }
}
