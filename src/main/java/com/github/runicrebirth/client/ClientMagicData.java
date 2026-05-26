package com.github.runicrebirth.client;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.spells.SpellComponent;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.network.StackChangedS2CPacket;
import com.github.runicrebirth.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Client-side mirror of SpellStacks sync'd from server via StackChangedS2CPacket.
 * HUD + canvas + wand animation read from this.
 */
@OnlyIn(Dist.CLIENT)
public final class ClientMagicData {

    private static final int CAST_ANIM_TICKS = 20;

    private static List<List<SpellComponent>> stacks = Collections.emptyList();
    private static List<ResourceLocation> stackElements = Collections.emptyList();
    private static int activeIndex = 0;
    private static int castAnimTicksRemaining = 0;
    private static int charges = 0;
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
    }

    public static boolean isCastAnimActive() {
        return castAnimTicksRemaining > 0;
    }

    public static List<List<SpellComponent>> stacks() { return stacks; }

    public static int activeIndex() { return activeIndex; }

    public static List<SpellComponent> activeStack() {
        return activeStackFor(stacks, activeIndex);
    }

    public static boolean isActiveStackValid() {
        return isStackValid(activeStack());
    }

    public static ResourceLocation elementForStack(int idx) {
        if (idx < 0 || idx >= stackElements.size()) return null;
        return stackElements.get(idx);
    }

    public static int charges() { return charges; }

    public static boolean hasCharges() { return charges > 0; }

}
