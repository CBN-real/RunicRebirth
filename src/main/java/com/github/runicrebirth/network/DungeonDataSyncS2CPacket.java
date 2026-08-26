package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import com.github.runicrebirth.client.ClientDungeonData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record DungeonDataSyncS2CPacket(
        Set<ResourceLocation> unlockedElements,
        int knowledgePoints,
        Set<ResourceLocation> unlockedSpellTypes,
        Map<ResourceLocation, Integer> maxDifficultyCleared,
        Set<ResourceLocation> unlockedEntries
) implements CustomPacketPayload {

    public static final Type<DungeonDataSyncS2CPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon_data_sync"));

    public static final StreamCodec<FriendlyByteBuf, DungeonDataSyncS2CPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeVarInt(pkt.unlockedElements.size());
                pkt.unlockedElements.forEach(buf::writeResourceLocation);
                buf.writeVarInt(pkt.knowledgePoints);
                buf.writeVarInt(pkt.unlockedSpellTypes.size());
                pkt.unlockedSpellTypes.forEach(buf::writeResourceLocation);
                buf.writeVarInt(pkt.maxDifficultyCleared.size());
                pkt.maxDifficultyCleared.forEach((id, diff) -> {
                    buf.writeResourceLocation(id);
                    buf.writeVarInt(diff);
                });
                buf.writeVarInt(pkt.unlockedEntries.size());
                pkt.unlockedEntries.forEach(buf::writeResourceLocation);
            },
            buf -> {
                int elemCount = buf.readVarInt();
                Set<ResourceLocation> elements = new HashSet<>();
                for (int i = 0; i < elemCount; i++) elements.add(buf.readResourceLocation());
                int kp = buf.readVarInt();
                int spellCount = buf.readVarInt();
                Set<ResourceLocation> spells = new HashSet<>();
                for (int i = 0; i < spellCount; i++) spells.add(buf.readResourceLocation());
                int diffCount = buf.readVarInt();
                Map<ResourceLocation, Integer> diffs = new HashMap<>();
                for (int i = 0; i < diffCount; i++) diffs.put(buf.readResourceLocation(), buf.readVarInt());
                int entryCount = buf.readVarInt();
                Set<ResourceLocation> entries = new HashSet<>();
                for (int i = 0; i < entryCount; i++) entries.add(buf.readResourceLocation());
                return new DungeonDataSyncS2CPacket(elements, kp, spells, diffs, entries);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(DungeonDataSyncS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientDungeonData.apply(packet));
    }

    public static void sendTo(ServerPlayer player) {
        DungeonData data = DungeonData.of(player);
        PacketDistributor.sendToPlayer(player, new DungeonDataSyncS2CPacket(
                new HashSet<>(data.getUnlockedElements()),
                data.getKnowledgePoints(),
                new HashSet<>(data.getUnlockedSpellTypes()),
                new HashMap<>(Map.of()),
                new HashSet<>(data.getUnlockedEntries())
        ));
    }
}
