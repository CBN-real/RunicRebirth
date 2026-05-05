package com.github.interactivemagic.network;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.SpellStack;
import com.github.interactivemagic.capabilities.magic.MagicData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClearStackC2SPacket() implements CustomPacketPayload {

    public static final Type<ClearStackC2SPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "clear_stack"));

    public static final StreamCodec<FriendlyByteBuf, ClearStackC2SPacket> STREAM_CODEC = StreamCodec.unit(new ClearStackC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(ClearStackC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            MagicData data = MagicData.of(player);
            SpellStack s = data.activeStack();
            if (s != null) s.clear();
            StackChangedS2CPacket.sendTo(player);
        });
    }
}
