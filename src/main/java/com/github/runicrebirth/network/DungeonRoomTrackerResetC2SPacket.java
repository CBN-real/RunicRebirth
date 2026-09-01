package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.DungeonRoomTrackerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DungeonRoomTrackerResetC2SPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<DungeonRoomTrackerResetC2SPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon_room_tracker_reset"));

    public static final StreamCodec<FriendlyByteBuf, DungeonRoomTrackerResetC2SPacket> STREAM_CODEC = StreamCodec.of(
        (buf, pkt) -> buf.writeBlockPos(pkt.pos),
        buf -> new DungeonRoomTrackerResetC2SPacket(buf.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(DungeonRoomTrackerResetC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player().level() instanceof ServerLevel server)) return;
            if (server.getBlockEntity(packet.pos()) instanceof DungeonRoomTrackerBlockEntity tracker) {
                tracker.reset(server);
            }
        });
    }
}
