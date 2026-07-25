package com.github.runicrebirth.api.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record WandStacksData(List<StackEntry> stacks, int activeIndex) {

    public record ComponentRef(int kind, ResourceLocation id) {
        public static final int KIND_TYPE = 0;
        public static final int KIND_MODIFIER = 1;

        public static final Codec<ComponentRef> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("kind").forGetter(ComponentRef::kind),
            ResourceLocation.CODEC.fieldOf("id").forGetter(ComponentRef::id)
        ).apply(i, ComponentRef::new));

        public static final StreamCodec<ByteBuf, ComponentRef> STREAM_CODEC = StreamCodec.of(
            (buf, ref) -> {
                buf.writeByte(ref.kind);
                ByteBufCodecs.STRING_UTF8.encode(buf, ref.id.toString());
            },
            buf -> {
                int kind = buf.readByte();
                ResourceLocation id = ResourceLocation.parse(ByteBufCodecs.STRING_UTF8.decode(buf));
                return new ComponentRef(kind, id);
            }
        );
    }

    public record StackEntry(
        List<ComponentRef> components,
        @Nullable ResourceLocation elementId,
        boolean inscribed,
        int permanentCount
    ) {
        public static final StackEntry EMPTY = new StackEntry(List.of(), null, false, 0);

        public boolean hasPermanentSpellType() {
            for (int i = 0; i < permanentCount && i < components.size(); i++) {
                if (components.get(i).kind() == ComponentRef.KIND_TYPE) return true;
            }
            return false;
        }

        public StackEntry withClearedTemporary() {
            if (!inscribed || permanentCount <= 0) return EMPTY;
            List<ComponentRef> permanent = components.subList(0, Math.min(permanentCount, components.size()));
            return new StackEntry(List.copyOf(permanent), elementId, true, permanentCount);
        }

        public static final Codec<StackEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
            ComponentRef.CODEC.listOf().fieldOf("components").forGetter(StackEntry::components),
            ResourceLocation.CODEC.optionalFieldOf("element").forGetter(e -> Optional.ofNullable(e.elementId)),
            Codec.BOOL.optionalFieldOf("inscribed", false).forGetter(StackEntry::inscribed),
            Codec.INT.optionalFieldOf("permanentCount", 0).forGetter(StackEntry::permanentCount)
        ).apply(i, (comps, elem, ins, pc) -> new StackEntry(comps, elem.orElse(null), ins, pc)));

        public static final StreamCodec<ByteBuf, StackEntry> STREAM_CODEC = StreamCodec.of(
            (buf, entry) -> {
                ByteBufCodecs.INT.encode(buf, entry.components.size());
                for (ComponentRef ref : entry.components) {
                    ComponentRef.STREAM_CODEC.encode(buf, ref);
                }
                buf.writeBoolean(entry.elementId != null);
                if (entry.elementId != null) {
                    ByteBufCodecs.STRING_UTF8.encode(buf, entry.elementId.toString());
                }
                buf.writeBoolean(entry.inscribed);
                ByteBufCodecs.INT.encode(buf, entry.permanentCount);
            },
            buf -> {
                int count = ByteBufCodecs.INT.decode(buf);
                List<ComponentRef> comps = new ArrayList<>(count);
                for (int j = 0; j < count; j++) {
                    comps.add(ComponentRef.STREAM_CODEC.decode(buf));
                }
                ResourceLocation elem = buf.readBoolean()
                    ? ResourceLocation.parse(ByteBufCodecs.STRING_UTF8.decode(buf))
                    : null;
                boolean inscribed = buf.readBoolean();
                int pc = ByteBufCodecs.INT.decode(buf);
                return new StackEntry(List.copyOf(comps), elem, inscribed, pc);
            }
        );
    }

    public static final Codec<WandStacksData> CODEC = RecordCodecBuilder.create(i -> i.group(
        StackEntry.CODEC.listOf().fieldOf("stacks").forGetter(WandStacksData::stacks),
        Codec.INT.fieldOf("activeIndex").forGetter(WandStacksData::activeIndex)
    ).apply(i, WandStacksData::new));

    public static final StreamCodec<ByteBuf, WandStacksData> STREAM_CODEC = StreamCodec.of(
        (buf, data) -> {
            ByteBufCodecs.INT.encode(buf, data.stacks.size());
            for (StackEntry entry : data.stacks) {
                StackEntry.STREAM_CODEC.encode(buf, entry);
            }
            ByteBufCodecs.INT.encode(buf, data.activeIndex);
        },
        buf -> {
            int count = ByteBufCodecs.INT.decode(buf);
            List<StackEntry> stacks = new ArrayList<>(count);
            for (int j = 0; j < count; j++) {
                stacks.add(StackEntry.STREAM_CODEC.decode(buf));
            }
            int active = ByteBufCodecs.INT.decode(buf);
            return new WandStacksData(List.copyOf(stacks), active);
        }
    );

    public static WandStacksData createDefault(int stackCount) {
        List<StackEntry> entries = new ArrayList<>(stackCount);
        for (int i = 0; i < stackCount; i++) {
            entries.add(StackEntry.EMPTY);
        }
        return new WandStacksData(List.copyOf(entries), 0);
    }

    public WandStacksData withActiveIndex(int idx) {
        return new WandStacksData(this.stacks, Math.min(idx, stacks.size() - 1));
    }

    public WandStacksData withStack(int idx, StackEntry entry) {
        List<StackEntry> newStacks = new ArrayList<>(this.stacks);
        newStacks.set(idx, entry);
        return new WandStacksData(List.copyOf(newStacks), this.activeIndex);
    }

    public int inscribedCount() {
        int count = 0;
        for (StackEntry e : stacks) {
            if (e.inscribed) count++;
        }
        return count;
    }
}
