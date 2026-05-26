package com.github.runicrebirth.config;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue HUD_MAX_SLOTS;
    public static final ModConfigSpec.IntValue CANVAS_SIZE_PX;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("hud");
        HUD_MAX_SLOTS = b.comment("Max number of slots shown per SpellStack row in HUD").defineInRange("maxSlots", 8, 1, 16);
        b.pop();
        b.push("canvas");
        CANVAS_SIZE_PX = b.comment("Drawing canvas side length in pixels").defineInRange("sizePx", 100, 100, 1000);
        b.pop();
        SPEC = b.build();
    }

    private ClientConfig() {}

    public static void register(net.neoforged.fml.ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC);
    }
}
