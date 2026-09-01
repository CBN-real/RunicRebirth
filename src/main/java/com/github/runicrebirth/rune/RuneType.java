package com.github.runicrebirth.rune;

import com.github.runicrebirth.api.spells.SpellParams;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class RuneType {

    private final Identifier id;
    private final String displayName;

    protected RuneType(Identifier id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Identifier id() { return id; }
    public String displayName() { return displayName; }

    public abstract List<String> allStatKeys();
    public abstract float rollStat(String key, int tier, RandomSource rand);
    public abstract boolean applicableTo(ItemStack stack);
    public abstract void applyToParams(EnhancementRuneData data, SpellParams params);

    public Map<String, Float> rollStats(int tier, RandomSource rand) {
        List<String> keys = new ArrayList<>(allStatKeys());
        Collections.shuffle(keys, new Random(rand.nextLong()));
        int count = tier;
        Map<String, Float> result = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(count, keys.size()); i++) {
            String key = keys.get(i);
            result.put(key, rollStat(key, tier, rand));
        }
        return result;
    }

    protected static float rangeRoll(RandomSource rand, float min, float max) {
        return min + rand.nextFloat() * (max - min);
    }
}
