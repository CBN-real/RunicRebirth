package com.github.interactivemagic.init;

import com.github.interactivemagic.InteractiveMagic;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public final class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
        DeferredRegister.create(Registries.PARTICLE_TYPE, InteractiveMagic.MODID);

    public static final Supplier<SimpleParticleType> FIRE_ELEMENT =
        PARTICLES.register("fire_element", () -> new SimpleParticleType(false));

  public static final Supplier<SimpleParticleType> WIND_ELEMENT =
      PARTICLES.register("wind_element", () -> new SimpleParticleType(false));

  public static final Supplier<SimpleParticleType> EARTH_ELEMENT =
      PARTICLES.register("earth_element", () -> new SimpleParticleType(false));

  public static final Supplier<SimpleParticleType> ARCANE_ELEMENT =
      PARTICLES.register("arcane_element", () -> new SimpleParticleType(false));

  public static final Supplier<SimpleParticleType> ICE_ELEMENT =
      PARTICLES.register("ice_element", () -> new SimpleParticleType(false));

  public static final Supplier<SimpleParticleType> ARCANE_TINY =
      PARTICLES.register("arcane_tiny", () -> new SimpleParticleType(false));

    private ModParticles() {}
}
