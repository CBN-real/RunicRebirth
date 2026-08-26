package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import com.github.runicrebirth.dungeon.DungeonInstance;
import com.github.runicrebirth.dungeon.DungeonInstanceManager;
import com.github.runicrebirth.dungeon.DungeonTeleporter;
import com.github.runicrebirth.dungeon.gen.DungeonGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EnterDungeonC2SPacket(
        ResourceLocation dungeonId,
        int difficulty,
        BlockPos controllerPos
) implements CustomPacketPayload {

    public static final Type<EnterDungeonC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "enter_dungeon"));

    public static final StreamCodec<FriendlyByteBuf, EnterDungeonC2SPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeResourceLocation(pkt.dungeonId);
                buf.writeVarInt(pkt.difficulty);
                buf.writeBlockPos(pkt.controllerPos);
            },
            buf -> new EnterDungeonC2SPacket(
                    buf.readResourceLocation(),
                    buf.readVarInt(),
                    buf.readBlockPos()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(EnterDungeonC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            if (packet.difficulty < 1 || packet.difficulty > 3) return;

            if (DungeonInstanceManager.get().isPlayerInDungeon(player.getUUID())) return;

            ResourceLocation returnDim = player.level().dimension().location();
            DungeonInstance instance = DungeonInstanceManager.get().createInstance(
                    packet.dungeonId, packet.difficulty, packet.controllerPos, returnDim);
            if (instance == null) return;

            DungeonGenerator.generate(player.getServer(), instance);

            DungeonInstanceManager.get().enterInstance(player, instance);
            DungeonTeleporter.teleportToDungeon(player, instance);
            com.github.runicrebirth.dungeon.DungeonEventHandler.onEnterDungeon(player, instance);

            DungeonDataSyncS2CPacket.sendTo(player);
        });
    }
}
