package com.github.interactivemagic.network;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.registry.ModifierRegistry;
import com.github.interactivemagic.api.registry.SpellTypeRegistry;
import com.github.interactivemagic.api.spells.SpellComponent;
import com.github.interactivemagic.api.spells.SpellStack;
import com.github.interactivemagic.capabilities.magic.MagicData;
import com.github.interactivemagic.client.ClientMagicData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → client: full sync of all SpellStacks + activeStackIndex. Triggered whenever a stack mutates.
 * Encoding: int activeIndex, int stackCount, per stack: int size, per entry: byte kind (0=type,1=modifier), ResourceLocation id.
 */
public record StackChangedS2CPacket(
    int activeIndex,
    List<List<Entry>> stacks,
    int charges,
    List<ResourceLocation> stackElements
) implements CustomPacketPayload {

    public record Entry(byte kind, ResourceLocation id) {
        public static final byte KIND_TYPE = 0;
        public static final byte KIND_MODIFIER = 1;
    }

    public static final Type<StackChangedS2CPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "stack_changed"));

    public static final StreamCodec<FriendlyByteBuf, StackChangedS2CPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            buf.writeVarInt(packet.activeIndex);
            buf.writeVarInt(packet.stacks.size());
            for (List<Entry> stack : packet.stacks) {
                buf.writeVarInt(stack.size());
                for (Entry e : stack) {
                    buf.writeByte(e.kind);
                    buf.writeResourceLocation(e.id);
                }
            }
            buf.writeVarInt(packet.charges);
            buf.writeVarInt(packet.stackElements.size());
            for (ResourceLocation rl : packet.stackElements) {
                buf.writeBoolean(rl != null);
                if (rl != null) buf.writeResourceLocation(rl);
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
                    ResourceLocation id = buf.readResourceLocation();
                    stack.add(new Entry(kind, id));
                }
                stacks.add(stack);
            }
            int charges = buf.readVarInt();
            int elemCount = buf.readVarInt();
            List<ResourceLocation> stackElements = new ArrayList<>(elemCount);
            for (int i = 0; i < elemCount; i++) {
                stackElements.add(buf.readBoolean() ? buf.readResourceLocation() : null);
            }
            return new StackChangedS2CPacket(active, stacks, charges, stackElements);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void sendTo(ServerPlayer player) {
        MagicData data = MagicData.of(player);
        SpellStack[] stacks = data.stacks();
        if (stacks == null) return;
        List<List<Entry>> snapshot = new ArrayList<>(stacks.length);
        List<ResourceLocation> elementIds = new ArrayList<>(stacks.length);
        for (SpellStack s : stacks) {
            List<Entry> list = new ArrayList<>(s.size());
            for (SpellComponent c : s.components()) {
                byte kind = c instanceof com.github.interactivemagic.api.spells.SpellType
                    ? Entry.KIND_TYPE : Entry.KIND_MODIFIER;
                list.add(new Entry(kind, c.id()));
            }
            snapshot.add(list);
            com.github.interactivemagic.api.spells.Element el = s.resolveElement();
            elementIds.add(el != null ? el.id() : null);
        }
        PacketDistributor.sendToPlayer(player, new StackChangedS2CPacket(data.activeStackIndex(), snapshot, data.charges(), elementIds));
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
            InteractiveMagic.LOGGER.warn("[InteractiveMagic] Resolve fallback hit for {} (kind={})", entry.id, entry.kind);
        } else {
            InteractiveMagic.LOGGER.warn("[InteractiveMagic] Resolve failed entirely for {} (kind={}); type-reg size={}, mod-reg size={}",
                entry.id, entry.kind, SpellTypeRegistry.REGISTRY.size(), ModifierRegistry.REGISTRY.size());
        }
        return fallback;
    }
}
