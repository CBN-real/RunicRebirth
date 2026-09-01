package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.client.sounds.HoverRingSoundInstance;
import com.github.runicrebirth.items.curios.HoverRingItem;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RingDurationSyncS2CPacket(Map<Identifier, Integer> durations) implements CustomPacketPayload {

    public static final Type<RingDurationSyncS2CPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "ring_duration_sync"));

    public static final StreamCodec<FriendlyByteBuf, RingDurationSyncS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            buf.writeVarInt(packet.durations.size());
            for (Map.Entry<Identifier, Integer> e : packet.durations.entrySet()) {
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
            return new RingDurationSyncS2CPacket(map);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void sendTo(ServerPlayer player, Map<Identifier, Integer> durations) {
        PacketDistributor.sendToPlayer(player, new RingDurationSyncS2CPacket(new HashMap<>(durations)));
    }

    public static void handle(RingDurationSyncS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            boolean wasHovering = ClientMagicData.ringDurationRemaining(HoverRingItem.DURATION_KEY) != 0;
            ClientMagicData.applyRingDurations(packet.durations());
            boolean isHovering = ClientMagicData.ringDurationRemaining(HoverRingItem.DURATION_KEY) != 0;
            if (!wasHovering && isHovering) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    mc.getSoundManager().play(new HoverRingSoundInstance(mc.player));
                }
            }
        });
    }
}
