package com.github.runicrebirth.dungeon;

import com.github.runicrebirth.RunicRebirth;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public class DungeonTierRegistry extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    public static final DungeonTierRegistry INSTANCE = new DungeonTierRegistry();

    private final Map<ResourceLocation, DungeonTier> tiers = new HashMap<>();

    private DungeonTierRegistry() {
        super(GSON, "dungeon_tiers");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager manager, ProfilerFiller profiler) {
        tiers.clear();
        for (var entry : objects.entrySet()) {
            ResourceLocation id = entry.getKey();
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

    public static DungeonTier get(ResourceLocation id) {
        return INSTANCE.tiers.get(id);
    }

    public static Collection<DungeonTier> getAll() {
        return Collections.unmodifiableCollection(INSTANCE.tiers.values());
    }

    public static int count() {
        return INSTANCE.tiers.size();
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(DungeonTierRegistry.INSTANCE);
        event.addListener(DungeonVariantRegistry.INSTANCE);
    }
}
