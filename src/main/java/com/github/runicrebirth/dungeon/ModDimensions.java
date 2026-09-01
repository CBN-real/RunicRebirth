package com.github.runicrebirth.dungeon;

import com.github.runicrebirth.RunicRebirth;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public final class ModDimensions {

    public static final ResourceKey<Level> DUNGEON_LEVEL =
            ResourceKey.create(Registries.DIMENSION,
                    Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon"));

    public static final ResourceKey<DimensionType> DUNGEON_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE,
                    Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon"));

    private ModDimensions() {}
}
