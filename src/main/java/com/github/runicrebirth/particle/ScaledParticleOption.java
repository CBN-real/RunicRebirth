package com.github.runicrebirth.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ScaledParticleOption implements ParticleOptions {

    private final ParticleType<ScaledParticleOption> type;
    private final float scale;

    public ScaledParticleOption(ParticleType<ScaledParticleOption> type, float scale) {
        this.type = type;
        this.scale = scale;
    }

    @Override
    public ParticleType<ScaledParticleOption> getType() {
        return type;
    }

    public float getScale() {
        return scale;
    }

    public static MapCodec<ScaledParticleOption> codec(ParticleType<ScaledParticleOption> type) {
        return RecordCodecBuilder.mapCodec(b -> b.group(
            com.mojang.serialization.Codec.FLOAT.fieldOf("scale").forGetter(ScaledParticleOption::getScale)
        ).apply(b, scale -> new ScaledParticleOption(type, scale)));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, ScaledParticleOption> streamCodec(ParticleType<ScaledParticleOption> type) {
        return StreamCodec.of(
            (buf, option) -> buf.writeFloat(option.scale),
            buf -> new ScaledParticleOption(type, buf.readFloat())
        );
    }
}
