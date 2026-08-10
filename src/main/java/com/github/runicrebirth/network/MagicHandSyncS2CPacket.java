package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.entities.MagicHandEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MagicHandSyncS2CPacket(int ticks, boolean passive) implements CustomPacketPayload {

    public static final Type<MagicHandSyncS2CPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "magic_hand_sync"));

    public static final StreamCodec<FriendlyByteBuf, MagicHandSyncS2CPacket> STREAM_CODEC =
        StreamCodec.of(
            (buf, pkt) -> { buf.writeVarInt(pkt.ticks()); buf.writeBoolean(pkt.passive()); },
            buf -> new MagicHandSyncS2CPacket(buf.readVarInt(), buf.readBoolean())
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void sendTo(ServerPlayer player, int ticks, boolean passive) {
        PacketDistributor.sendToPlayer(player, new MagicHandSyncS2CPacket(ticks, passive));
    }

    public static void handle(MagicHandSyncS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientMagicData.setMagicHandSync(packet.ticks(), packet.passive());
            // -1 = indefinite active (passive mode), 0 = deactivated, >0 = hostile countdown
            int durationValue = packet.ticks() == 0 ? 0 : (packet.passive() ? -1 : packet.ticks());
            ClientMagicData.applyRingDuration(MagicHandEntity.DURATION_KEY,
                durationValue, MagicHandEntity.HOSTILE_HOLD_TICKS);
        });
    }
}
