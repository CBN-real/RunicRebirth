package com.github.interactivemagic.network;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.capabilities.magic.MagicData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DrawStartC2SPacket() implements CustomPacketPayload {

    public static final Type<DrawStartC2SPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "draw_start"));

    public static final StreamCodec<FriendlyByteBuf, DrawStartC2SPacket> STREAM_CODEC = StreamCodec.unit(new DrawStartC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DrawStartC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            MagicData.of(player).setDrawing(true);
        });
    }
}
