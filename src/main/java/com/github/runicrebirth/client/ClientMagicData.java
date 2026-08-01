package com.github.runicrebirth.client;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellComponent;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.network.StackChangedS2CPacket;
import com.github.runicrebirth.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Client-side mirror of SpellStacks sync'd from server via StackChangedS2CPacket.
 * HUD + canvas + wand animation read from this.
 */
@OnlyIn(Dist.CLIENT)
public final class ClientMagicData {

    private static final int CAST_ANIM_TICKS = 60;

    private static List<List<SpellComponent>> stacks = Collections.emptyList();
    private static List<ResourceLocation> stackElements = Collections.emptyList();
    private static int activeIndex = 0;
    private static int castAnimTicksRemaining = 0;
    private static int ringCastAnimTicksRemaining = 0;
    private static int charges = 0;
    private static Set<ResourceLocation> unlockedSpells = new HashSet<>();
    private static final Map<Integer, Integer> remoteCastAnimTicks = new HashMap<>();
    private static final Map<Integer, Integer> remoteRingCastAnimTicks = new HashMap<>();
    private static final Map<ResourceLocation, Integer> cooldownRemaining = new HashMap<>();
    private static final Map<ResourceLocation, Integer> cooldownMax = new HashMap<>();
    private static int phantomMiningTicks = 0;
    private static int magicHandTicks = 0;
    private static boolean magicHandIsPassive = false;
    private static Runnable onStackChanged;
    private ClientMagicData() {}

    public static void setOnStackChanged(Runnable listener) { onStackChanged = listener; }
    public static void clearOnStackChanged() { onStackChanged = null; }

    public static void apply(StackChangedS2CPacket packet) {
        // Detect "cast" transition: previous active stack had a SpellType; new active stack doesn't.
        boolean wasValid = isStackValid(activeStackFor(stacks, activeIndex));

        List<List<SpellComponent>> newStacks = new ArrayList<>(packet.stacks().size());
        for (List<StackChangedS2CPacket.Entry> entryList : packet.stacks()) {
            List<SpellComponent> resolved = new ArrayList<>(entryList.size());
            for (StackChangedS2CPacket.Entry e : entryList) {
                SpellComponent c = StackChangedS2CPacket.resolve(e);
                if (c != null) {
                    resolved.add(c);
                } else if (Log.STACK_DEBUG) {
                    RunicRebirth.LOGGER.warn("[RunicRebirth] ClientMagicData dropped unresolved entry id={} kind={}", e.id(), e.kind());
                }
            }
            newStacks.add(resolved);
        }

        int newActive = packet.activeIndex();
        boolean isNowValid = isStackValid(activeStackFor(newStacks, newActive));

        // Valid → invalid (and the active index didn't change) means the stack was just cast+cleared.
        if (wasValid && !isNowValid && newActive == activeIndex) {
            castAnimTicksRemaining = CAST_ANIM_TICKS;
        }

        stacks = newStacks;
        stackElements = packet.stackElements() != null ? packet.stackElements() : Collections.emptyList();
        activeIndex = newActive;
        charges = packet.charges();

        if (Log.STACK_DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (int s = 0; s < stacks.size(); s++) {
                sb.append("stack[").append(s).append(']');
                for (SpellComponent c : stacks.get(s)) sb.append(' ').append(c.id());
                sb.append("  ");
            }
            RunicRebirth.LOGGER.info("[RunicRebirth] ClientMagicData synced: active={} | {}", activeIndex, sb);
        }

        if (onStackChanged != null) onStackChanged.run();
    }

    private static List<SpellComponent> activeStackFor(List<List<SpellComponent>> src, int idx) {
        if (src == null || src.isEmpty() || idx < 0 || idx >= src.size()) return Collections.emptyList();
        return src.get(idx);
    }

    private static boolean isStackValid(List<SpellComponent> stack) {
        for (SpellComponent c : stack) if (c instanceof SpellType) return true;
        return false;
    }

    public static void tickCastAnim() {
        if (castAnimTicksRemaining > 0) castAnimTicksRemaining--;
        if (ringCastAnimTicksRemaining > 0) ringCastAnimTicksRemaining--;
        if (!magicHandIsPassive && magicHandTicks > 0) magicHandTicks--;
        Iterator<Map.Entry<Integer, Integer>> it = remoteCastAnimTicks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> e = it.next();
            int remaining = e.getValue() - 1;
            if (remaining <= 0) it.remove();
            else e.setValue(remaining);
        }
        Iterator<Map.Entry<Integer, Integer>> rit = remoteRingCastAnimTicks.entrySet().iterator();
        while (rit.hasNext()) {
            Map.Entry<Integer, Integer> e = rit.next();
            int remaining = e.getValue() - 1;
            if (remaining <= 0) rit.remove();
            else e.setValue(remaining);
        }
    }

    public static boolean isCastAnimActive() {
        return castAnimTicksRemaining > 0;
    }

    public static void setRemoteCastAnim(int entityId, int ticks) {
        if (ticks <= 0) remoteCastAnimTicks.remove(entityId);
        else remoteCastAnimTicks.put(entityId, ticks);
    }

    public static boolean isCastAnimActiveFor(int entityId) {
        return remoteCastAnimTicks.getOrDefault(entityId, 0) > 0;
    }

    public static boolean isRingCastAnimActive() {
        return ringCastAnimTicksRemaining > 0;
    }

    public static void setRingCastAnim(int ticks) {
        ringCastAnimTicksRemaining = ticks;
    }

    public static void setRemoteRingCastAnim(int entityId, int ticks) {
        if (ticks <= 0) remoteRingCastAnimTicks.remove(entityId);
        else remoteRingCastAnimTicks.put(entityId, ticks);
    }

    public static boolean isRingCastAnimActiveFor(int entityId) {
        return remoteRingCastAnimTicks.getOrDefault(entityId, 0) > 0;
    }

    public static List<List<SpellComponent>> stacks() { return stacks; }

    public static int activeIndex() { return activeIndex; }

    public static List<SpellComponent> activeStack() {
        return activeStackFor(stacks, activeIndex);
    }

    public static boolean isActiveStackValid() {
        return isStackValid(activeStack());
    }

    public static boolean isHeldStackValid() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        net.minecraft.world.item.ItemStack held = mc.player.getMainHandItem();
        if (!(held.getItem() instanceof com.github.runicrebirth.items.SpellWriter)) return false;
        com.github.runicrebirth.api.spells.WandStacksData data = held.get(
            com.github.runicrebirth.init.ModDataComponents.WAND_STACKS.get());
        if (data == null || data.stacks().isEmpty()) return false;
        int idx = data.activeIndex();
        if (idx < 0 || idx >= data.stacks().size()) return false;
        com.github.runicrebirth.api.spells.WandStacksData.StackEntry entry = data.stacks().get(idx);
        for (com.github.runicrebirth.api.spells.WandStacksData.ComponentRef ref : entry.components()) {
            if (ref.kind() == com.github.runicrebirth.api.spells.WandStacksData.ComponentRef.KIND_TYPE) return true;
        }
        return false;
    }

    public static com.github.runicrebirth.api.spells.WandStacksData getHeldWandData() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return null;
        net.minecraft.world.item.ItemStack held = mc.player.getMainHandItem();
        if (!(held.getItem() instanceof com.github.runicrebirth.items.SpellWriter)) return null;
        return held.get(com.github.runicrebirth.init.ModDataComponents.WAND_STACKS.get());
    }

    public static ResourceLocation elementForStack(int idx) {
        if (idx < 0 || idx >= stackElements.size()) return null;
        return stackElements.get(idx);
    }

    public static int charges() { return charges; }

    public static boolean hasCharges() { return charges > 0; }

    public static int phantomMiningTicks() { return phantomMiningTicks; }
    public static void setPhantomMiningTicks(int ticks) { phantomMiningTicks = Math.max(0, ticks); }

    public static int magicHandTicks() { return magicHandTicks; }
    public static boolean magicHandIsPassive() { return magicHandIsPassive; }
    public static void setMagicHandSync(int ticks, boolean passive) {
        magicHandTicks = ticks;
        magicHandIsPassive = passive;
    }

    public static void setUnlockedSpells(Set<ResourceLocation> spells) {
        unlockedSpells = new HashSet<>(spells);
    }

    public static boolean isSpellUnlocked(ResourceLocation spellTypeId) {
        return unlockedSpells.contains(spellTypeId);
    }

    public static void applyCooldowns(Map<ResourceLocation, Integer> incoming) {
        for (Map.Entry<ResourceLocation, Integer> e : incoming.entrySet()) {
            ResourceLocation id = e.getKey();
            int rem = e.getValue();
            int prev = cooldownRemaining.getOrDefault(id, 0);
            if (rem > prev) cooldownMax.put(id, rem);
            cooldownRemaining.put(id, rem);
        }
        cooldownRemaining.keySet().retainAll(incoming.keySet());
    }

    public static Map<ResourceLocation, Integer> cooldownRemaining() {
        return Collections.unmodifiableMap(cooldownRemaining);
    }

    public static int cooldownMax(ResourceLocation id) {
        return cooldownMax.getOrDefault(id, 1);
    }

}
