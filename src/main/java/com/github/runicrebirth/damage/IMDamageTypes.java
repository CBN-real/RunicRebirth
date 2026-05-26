package com.github.runicrebirth.damage;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public final class IMDamageTypes {

    private IMDamageTypes() {}

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, name));
    }

    public static final ResourceKey<DamageType> BLUNT_MAGIC = register("blunt_magic");
    public static final ResourceKey<DamageType> SHARP_MAGIC = register("sharp_magic");
    public static final ResourceKey<DamageType> SPIRIT_MAGIC = register("spirit_magic");

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(BLUNT_MAGIC, new DamageType(
            BLUNT_MAGIC.location().getPath(),
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0f));
        context.register(SHARP_MAGIC, new DamageType(
            SHARP_MAGIC.location().getPath(),
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0f));
        context.register(SPIRIT_MAGIC, new DamageType(
            SPIRIT_MAGIC.location().getPath(),
            DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
            0f));
    }
}
