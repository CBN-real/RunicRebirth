package com.github.runicrebirth.dungeon;

import com.github.runicrebirth.RunicRebirth;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonVariantRegistry extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    public static final DungeonVariantRegistry INSTANCE = new DungeonVariantRegistry();

    private final Map<ResourceLocation, DungeonVariant> variants = new HashMap<>();

    private DungeonVariantRegistry() {
        super(GSON, "dungeon_variants");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler) {
        variants.clear();
        for (var entry : objects.entrySet()) {
            ResourceLocation id = entry.getKey();
            try {
                JsonObject obj = entry.getValue().getAsJsonObject();
                DungeonVariant variant = DungeonVariant.fromJson(id, obj);
                if (!variant.isValid()) {
                    RunicRebirth.LOGGER.error("[DungeonVariantRegistry] Variant {} is invalid (missing required room types), skipping", id);
                    continue;
                }
                variants.put(id, variant);
                RunicRebirth.LOGGER.debug("[DungeonVariantRegistry] Loaded variant: {}", id);
            } catch (Exception e) {
                RunicRebirth.LOGGER.error("[DungeonVariantRegistry] Failed to load variant {}: {}", id, e.getMessage());
            }
        }
        RunicRebirth.LOGGER.info("[DungeonVariantRegistry] Loaded {} dungeon variants", variants.size());
    }

    public static DungeonVariant get(ResourceLocation id) {
        return INSTANCE.variants.get(id);
    }

    public static List<DungeonVariant> getVariantsForTier(ResourceLocation tierId) {
        List<DungeonVariant> result = new ArrayList<>();
        for (DungeonVariant v : INSTANCE.variants.values()) {
            if (tierId.equals(v.getTier())) result.add(v);
        }
        return Collections.unmodifiableList(result);
    }

    public static int count() {
        return INSTANCE.variants.size();
    }
}
