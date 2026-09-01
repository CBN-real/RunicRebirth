package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FinalizeCircuitC2SPacket() implements CustomPacketPayload {

    public static final Type<FinalizeCircuitC2SPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "finalize_circuit"));

    public static final StreamCodec<FriendlyByteBuf, FinalizeCircuitC2SPacket> STREAM_CODEC =
        StreamCodec.unit(new FinalizeCircuitC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(FinalizeCircuitC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            DrawSubmitC2SPacket.finalizeCircuit(player);
        });
    }
}
