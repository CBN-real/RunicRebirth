package com.github.runicrebirth.capabilities.magic;

import com.github.runicrebirth.api.spells.SpellComponent;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellStack;
import com.github.runicrebirth.init.ModAttachments;
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

    private int globalCastLockoutTicks;
    private boolean drawing;
    private int canvasEntityId = -1;

    private int charges;
    private ResourceLocation chargedSpellId;
    private SpellParams chargedParams;
    public MagicData() {
        this.cooldowns = new HashMap<>();
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

    public int canvasEntityId() { return canvasEntityId; }
    public void setCanvasEntityId(int id) { this.canvasEntityId = id; }
    public void clearCanvasEntityId() { this.canvasEntityId = -1; }

    // Pending circuit inscription (transient, not persisted)
    private SpellStack pendingCircuitSpell;

    public SpellStack pendingCircuitSpell() { return pendingCircuitSpell; }

    public SpellStack getOrCreatePendingCircuit() {
        if (pendingCircuitSpell == null) pendingCircuitSpell = new SpellStack();
        return pendingCircuitSpell;
    }

    public void clearPendingCircuit() { pendingCircuitSpell = null; }

    public boolean hasPendingCircuit() {
        return pendingCircuitSpell != null && !pendingCircuitSpell.isEmpty();
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
