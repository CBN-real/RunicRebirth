package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.ClientMagicData;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CooldownSyncS2CPacket(Map<Identifier, Integer> cooldowns) implements CustomPacketPayload {

    public static final Type<CooldownSyncS2CPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "cooldown_sync"));

    public static final StreamCodec<FriendlyByteBuf, CooldownSyncS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            buf.writeVarInt(packet.cooldowns.size());
            for (Map.Entry<Identifier, Integer> e : packet.cooldowns.entrySet()) {
                buf.writeIdentifier(e.getKey());
                buf.writeVarInt(e.getValue());
            }
        },
        buf -> {
            int count = buf.readVarInt();
            Map<Identifier, Integer> map = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                map.put(buf.readIdentifier(), buf.readVarInt());
            }
            return new CooldownSyncS2CPacket(map);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void sendTo(ServerPlayer player, Map<Identifier, Integer> cooldowns) {
        PacketDistributor.sendToPlayer(player, new CooldownSyncS2CPacket(new HashMap<>(cooldowns)));
    }

    public static void handle(CooldownSyncS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientMagicData.applyCooldowns(packet.cooldowns()));
    }
}
