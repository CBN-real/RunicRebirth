package com.github.interactivemagic.capabilities.magic;

import com.github.interactivemagic.api.spells.SpellParams;
import com.github.interactivemagic.api.spells.SpellStack;
import com.github.interactivemagic.init.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Per-player magic runtime state. Attached via NeoForge data attachments.
 * Server is authoritative; clients see a mirror maintained by StackChangedS2CPacket.
 *
 * Persistent: cooldowns.
 * Transient: stacks[], activeStackIndex, globalCastLockoutTicks, drawing flag (rebuilt each session).
 */
public class MagicData {

    public static final Codec<MagicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("cooldowns").forGetter(d -> d.cooldowns)
    ).apply(instance, MagicData::fromCodec));

    private final Map<ResourceLocation, Integer> cooldowns;

    private SpellStack[] stacks;
    private int activeStackIndex;
    private int globalCastLockoutTicks;
    private boolean drawing;

    private int charges;
    private ResourceLocation chargedSpellId;
    private SpellParams chargedParams;
    public MagicData() {
        this.cooldowns = new HashMap<>();
        this.stacks = null;
        this.activeStackIndex = 0;
        this.globalCastLockoutTicks = 0;
        this.drawing = false;
    }

    private static MagicData fromCodec(Map<ResourceLocation, Integer> cooldowns) {
        MagicData d = new MagicData();
        d.cooldowns.putAll(cooldowns);
        return d;
    }

    public static MagicData of(Player player) {
        return player.getData(ModAttachments.MAGIC_DATA);
    }

    // Cooldowns
    public boolean isOnCooldown(ResourceLocation id) {
        return this.cooldowns.getOrDefault(id, 0) > 0;
    }

    public int remainingCooldownTicks(ResourceLocation id) {
        return this.cooldowns.getOrDefault(id, 0);
    }

    public void startCooldown(ResourceLocation id, int ticks) {
        this.cooldowns.put(id, ticks);
    }

    public Map<ResourceLocation, Integer> cooldowns() {
        return this.cooldowns;
    }

    // Lockout
    public int globalCastLockoutTicks() { return globalCastLockoutTicks; }
    public void setGlobalCastLockout(int ticks) { this.globalCastLockoutTicks = Math.max(0, ticks); }

    // Drawing flag
    public boolean isDrawing() { return drawing; }
    public void setDrawing(boolean v) { this.drawing = v; }

    // Stacks
    public void ensureStacks(int count) {
        if (stacks == null || stacks.length != count) {
            SpellStack[] fresh = new SpellStack[count];
            for (int i = 0; i < count; i++) fresh[i] = new SpellStack();
            stacks = fresh;
            activeStackIndex = 0;
        }
    }

    public SpellStack[] stacks() { return stacks; }

    public SpellStack activeStack() {
        return stacks == null ? null : stacks[activeStackIndex];
    }

    public int activeStackIndex() { return activeStackIndex; }

    public void cycleActiveStack() {
        if (stacks == null || stacks.length == 0) return;
        activeStackIndex = (activeStackIndex + 1) % stacks.length;
    }

    public void setActiveStackIndex(int i) {
        if (stacks != null && i >= 0 && i < stacks.length) activeStackIndex = i;
    }

    // Charges
    public int charges() { return charges; }
    public boolean hasCharges() { return charges > 0; }
    public ResourceLocation chargedSpellId() { return chargedSpellId; }
    public SpellParams chargedParams() { return chargedParams; }

    public void setCharges(int count, ResourceLocation spellId, SpellParams params) {
        this.charges = count;
        this.chargedSpellId = spellId;
        this.chargedParams = params.copy();
    }

    public void consumeCharge() {
        if (charges > 0) charges--;
        if (charges <= 0) {
            chargedSpellId = null;
            chargedParams = null;
        }
    }

    public void clearCharges() {
        charges = 0;
        chargedSpellId = null;
        chargedParams = null;
    }

    public void tick() {
        if (!cooldowns.isEmpty()) {
            cooldowns.entrySet().removeIf(e -> {
                int r = e.getValue() - 1;
                if (r <= 0) return true;
                e.setValue(r);
                return false;
            });
        }
        if (globalCastLockoutTicks > 0) globalCastLockoutTicks--;
    }
}
