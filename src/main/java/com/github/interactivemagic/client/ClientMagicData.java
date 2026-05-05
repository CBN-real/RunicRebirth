package com.github.interactivemagic.client;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.spells.SpellComponent;
import com.github.interactivemagic.api.spells.SpellType;
import com.github.interactivemagic.network.StackChangedS2CPacket;
import com.github.interactivemagic.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private static int activeIndex = 0;
    private static int castAnimTicksRemaining = 0;
    private static int charges = 0;
    private static boolean castingInProgress = false;

    private ClientMagicData() {}

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
                    InteractiveMagic.LOGGER.warn("[InteractiveMagic] ClientMagicData dropped unresolved entry id={} kind={}", e.id(), e.kind());
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
        activeIndex = newActive;
        charges = packet.charges();
        castingInProgress = packet.castingInProgress();

        if (Log.STACK_DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (int s = 0; s < stacks.size(); s++) {
                sb.append("stack[").append(s).append(']');
                for (SpellComponent c : stacks.get(s)) sb.append(' ').append(c.id());
                sb.append("  ");
            }
            InteractiveMagic.LOGGER.info("[InteractiveMagic] ClientMagicData synced: active={} | {}", activeIndex, sb);
        }
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

    public static int charges() { return charges; }

    public static boolean hasCharges() { return charges > 0; }

    public static boolean isCastingInProgress() { return castingInProgress; }
}
