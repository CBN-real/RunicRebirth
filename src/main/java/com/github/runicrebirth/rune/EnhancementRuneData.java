package com.github.runicrebirth.rune;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record EnhancementRuneData(Identifier runeTypeId, int tier, Map<String, Float> stats) {

    public static final Codec<EnhancementRuneData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Identifier.CODEC.fieldOf("type").forGetter(EnhancementRuneData::runeTypeId),
        Codec.INT.fieldOf("tier").forGetter(EnhancementRuneData::tier),
        Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("stats").forGetter(EnhancementRuneData::stats)
    ).apply(inst, EnhancementRuneData::new));

    public static final StreamCodec<FriendlyByteBuf, EnhancementRuneData> STREAM_CODEC = StreamCodec.of(
        (buf, data) -> {
            buf.writeIdentifier(data.runeTypeId());
            buf.writeVarInt(data.tier());
            buf.writeVarInt(data.stats().size());
            data.stats().forEach((k, v) -> {
                buf.writeUtf(k);
                buf.writeFloat(v);
            });
        },
        buf -> {
            Identifier id = buf.readIdentifier();
            int tier = buf.readVarInt();
            int size = buf.readVarInt();
            Map<String, Float> stats = new LinkedHashMap<>(size);
            for (int i = 0; i < size; i++) {
                stats.put(buf.readUtf(), buf.readFloat());
            }
            return new EnhancementRuneData(id, tier, stats);
        }
    );

    public static final StreamCodec<FriendlyByteBuf, List<EnhancementRuneData>> LIST_STREAM_CODEC = StreamCodec.of(
        (buf, list) -> {
            buf.writeVarInt(list.size());
            list.forEach(d -> STREAM_CODEC.encode(buf, d));
        },
        buf -> {
            int n = buf.readVarInt();
            List<EnhancementRuneData> list = new ArrayList<>(n);
            for (int i = 0; i < n; i++) list.add(STREAM_CODEC.decode(buf));
            return list;
        }
    );
}
