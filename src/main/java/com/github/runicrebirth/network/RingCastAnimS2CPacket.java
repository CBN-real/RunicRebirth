package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.ClientMagicData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RingCastAnimS2CPacket(int entityId, int ticks) implements CustomPacketPayload {

    public static final Type<RingCastAnimS2CPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "ring_cast_anim"));

    public static final StreamCodec<FriendlyByteBuf, RingCastAnimS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buf, pkt) -> { buf.writeVarInt(pkt.entityId); buf.writeVarInt(pkt.ticks); },
        buf -> new RingCastAnimS2CPacket(buf.readVarInt(), buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(RingCastAnimS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.getId() == packet.entityId()) {
                ClientMagicData.setRingCastAnim(packet.ticks());
            } else {
                ClientMagicData.setRemoteRingCastAnim(packet.entityId(), packet.ticks());
            }
        });
    }

    public static void send(ServerPlayer caster, int ticks) {
        var pkt = new RingCastAnimS2CPacket(caster.getId(), ticks);
        PacketDistributor.sendToPlayer(caster, pkt);
        PacketDistributor.sendToPlayersTrackingEntity(caster, pkt);
    }
}
