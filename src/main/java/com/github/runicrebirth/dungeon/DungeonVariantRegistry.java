package com.github.runicrebirth.dungeon;

import com.github.runicrebirth.RunicRebirth;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonVariantRegistry extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {

    private static final Gson GSON = new Gson();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("dungeon_variants");
    public static final DungeonVariantRegistry INSTANCE = new DungeonVariantRegistry();

    private final Map<Identifier, DungeonVariant> variants = new HashMap<>();

    private DungeonVariantRegistry() {}

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<Identifier, JsonElement> result = new HashMap<>();
        for (Map.Entry<Identifier, Resource> entry : LISTER.listMatchingResources(manager).entrySet()) {
            try (var reader = new java.io.InputStreamReader(entry.getValue().open())) {
                result.put(LISTER.fileToId(entry.getKey()), com.google.gson.JsonParser.parseReader(reader));
            } catch (Exception e) {
                RunicRebirth.LOGGER.error("[DungeonVariantRegistry] Failed to read {}: {}", entry.getKey(), e.getMessage());
            }
        }
        return result;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler) {
        variants.clear();
        for (var entry : objects.entrySet()) {
            Identifier id = entry.getKey();
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

    public static DungeonVariant get(Identifier id) {
        return INSTANCE.variants.get(id);
    }

    public static List<DungeonVariant> getVariantsForTier(Identifier tierId) {
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
