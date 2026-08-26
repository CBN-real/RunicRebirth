package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.screens.RunicUnlockScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenUnlockScreenS2CPacket(BlockPos cushionPos) implements CustomPacketPayload {

    public static final Type<OpenUnlockScreenS2CPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "open_unlock_screen"));

    public static final StreamCodec<FriendlyByteBuf, OpenUnlockScreenS2CPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> buf.writeBlockPos(pkt.cushionPos),
            buf -> new OpenUnlockScreenS2CPacket(buf.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenUnlockScreenS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> Minecraft.getInstance().setScreen(new RunicUnlockScreen(packet.cushionPos())));
    }
}
