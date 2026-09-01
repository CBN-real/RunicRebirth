package com.github.runicrebirth.capabilities.magic;

import com.github.runicrebirth.api.spells.SpellStack;
import com.github.runicrebirth.init.ModAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

/**
 * Per-player magic runtime state. Attached via NeoForge data attachments.
 * Server is authoritative; clients see a mirror maintained by StackChangedS2CPacket.
 *
 * Persistent: cooldowns.
 * Transient: stacks[], activeStackIndex, globalCastLockoutTicks, drawing flag (rebuilt each session).
 */
public class MagicData {

    public static final MapCodec<MagicData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.unboundedMap(Identifier.CODEC, Codec.INT).fieldOf("cooldowns").forGetter(d -> d.cooldowns),
        Codec.INT.optionalFieldOf("phantom_mining_ticks", 0).forGetter(d -> d.phantomMiningTicks)
    ).apply(instance, MagicData::fromCodec));

    private final Map<Identifier, Integer> cooldowns;

    private int globalCastLockoutTicks;
    private boolean drawing;
    private int canvasEntityId = -1;
    private int phantomMiningTicks;
    private boolean phantomMiningJustExpired;
    private int magicHandEntityId = -1;
    private int arcaneDroneEntityId = -1;
    private int hammerDroneEntityId = -1;
    private int thrusterActiveTicks;
    private boolean glidingActive;


    public MagicData() {
        this.cooldowns = new HashMap<>();
        this.globalCastLockoutTicks = 0;
        this.drawing = false;
    }

    private static MagicData fromCodec(Map<Identifier, Integer> cooldowns, int phantomMiningTicks) {
        MagicData d = new MagicData();
        d.cooldowns.putAll(cooldowns);
        d.phantomMiningTicks = Math.max(0, phantomMiningTicks);
        return d;
    }

    public static MagicData of(Player player) {
        return player.getData(ModAttachments.MAGIC_DATA);
    }

    // Cooldowns
    public boolean isOnCooldown(Identifier id) {
        return this.cooldowns.getOrDefault(id, 0) > 0;
    }

    public int remainingCooldownTicks(Identifier id) {
        return this.cooldowns.getOrDefault(id, 0);
    }

    public void startCooldown(Identifier id, int ticks) {
        this.cooldowns.put(id, ticks);
    }

    public Map<Identifier, Integer> cooldowns() {
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

    // Phantom Mining effect
    public int phantomMiningTicks() { return phantomMiningTicks; }
    public void setPhantomMiningTicks(int ticks) { this.phantomMiningTicks = Math.max(0, ticks); }
    public boolean consumePhantomMiningExpired() {
        boolean v = phantomMiningJustExpired;
        phantomMiningJustExpired = false;
        return v;
    }

    // Magic hand ring — transient, not persisted
    public int magicHandEntityId() { return magicHandEntityId; }
    public void setMagicHandEntityId(int id) { magicHandEntityId = id; }
    public void clearMagicHandEntityId() { magicHandEntityId = -1; }

    // Arcane drone — transient, not persisted
    public int arcaneDroneEntityId() { return arcaneDroneEntityId; }
    public void setArcaneDroneEntityId(int id) { arcaneDroneEntityId = id; }
    public void clearArcaneDroneEntityId() { arcaneDroneEntityId = -1; }

    // Hammer drone — transient, not persisted
    public int hammerDroneEntityId() { return hammerDroneEntityId; }
    public void setHammerDroneEntityId(int id) { hammerDroneEntityId = id; }
    public void clearHammerDroneEntityId() { hammerDroneEntityId = -1; }

    // Thrown runic dagger — transient, not persisted
    private int thrownDaggerEntityId = -1;

    public int thrownDaggerEntityId() { return thrownDaggerEntityId; }
    public void setThrownDaggerEntityId(int id) { thrownDaggerEntityId = id; }
    public void clearThrownDaggerEntityId() { thrownDaggerEntityId = -1; }

    // Whirlwind — transient, not persisted
    private int whirlwindTicksRemaining;
    private int whirlwindWavesFired;
    private float whirlwindDamage;

    public void startWhirlwind(float damage) {
        this.whirlwindTicksRemaining = 60;
        this.whirlwindWavesFired = 0;
        this.whirlwindDamage = damage;
    }

    public float whirlwindDamage() { return whirlwindDamage; }
    public boolean isWhirlwindActive() { return whirlwindTicksRemaining > 0; }

    public boolean tickWhirlwind() {
        if (whirlwindTicksRemaining <= 0) return false;
        whirlwindTicksRemaining--;
        if (whirlwindTicksRemaining % 20 == 0 && whirlwindWavesFired < 3) {
            whirlwindWavesFired++;
            return true;
        }
        return false;
    }

    // Thruster ring
    public int thrusterActiveTicks() { return thrusterActiveTicks; }
    public void setThrusterActiveTicks(int ticks) { thrusterActiveTicks = Math.max(0, ticks); }

    // Gliding ring
    public boolean isGlidingActive() { return glidingActive; }
    public void setGlidingActive(boolean active) { glidingActive = active; }

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
        if (phantomMiningTicks > 0) {
            phantomMiningTicks--;
            if (phantomMiningTicks == 0) phantomMiningJustExpired = true;
        }
        if (thrusterActiveTicks > 0) thrusterActiveTicks--;
    }
}
