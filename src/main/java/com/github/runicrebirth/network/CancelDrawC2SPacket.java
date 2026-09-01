package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.entities.DrawingCanvasEntity;
import com.github.runicrebirth.items.SpellWriter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CancelDrawC2SPacket() implements CustomPacketPayload {

    public static final Type<CancelDrawC2SPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "cancel_draw"));

    public static final StreamCodec<FriendlyByteBuf, CancelDrawC2SPacket> STREAM_CODEC = StreamCodec.unit(new CancelDrawC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(CancelDrawC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            MagicData data = MagicData.of(player);
            data.setDrawing(false);
            data.clearPendingCircuit();
            int entityId = data.canvasEntityId();
            if (entityId != -1) {
                Entity entity = player.level().getEntity(entityId);
                if (entity instanceof DrawingCanvasEntity canvas) {
                    canvas.beginEnding();
                }
                data.clearCanvasEntityId();
            }
            CastAnimBroadcastS2CPacket.broadcast(player, 0, net.minecraft.world.InteractionHand.MAIN_HAND);
            ItemStack held = player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);
        });
    }
}
