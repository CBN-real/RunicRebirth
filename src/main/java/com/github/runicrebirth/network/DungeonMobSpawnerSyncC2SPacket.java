package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.DungeonMobSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DungeonMobSpawnerSyncC2SPacket(BlockPos pos, CompoundTag config) implements CustomPacketPayload {

    public static final Type<DungeonMobSpawnerSyncC2SPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon_mob_spawner_sync"));

    public static final StreamCodec<FriendlyByteBuf, DungeonMobSpawnerSyncC2SPacket> STREAM_CODEC = StreamCodec.of(
        (buf, pkt) -> { buf.writeBlockPos(pkt.pos); buf.writeNbt(pkt.config); },
        buf -> new DungeonMobSpawnerSyncC2SPacket(buf.readBlockPos(), buf.readNbt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(DungeonMobSpawnerSyncC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player().level() instanceof ServerLevel server)) return;
            if (server.getBlockEntity(packet.pos()) instanceof DungeonMobSpawnerBlockEntity spawner) {
                spawner.deserializeConfig(packet.config());
            }
        });
    }
}
