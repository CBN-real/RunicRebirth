package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.SpellWriter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClearStackC2SPacket() implements CustomPacketPayload {

    public static final Type<ClearStackC2SPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "clear_stack"));

    public static final StreamCodec<FriendlyByteBuf, ClearStackC2SPacket> STREAM_CODEC = StreamCodec.unit(new ClearStackC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(ClearStackC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!(held.getItem() instanceof SpellWriter)) return;
            SpellWriter.clearActiveStack(held);
            StackChangedS2CPacket.sendTo(player);
        });
    }
}
