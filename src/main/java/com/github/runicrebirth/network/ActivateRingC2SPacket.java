package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.items.curios.IActivatableRing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;

public record ActivateRingC2SPacket() implements CustomPacketPayload {

    public static final Type<ActivateRingC2SPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "activate_ring"));

    public static final StreamCodec<FriendlyByteBuf, ActivateRingC2SPacket> STREAM_CODEC =
        StreamCodec.unit(new ActivateRingC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ActivateRingC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
                var slots = inv.getCurios().get("spell_ring");
                if (slots == null) return;
                ItemStack stack = slots.getStacks().getStackInSlot(0);
                if (!stack.isEmpty() && stack.getItem() instanceof IActivatableRing ring) {
                    ring.activate(player, stack);
                }
            });
        });
    }
}
