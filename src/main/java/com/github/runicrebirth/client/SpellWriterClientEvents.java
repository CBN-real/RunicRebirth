package com.github.runicrebirth.client;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.drawing.DrawingCanvasScreen;
import com.github.runicrebirth.items.SpellWriter;
import com.github.runicrebirth.network.DrawStartC2SPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Client-side interceptor: when a player right-clicks a SpellWriter with an invalid active stack,
 * cancel the interaction and open the DrawingCanvasScreen instead of letting the use() reach the server.
 * Shift-click and cast paths are NOT canceled — server handles them via SpellWriter.use().
 */
@EventBusSubscriber(modid = RunicRebirth.MODID, value = Dist.CLIENT)
public final class SpellWriterClientEvents {

    private SpellWriterClientEvents() {}

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getItemStack().getItem() instanceof SpellWriter)) return;
        if (event.getEntity().isShiftKeyDown()) return;
        if (ClientMagicData.isActiveStackValid()) return;
        if (ClientMagicData.hasCharges()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;
        event.setCanceled(true);
        PacketDistributor.sendToServer(new DrawStartC2SPacket());
        mc.setScreen(new DrawingCanvasScreen());
    }
}
