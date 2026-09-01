package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.particle.ScaledParticleOption;
import com.github.runicrebirth.particle.TremorBlockParticleOption;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public final class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
        DeferredRegister.create(Registries.PARTICLE_TYPE, RunicRebirth.MODID);

    // TODO 26.1.2: Verify ParticleType constructor API for codec-only particles
    @SuppressWarnings("unchecked")
    private static Supplier<ParticleType<ScaledParticleOption>> registerScaled(String name) {
        return PARTICLES.register(name, () -> new ParticleType<ScaledParticleOption>(false) {
            @Override
            public MapCodec<ScaledParticleOption> codec() {
                return ScaledParticleOption.codec(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, ScaledParticleOption> streamCodec() {
                return ScaledParticleOption.streamCodec(this);
            }
        });
    }

    public static final Supplier<ParticleType<ScaledParticleOption>> FIRE_ELEMENT   = registerScaled("fire_element");
    public static final Supplier<ParticleType<ScaledParticleOption>> WIND_ELEMENT   = registerScaled("wind_element");
    public static final Supplier<ParticleType<ScaledParticleOption>> EARTH_ELEMENT  = registerScaled("earth_element");
    public static final Supplier<ParticleType<ScaledParticleOption>> ARCANE_ELEMENT = registerScaled("arcane_element");
    public static final Supplier<ParticleType<ScaledParticleOption>> ICE_ELEMENT    = registerScaled("ice_element");

    public static final Supplier<ParticleType<ScaledParticleOption>> ARCANE_TINY = registerScaled("arcane_tiny");
    public static final Supplier<ParticleType<ScaledParticleOption>> FIRE_TINY   = registerScaled("fire_tiny");
    public static final Supplier<ParticleType<ScaledParticleOption>> ICE_TINY    = registerScaled("ice_tiny");
    public static final Supplier<ParticleType<ScaledParticleOption>> EARTH_TINY  = registerScaled("earth_tiny");
    public static final Supplier<ParticleType<ScaledParticleOption>> WIND_TINY   = registerScaled("wind_tiny");

    public static final Supplier<ParticleType<ScaledParticleOption>> ARCANE_INK = registerScaled("arcane_ink");
    public static final Supplier<ParticleType<ScaledParticleOption>> FIRE_INK   = registerScaled("fire_ink");
    public static final Supplier<ParticleType<ScaledParticleOption>> ICE_INK    = registerScaled("ice_ink");
    public static final Supplier<ParticleType<ScaledParticleOption>> EARTH_INK  = registerScaled("earth_ink");
    public static final Supplier<ParticleType<ScaledParticleOption>> WIND_INK   = registerScaled("wind_ink");

    public static final Supplier<ParticleType<ScaledParticleOption>> RESISTED     = registerScaled("resisted");
    public static final Supplier<ParticleType<ScaledParticleOption>> CRITICAL_HIT = registerScaled("critical_hit");

    public static final Supplier<ParticleType<ScaledParticleOption>> HOVER_EFFECT = registerScaled("hover_effect");

    @SuppressWarnings("unchecked")
    public static final Supplier<ParticleType<TremorBlockParticleOption>> TREMOR_BLOCK =
        PARTICLES.register("tremor_block", () -> new ParticleType<TremorBlockParticleOption>(false) {
            @Override
            public MapCodec<TremorBlockParticleOption> codec() {
                return TremorBlockParticleOption.codec(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, TremorBlockParticleOption> streamCodec() {
                return TremorBlockParticleOption.streamCodec(this);
            }
        });

    private ModParticles() {}
}
