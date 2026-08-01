package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.ClientMagicData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PhantomMiningSyncS2CPacket(int ticks) implements CustomPacketPayload {

    public static final Type<PhantomMiningSyncS2CPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "phantom_mining_sync"));

    public static final StreamCodec<FriendlyByteBuf, PhantomMiningSyncS2CPacket> STREAM_CODEC =
        StreamCodec.of(
            (buf, pkt) -> buf.writeVarInt(pkt.ticks()),
            buf -> new PhantomMiningSyncS2CPacket(buf.readVarInt())
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void sendTo(ServerPlayer player, int ticks) {
        PacketDistributor.sendToPlayer(player, new PhantomMiningSyncS2CPacket(ticks));
    }

    public static void handle(PhantomMiningSyncS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientMagicData.setPhantomMiningTicks(packet.ticks()));
    }
}
