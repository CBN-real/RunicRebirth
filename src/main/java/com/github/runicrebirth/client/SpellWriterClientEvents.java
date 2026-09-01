package com.github.runicrebirth.client;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.drawing.DrawingCanvasScreen;
import com.github.runicrebirth.items.RunicCircuitItem;
import com.github.runicrebirth.items.SpellWriter;
import com.github.runicrebirth.network.DrawStartC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.fml.LogicalSide;

@EventBusSubscriber(modid = RunicRebirth.MODID, value = Dist.CLIENT)
public final class SpellWriterClientEvents {

    private SpellWriterClientEvents() {}

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getSide() != LogicalSide.CLIENT) return;
        ItemStack held = event.getItemStack();

        if (held.getItem() instanceof RunicCircuitItem circuit) {
            if (circuit.isInscribed(held)) return;
            if (event.getEntity().isShiftKeyDown()) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null) return;
            event.setCanceled(true);
            ClientPacketDistributor.sendToServer(new DrawStartC2SPacket());
            mc.setScreen(new DrawingCanvasScreen(true, circuit.getModifierSlots(held)));
            return;
        }

        if (!(held.getItem() instanceof SpellWriter)) return;
        if (event.getEntity().isShiftKeyDown()) return;
        if (ClientMagicData.isHeldStackValid()) return;
        if (ClientMagicData.hasCharges()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;
        event.setCanceled(true);
        ClientPacketDistributor.sendToServer(new DrawStartC2SPacket());
        mc.setScreen(new DrawingCanvasScreen());
    }
}
