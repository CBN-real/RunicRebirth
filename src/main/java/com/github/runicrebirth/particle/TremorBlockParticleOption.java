package com.github.runicrebirth.particle;

import com.github.runicrebirth.init.ModParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TremorBlockParticleOption implements ParticleOptions {

    private static final Codec<BlockState> BLOCK_STATE_CODEC = Codec.withAlternative(
        BlockState.CODEC, BuiltInRegistries.BLOCK.byNameCodec(), Block::defaultBlockState
    );

    private static final StreamCodec<FriendlyByteBuf, BlockState> BLOCK_STATE_STREAM_CODEC =
        StreamCodec.of(
            (buf, state) -> buf.writeVarInt(Block.BLOCK_STATE_REGISTRY.getId(state)),
            buf -> Block.BLOCK_STATE_REGISTRY.byId(buf.readVarInt())
        );

    private static final StreamCodec<FriendlyByteBuf, Vec3> VEC3_STREAM_CODEC =
        StreamCodec.of(
            (buf, vec) -> { buf.writeDouble(vec.x); buf.writeDouble(vec.y); buf.writeDouble(vec.z); },
            buf -> new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
        );

    private final ParticleType<TremorBlockParticleOption> type;
    private final BlockState state;
    private final Vec3 motion;
    private final float scale;

    public static MapCodec<TremorBlockParticleOption> codec(ParticleType<TremorBlockParticleOption> particleType) {
        return RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                BLOCK_STATE_CODEC.fieldOf("block_state").forGetter(TremorBlockParticleOption::getState),
                Vec3.CODEC.optionalFieldOf("motion", Vec3.ZERO).forGetter(TremorBlockParticleOption::getMotion),
                Codec.FLOAT.optionalFieldOf("scale", 1.0f).forGetter(TremorBlockParticleOption::getScale)
            ).apply(builder, (state, motion, scale) -> new TremorBlockParticleOption(particleType, state, motion, scale)));
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, TremorBlockParticleOption> streamCodec(
            ParticleType<TremorBlockParticleOption> particleType) {
        return StreamCodec.of(
            (buf, option) -> {
                BLOCK_STATE_STREAM_CODEC.encode(buf, option.state);
                VEC3_STREAM_CODEC.encode(buf, option.motion);
                buf.writeFloat(option.scale);
            },
            buf -> {
                BlockState state = BLOCK_STATE_STREAM_CODEC.decode(buf);
                Vec3 motion = VEC3_STREAM_CODEC.decode(buf);
                float scale = buf.readFloat();
                return new TremorBlockParticleOption(particleType, state, motion, scale);
            }
        );
    }

    public TremorBlockParticleOption(ParticleType<TremorBlockParticleOption> type, BlockState state, Vec3 motion, float scale) {
        this.type = type;
        this.state = state;
        this.motion = motion;
        this.scale = scale;
    }

    public TremorBlockParticleOption(ParticleType<TremorBlockParticleOption> type, BlockState state, Vec3 motion) {
        this(type, state, motion, 1.0f);
    }

    public TremorBlockParticleOption(BlockState state, Vec3 motion) {
        this(ModParticles.TREMOR_BLOCK.get(), state, motion, 1.0f);
    }

    public TremorBlockParticleOption(BlockState state, Vec3 motion, float scale) {
        this(ModParticles.TREMOR_BLOCK.get(), state, motion, scale);
    }

    @Override
    public ParticleType<TremorBlockParticleOption> getType() {
        return this.type;
    }

    public BlockState getState() {
        return this.state;
    }

    public Vec3 getMotion() {
        return this.motion;
    }

    public float getScale() {
        return this.scale;
    }
}
