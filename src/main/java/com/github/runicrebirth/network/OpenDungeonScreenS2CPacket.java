package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.screens.DungeonSelectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenDungeonScreenS2CPacket(BlockPos controllerPos) implements CustomPacketPayload {

    public static final Type<OpenDungeonScreenS2CPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "open_dungeon_screen"));

    public static final StreamCodec<FriendlyByteBuf, OpenDungeonScreenS2CPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> buf.writeBlockPos(pkt.controllerPos),
            buf -> new OpenDungeonScreenS2CPacket(buf.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenDungeonScreenS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> Minecraft.getInstance().setScreen(
                new DungeonSelectionScreen(packet.controllerPos)));
    }
}
