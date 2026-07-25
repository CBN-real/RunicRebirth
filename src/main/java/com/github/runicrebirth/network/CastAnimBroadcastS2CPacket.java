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

public record CastAnimBroadcastS2CPacket(int entityId, int ticks) implements CustomPacketPayload {

    public static final Type<CastAnimBroadcastS2CPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "cast_anim_broadcast"));

    public static final StreamCodec<FriendlyByteBuf, CastAnimBroadcastS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buf, pkt) -> {
            buf.writeVarInt(pkt.entityId);
            buf.writeVarInt(pkt.ticks);
        },
        buf -> new CastAnimBroadcastS2CPacket(buf.readVarInt(), buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(CastAnimBroadcastS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientMagicData.setRemoteCastAnim(packet.entityId, packet.ticks));
    }

    public static void broadcast(ServerPlayer caster, int ticks) {
        PacketDistributor.sendToPlayersTrackingEntity(caster,
            new CastAnimBroadcastS2CPacket(caster.getId(), ticks));
    }
}
