package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.registry.ModifierRegistry;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.SpellComponent;
import com.github.runicrebirth.api.spells.SpellStack;
import com.github.runicrebirth.client.ClientMagicData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → client: full sync of all SpellStacks + activeStackIndex. Triggered whenever a stack mutates.
 * Encoding: int activeIndex, int stackCount, per stack: int size, per entry: byte kind (0=type,1=modifier), Identifier id.
 */
public record StackChangedS2CPacket(
    int activeIndex,
    List<List<Entry>> stacks,
    int charges,
    List<Identifier> stackElements
) implements CustomPacketPayload {

    public record Entry(byte kind, Identifier id) {
        public static final byte KIND_TYPE = 0;
        public static final byte KIND_MODIFIER = 1;
    }

    public static final Type<StackChangedS2CPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "stack_changed"));

    public static final StreamCodec<FriendlyByteBuf, StackChangedS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            buf.writeVarInt(packet.activeIndex);
            buf.writeVarInt(packet.stacks.size());
            for (List<Entry> stack : packet.stacks) {
                buf.writeVarInt(stack.size());
                for (Entry e : stack) {
                    buf.writeByte(e.kind);
                    buf.writeIdentifier(e.id);
                }
            }
            buf.writeVarInt(packet.charges);
            buf.writeVarInt(packet.stackElements.size());
            for (Identifier rl : packet.stackElements) {
                buf.writeBoolean(rl != null);
                if (rl != null) buf.writeIdentifier(rl);
            }
        },
        buf -> {
            int active = buf.readVarInt();
            int stackCount = buf.readVarInt();
            List<List<Entry>> stacks = new ArrayList<>(stackCount);
            for (int s = 0; s < stackCount; s++) {
                int size = buf.readVarInt();
                List<Entry> stack = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    byte kind = buf.readByte();
                    Identifier id = buf.readIdentifier();
                    stack.add(new Entry(kind, id));
                }
                stacks.add(stack);
            }
            int charges = buf.readVarInt();
            int elemCount = buf.readVarInt();
            List<Identifier> stackElements = new ArrayList<>(elemCount);
            for (int i = 0; i < elemCount; i++) {
                stackElements.add(buf.readBoolean() ? buf.readIdentifier() : null);
            }
            return new StackChangedS2CPacket(active, stacks, charges, stackElements);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void sendTo(ServerPlayer player) {
        net.minecraft.world.item.ItemStack heldItem = player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);

        if (!(heldItem.getItem() instanceof com.github.runicrebirth.items.SpellWriter)) {
            PacketDistributor.sendToPlayer(player, new StackChangedS2CPacket(0, List.of(), 0, List.of()));
            return;
        }

        com.github.runicrebirth.api.spells.WandStacksData wandData = com.github.runicrebirth.items.SpellWriter.getStacks(heldItem);
        List<List<Entry>> snapshot = new ArrayList<>(wandData.stacks().size());
        List<Identifier> elementIds = new ArrayList<>(wandData.stacks().size());

        for (com.github.runicrebirth.api.spells.WandStacksData.StackEntry entry : wandData.stacks()) {
            List<Entry> list = new ArrayList<>(entry.components().size());
            for (com.github.runicrebirth.api.spells.WandStacksData.ComponentRef ref : entry.components()) {
                list.add(new Entry((byte) ref.kind(), ref.id()));
            }
            snapshot.add(list);
            elementIds.add(entry.elementId());
        }
        int activeCharges = wandData.stacks().isEmpty() ? 0
            : wandData.stacks().get(wandData.activeIndex()).chargeCount();
        PacketDistributor.sendToPlayer(player, new StackChangedS2CPacket(wandData.activeIndex(), snapshot, activeCharges, elementIds));
    }

    public static void handle(StackChangedS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientMagicData.apply(packet));
    }

    public static SpellComponent resolve(Entry entry) {
        SpellComponent primary = entry.kind == Entry.KIND_TYPE
            ? SpellTypeRegistry.get(entry.id)
            : ModifierRegistry.get(entry.id);
        if (primary != null) return primary;
        // Fallback: try the other registry (custom registries sometimes lag on client resync).
        SpellComponent fallback = entry.kind == Entry.KIND_TYPE
            ? ModifierRegistry.get(entry.id)
            : SpellTypeRegistry.get(entry.id);
        if (fallback != null) {
            RunicRebirth.LOGGER.warn("[RunicRebirth] Resolve fallback hit for {} (kind={})", entry.id, entry.kind);
        } else {
            RunicRebirth.LOGGER.warn("[RunicRebirth] Resolve failed entirely for {} (kind={}); type-reg size={}, mod-reg size={}",
                entry.id, entry.kind, SpellTypeRegistry.REGISTRY.size(), ModifierRegistry.REGISTRY.size());
        }
        return fallback;
    }
}
