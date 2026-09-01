package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.screens.DungeonMobSpawnerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenDungeonMobSpawnerS2CPacket(BlockPos pos, CompoundTag config) implements CustomPacketPayload {

    public static final Type<OpenDungeonMobSpawnerS2CPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "open_dungeon_mob_spawner"));

    public static final StreamCodec<FriendlyByteBuf, OpenDungeonMobSpawnerS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buf, pkt) -> { buf.writeBlockPos(pkt.pos); buf.writeNbt(pkt.config); },
        buf -> new OpenDungeonMobSpawnerS2CPacket(buf.readBlockPos(), buf.readNbt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenDungeonMobSpawnerS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> Minecraft.getInstance().setScreen(
            new DungeonMobSpawnerScreen(packet.pos(), packet.config())));
    }
}
