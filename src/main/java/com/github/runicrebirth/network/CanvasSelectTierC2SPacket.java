package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.entities.DrawingCanvasEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CanvasSelectTierC2SPacket(int tier) implements CustomPacketPayload {

    public static final Type<CanvasSelectTierC2SPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "canvas_select_tier"));

    public static final StreamCodec<FriendlyByteBuf, CanvasSelectTierC2SPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> buf.writeVarInt(packet.tier),
        buf -> new CanvasSelectTierC2SPacket(buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(CanvasSelectTierC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            MagicData data = MagicData.of(player);
            if (!data.isDrawing()) return;
            int entityId = data.canvasEntityId();
            if (entityId == -1) return;
            Entity entity = player.level().getEntity(entityId);
            if (entity instanceof DrawingCanvasEntity canvas) {
                canvas.setSelectedTier(packet.tier);
            }
        });
    }
}
