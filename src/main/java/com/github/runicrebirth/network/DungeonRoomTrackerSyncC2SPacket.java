package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.DungeonRoomTrackerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DungeonRoomTrackerSyncC2SPacket(BlockPos pos, CompoundTag config) implements CustomPacketPayload {

    public static final Type<DungeonRoomTrackerSyncC2SPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon_room_tracker_sync"));

    public static final StreamCodec<FriendlyByteBuf, DungeonRoomTrackerSyncC2SPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> { buf.writeBlockPos(pkt.pos); buf.writeNbt(pkt.config); },
            buf -> new DungeonRoomTrackerSyncC2SPacket(buf.readBlockPos(), buf.readNbt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(DungeonRoomTrackerSyncC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player().level() instanceof ServerLevel server) {
                BlockEntity be = server.getBlockEntity(packet.pos());
                if (be instanceof DungeonRoomTrackerBlockEntity tracker) {
                    tracker.deserializeConfig(packet.config());
                }
            }
        });
    }
}
