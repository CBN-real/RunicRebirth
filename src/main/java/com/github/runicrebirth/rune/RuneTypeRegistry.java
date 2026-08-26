package com.github.runicrebirth.rune;

import com.github.runicrebirth.init.ModElements;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RuneTypeRegistry {

    private static final Map<ResourceLocation, RuneType> REGISTRY = new LinkedHashMap<>();

    public static ElementRuneType ICE;
    public static ElementRuneType FIRE;
    public static ElementRuneType EARTH;
    public static ElementRuneType WIND;
    public static ElementRuneType ARCANE;
    public static ArcanumRuneType ARCANUM;
    public static OrderRuneType ORDER;
    public static VigsalRuneType VIGSALR;
    public static YotorRuneType YOTOR;

    private RuneTypeRegistry() {}

    public static void init() {
        ICE    = register(new ElementRuneType(ModElements.ICE.get()));
        FIRE   = register(new ElementRuneType(ModElements.FIRE.get()));
        EARTH  = register(new ElementRuneType(ModElements.EARTH.get()));
        WIND   = register(new ElementRuneType(ModElements.WIND.get()));
        ARCANE = register(new ElementRuneType(ModElements.ARCANE.get()));
        ARCANUM = register(new ArcanumRuneType());
        ORDER   = register(new OrderRuneType());
        VIGSALR = register(new VigsalRuneType());
        YOTOR   = register(new YotorRuneType());
    }

    private static <T extends RuneType> T register(T type) {
        REGISTRY.put(type.id(), type);
        return type;
    }

    public static RuneType get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static Collection<RuneType> all() {
        return REGISTRY.values();
    }
}
