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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public class DungeonTierRegistry extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {

    private static final Gson GSON = new Gson();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("dungeon_tiers");
    public static final DungeonTierRegistry INSTANCE = new DungeonTierRegistry();

    private final Map<Identifier, DungeonTier> tiers = new HashMap<>();

    private DungeonTierRegistry() {}

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<Identifier, JsonElement> result = new HashMap<>();
        for (Map.Entry<Identifier, Resource> entry : LISTER.listMatchingResources(manager).entrySet()) {
            try (var reader = new java.io.InputStreamReader(entry.getValue().open())) {
                result.put(LISTER.fileToId(entry.getKey()), com.google.gson.JsonParser.parseReader(reader));
            } catch (Exception e) {
                RunicRebirth.LOGGER.error("[DungeonTierRegistry] Failed to read {}: {}", entry.getKey(), e.getMessage());
            }
        }
        return result;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler) {
        tiers.clear();
        for (var entry : objects.entrySet()) {
            Identifier id = entry.getKey();
            try {
                JsonObject obj = entry.getValue().getAsJsonObject();
                DungeonTier tier = DungeonTier.fromJson(id, obj);
                tiers.put(id, tier);
                RunicRebirth.LOGGER.debug("[DungeonTierRegistry] Loaded tier: {}", id);
            } catch (Exception e) {
                RunicRebirth.LOGGER.error("[DungeonTierRegistry] Failed to load tier {}: {}", id, e.getMessage());
            }
        }
        RunicRebirth.LOGGER.info("[DungeonTierRegistry] Loaded {} dungeon tiers", tiers.size());
    }

    public static DungeonTier get(Identifier id) {
        return INSTANCE.tiers.get(id);
    }

    public static Collection<DungeonTier> getAll() {
        return Collections.unmodifiableCollection(INSTANCE.tiers.values());
    }

    public static int count() {
        return INSTANCE.tiers.size();
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon_tiers"), DungeonTierRegistry.INSTANCE);
        event.addListener(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "dungeon_variants"), DungeonVariantRegistry.INSTANCE);
    }
}
