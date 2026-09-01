package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.effects.CameraShakeHandler;
import com.github.runicrebirth.client.effects.CrackManager;
import com.github.runicrebirth.client.effects.ShockwaveManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ImpactEffectS2CPacket(
    Vec3 position,
    float radius,
    int color,
    float shakeIntensity,
    int shakeDurationTicks
) implements CustomPacketPayload {

    public static final Type<ImpactEffectS2CPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "impact_effect"));

    public static final StreamCodec<FriendlyByteBuf, ImpactEffectS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buf, pkt) -> {
            buf.writeDouble(pkt.position.x);
            buf.writeDouble(pkt.position.y);
            buf.writeDouble(pkt.position.z);
            buf.writeFloat(pkt.radius);
            buf.writeVarInt(pkt.color);
            buf.writeFloat(pkt.shakeIntensity);
            buf.writeVarInt(pkt.shakeDurationTicks);
        },
        buf -> new ImpactEffectS2CPacket(
            new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
            buf.readFloat(),
            buf.readVarInt(),
            buf.readFloat(),
            buf.readVarInt()
        )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(ImpactEffectS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            CameraShakeHandler.addShake(packet.position, packet.shakeIntensity, packet.shakeDurationTicks);
            CrackManager.addCracks(packet.position, packet.radius * 1.25f, packet.color);
            com.github.runicrebirth.client.overlays.FlashOverlay.trigger(packet.radius);
            ShockwaveManager.addShockwave(packet.position, packet.radius);
        });
    }
}
