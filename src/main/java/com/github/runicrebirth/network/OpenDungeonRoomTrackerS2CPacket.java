package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.screens.DungeonRoomTrackerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenDungeonRoomTrackerS2CPacket(BlockPos pos, CompoundTag config) implements CustomPacketPayload {

    public static final Type<OpenDungeonRoomTrackerS2CPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "open_dungeon_room_tracker"));

    public static final StreamCodec<FriendlyByteBuf, OpenDungeonRoomTrackerS2CPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> { buf.writeBlockPos(pkt.pos); buf.writeNbt(pkt.config); },
            buf -> new OpenDungeonRoomTrackerS2CPacket(buf.readBlockPos(), buf.readNbt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenDungeonRoomTrackerS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> Minecraft.getInstance().setScreen(
                new DungeonRoomTrackerScreen(packet.pos(), packet.config())));
    }
}
