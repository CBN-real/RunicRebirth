package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.animations.RunicWeaponAnimLayer;
import com.github.runicrebirth.client.animations.WarstaffAnimLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RunicWeaponAnimS2CPacket(int entityId, Anim anim) implements CustomPacketPayload {

    public enum Anim {
        SWORD_SWING, DAGGER_THROW, WARSTAFF_SPIN;

        public Identifier animId() {
            return switch (this) {
                case SWORD_SWING   -> RunicWeaponAnimLayer.SWORD_SWING_ANIM_ID;
                case DAGGER_THROW  -> RunicWeaponAnimLayer.DAGGER_THROW_ANIM_ID;
                case WARSTAFF_SPIN -> RunicWeaponAnimLayer.WARSTAFF_SPIN_ANIM_ID;
            };
        }
    }

    public static final Type<RunicWeaponAnimS2CPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "runic_weapon_anim"));

    public static final StreamCodec<FriendlyByteBuf, RunicWeaponAnimS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buf, pkt) -> { buf.writeVarInt(pkt.entityId); buf.writeByte(pkt.anim.ordinal()); },
        buf -> new RunicWeaponAnimS2CPacket(buf.readVarInt(), Anim.values()[buf.readByte()])
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(RunicWeaponAnimS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;
            Entity entity = mc.level.getEntity(packet.entityId());
            if (entity instanceof AbstractClientPlayer acp) {
                RunicWeaponAnimLayer.trigger(acp, packet.anim().animId());
                if (packet.anim() == Anim.WARSTAFF_SPIN) {
                    WarstaffAnimLayer.setSpinning(packet.entityId(), 3000L);
                }
            }
        });
    }

    public static void send(ServerPlayer caster, Anim anim) {
        var pkt = new RunicWeaponAnimS2CPacket(caster.getId(), anim);
        PacketDistributor.sendToPlayer(caster, pkt);
        PacketDistributor.sendToPlayersTrackingEntity(caster, pkt);
    }
}
