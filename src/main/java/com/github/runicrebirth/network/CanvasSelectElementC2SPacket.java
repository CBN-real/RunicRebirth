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

public record CanvasSelectElementC2SPacket(ResourceLocation elementId) implements CustomPacketPayload {

    public static final Type<CanvasSelectElementC2SPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "canvas_select_element"));

    public static final StreamCodec<FriendlyByteBuf, CanvasSelectElementC2SPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> buf.writeResourceLocation(packet.elementId),
        buf -> new CanvasSelectElementC2SPacket(buf.readResourceLocation())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(CanvasSelectElementC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            MagicData data = MagicData.of(player);
            if (!data.isDrawing()) return;
            int entityId = data.canvasEntityId();
            if (entityId == -1) return;
            Entity entity = player.level().getEntity(entityId);
            if (entity instanceof DrawingCanvasEntity canvas) {
                canvas.setSelectedElement(packet.elementId.toString());
            }
        });
    }
}
