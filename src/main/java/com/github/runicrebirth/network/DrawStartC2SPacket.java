package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.advancement.triggers.ModCriteriaTriggers;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.entities.DrawingCanvasEntity;
import com.github.runicrebirth.items.RunicCircuitItem;
import com.github.runicrebirth.items.SpellWriter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DrawStartC2SPacket() implements CustomPacketPayload {

    public static final Type<DrawStartC2SPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "draw_start"));

    public static final StreamCodec<FriendlyByteBuf, DrawStartC2SPacket> STREAM_CODEC = StreamCodec.unit(new DrawStartC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DrawStartC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
            boolean isSpellWriter = held.getItem() instanceof SpellWriter;
            boolean isCircuit = held.getItem() instanceof RunicCircuitItem
                && !RunicCircuitItem.isInscribed(held);

            if (!isSpellWriter && !isCircuit) return;

            if (isSpellWriter) {
                ModCriteriaTriggers.HELD_SPELL_WRITER.get().trigger(player);
            }

            MagicData data = MagicData.of(player);
            data.setDrawing(true);
            DrawingCanvasEntity canvas = DrawingCanvasEntity.spawnFor(player);
            if (canvas != null) {
                data.setCanvasEntityId(canvas.getId());
            }
            SpellUnlockSyncS2CPacket.sendTo(player);
            CastAnimBroadcastS2CPacket.broadcast(player, 1200);
        });
    }
}
