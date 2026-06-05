package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.blocks.entity.OculusControllerBlockEntity;
import com.github.runicrebirth.blocks.entity.OculusPortalBlockEntity;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import com.github.runicrebirth.dungeon.DungeonType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SelectDungeonC2SPacket(
        ResourceLocation dungeonId,
        int difficulty,
        BlockPos controllerPos
) implements CustomPacketPayload {

    public static final Type<SelectDungeonC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "select_dungeon"));

    public static final StreamCodec<FriendlyByteBuf, SelectDungeonC2SPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeResourceLocation(pkt.dungeonId);
                buf.writeVarInt(pkt.difficulty);
                buf.writeBlockPos(pkt.controllerPos);
            },
            buf -> new SelectDungeonC2SPacket(
                    buf.readResourceLocation(),
                    buf.readVarInt(),
                    buf.readBlockPos()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SelectDungeonC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            DungeonType type = DungeonType.byId(packet.dungeonId);
            if (type == null) return;

            if (packet.difficulty < 1 || packet.difficulty > type.getMaxDifficulty()) return;

            DungeonData data = DungeonData.of(player);
            int maxSelectable = data.getMaxSelectableDifficulty(type.getId(), type.getMaxDifficulty());
            if (packet.difficulty > maxSelectable) return;

            var be = player.level().getBlockEntity(packet.controllerPos);
            if (!(be instanceof OculusControllerBlockEntity controller) || !controller.isActive()) return;

            BlockPos portalPos = controller.getPortalPos();
            if (portalPos == null) return;

            var portalBe = player.level().getBlockEntity(portalPos);
            if (!(portalBe instanceof OculusPortalBlockEntity portal)) return;

            portal.setSelectedDungeon(packet.dungeonId, packet.difficulty);
            player.displayClientMessage(
                    Component.literal("§6Portal attuned to " + type.getDisplayName()
                            + " §7(Difficulty " + packet.difficulty + ")§6. Walk through to enter."), false);
        });
    }
}
