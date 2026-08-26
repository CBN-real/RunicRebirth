package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.animations.DaggerAnimLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DaggerAnimS2CPacket(int playerEntityId, int animOrdinal) implements CustomPacketPayload {

    public enum Anim { THROWN, RETURNING, IDLE }

    public static final Type<DaggerAnimS2CPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "dagger_anim"));

    public static final StreamCodec<FriendlyByteBuf, DaggerAnimS2CPacket> STREAM_CODEC =
        StreamCodec.of(
            (buf, pkt) -> { buf.writeVarInt(pkt.playerEntityId()); buf.writeVarInt(pkt.animOrdinal()); },
            buf -> new DaggerAnimS2CPacket(buf.readVarInt(), buf.readVarInt())
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(ServerPlayer caster, Anim anim) {
        var pkt = new DaggerAnimS2CPacket(caster.getId(), anim.ordinal());
        PacketDistributor.sendToPlayer(caster, pkt);
        PacketDistributor.sendToPlayersTrackingEntity(caster, pkt);
    }

    public static void handle(DaggerAnimS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            Entity entity = mc.level.getEntity(packet.playerEntityId());
            if (entity instanceof AbstractClientPlayer acp) {
                DaggerAnimLayer.trigger(acp, Anim.values()[packet.animOrdinal()]);
            }
        });
    }
}
