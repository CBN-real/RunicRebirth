package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.advancement.SpellAdvancementHelper;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.client.ClientMagicData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record SpellUnlockSyncS2CPacket(Set<Identifier> unlockedSpells) implements CustomPacketPayload {

    public static final Type<SpellUnlockSyncS2CPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spell_unlock_sync"));

    public static final StreamCodec<FriendlyByteBuf, SpellUnlockSyncS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buf, pkt) -> {
            buf.writeVarInt(pkt.unlockedSpells.size());
            for (Identifier id : pkt.unlockedSpells) {
                buf.writeIdentifier(id);
            }
        },
        buf -> {
            int count = buf.readVarInt();
            Set<Identifier> set = new HashSet<>(count);
            for (int i = 0; i < count; i++) {
                set.add(buf.readIdentifier());
            }
            return new SpellUnlockSyncS2CPacket(set);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(SpellUnlockSyncS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientMagicData.setUnlockedSpells(packet.unlockedSpells()));
    }

    public static void sendTo(ServerPlayer player) {
        Set<Identifier> unlocked = new HashSet<>();
        SpellTypeRegistry.REGISTRY.forEach(type -> {
            if (SpellAdvancementHelper.hasSpellUnlocked(player, type)) {
                unlocked.add(type.id());
            }
        });
        PacketDistributor.sendToPlayer(player, new SpellUnlockSyncS2CPacket(unlocked));
    }
}
