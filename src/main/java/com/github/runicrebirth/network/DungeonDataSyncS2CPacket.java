package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import com.github.runicrebirth.client.ClientDungeonData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record DungeonDataSyncS2CPacket(
        Set<Identifier> unlockedElements,
        int knowledgePoints,
        Set<Identifier> unlockedSpellTypes,
        Map<Identifier, Integer> maxDifficultyCleared,
        Set<Identifier> unlockedEntries
) implements CustomPacketPayload {

    public static final Type<DungeonDataSyncS2CPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon_data_sync"));

    public static final StreamCodec<FriendlyByteBuf, DungeonDataSyncS2CPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeVarInt(pkt.unlockedElements.size());
                pkt.unlockedElements.forEach(buf::writeIdentifier);
                buf.writeVarInt(pkt.knowledgePoints);
                buf.writeVarInt(pkt.unlockedSpellTypes.size());
                pkt.unlockedSpellTypes.forEach(buf::writeIdentifier);
                buf.writeVarInt(pkt.maxDifficultyCleared.size());
                pkt.maxDifficultyCleared.forEach((id, diff) -> {
                    buf.writeIdentifier(id);
                    buf.writeVarInt(diff);
                });
                buf.writeVarInt(pkt.unlockedEntries.size());
                pkt.unlockedEntries.forEach(buf::writeIdentifier);
            },
            buf -> {
                int elemCount = buf.readVarInt();
                Set<Identifier> elements = new HashSet<>();
                for (int i = 0; i < elemCount; i++) elements.add(buf.readIdentifier());
                int kp = buf.readVarInt();
                int spellCount = buf.readVarInt();
                Set<Identifier> spells = new HashSet<>();
                for (int i = 0; i < spellCount; i++) spells.add(buf.readIdentifier());
                int diffCount = buf.readVarInt();
                Map<Identifier, Integer> diffs = new HashMap<>();
                for (int i = 0; i < diffCount; i++) diffs.put(buf.readIdentifier(), buf.readVarInt());
                int entryCount = buf.readVarInt();
                Set<Identifier> entries = new HashSet<>();
                for (int i = 0; i < entryCount; i++) entries.add(buf.readIdentifier());
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
