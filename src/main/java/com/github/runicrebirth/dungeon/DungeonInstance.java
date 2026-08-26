package com.github.runicrebirth.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DungeonInstance {

    private static final int TIME_LIMIT_TICKS = 20 * 60 * 10; // 10 minutes
    private static final int DEATH_PENALTY_TICKS = 20 * 60;   // 1 minute

    private final UUID instanceId;
    private final ResourceLocation tierId;
    @Nullable
    private final ResourceLocation variantId;
    private final int difficulty;
    private final BlockPos origin;
    private final BlockPos returnPos;
    private final ResourceLocation returnDimension;
    private final Set<UUID> players = new HashSet<>();
    private List<DungeonModifier> modifiers = new ArrayList<>();
    private boolean completed;
    private boolean active;
    private int ticksAlive;
    private int remainingTicks;
    private boolean timerPaused = false;
    @Nullable
    private BlockPos entryPortalPos = null;

    public DungeonInstance(UUID instanceId, ResourceLocation tierId, @Nullable ResourceLocation variantId,
                           int difficulty, BlockPos origin, BlockPos returnPos,
                           ResourceLocation returnDimension, int durationTicks) {
        this.instanceId = instanceId;
        this.tierId = tierId;
        this.variantId = variantId;
        this.difficulty = difficulty;
        this.origin = origin;
        this.returnPos = returnPos;
        this.returnDimension = returnDimension;
        this.active = true;
        this.remainingTicks = durationTicks;
    }

    public UUID getInstanceId() { return instanceId; }
    public ResourceLocation getTierId() { return tierId; }
    @Nullable
    public ResourceLocation getVariantId() { return variantId; }
    public int getDifficulty() { return difficulty; }
    public BlockPos getOrigin() { return origin; }
    public BlockPos getReturnPos() { return returnPos; }
    public ResourceLocation getReturnDimension() { return returnDimension; }
    public Set<UUID> getPlayers() { return players; }
    public boolean isCompleted() { return completed; }
    public boolean isActive() { return active; }
    public List<DungeonModifier> getModifiers() { return modifiers; }
    public int getRemainingTicks() { return remainingTicks; }
    public boolean isTimerPaused() { return timerPaused; }
    public void setTimerPaused(boolean paused) { this.timerPaused = paused; }
    @Nullable
    public BlockPos getEntryPortalPos() { return entryPortalPos; }
    public void setEntryPortalPos(BlockPos pos) { this.entryPortalPos = pos; }
    public void setModifiers(List<DungeonModifier> modifiers) { this.modifiers = modifiers; }

    @Nullable
    public DungeonVariant getVariant() {
        return variantId != null ? DungeonVariantRegistry.get(variantId) : null;
    }

    public int getRemainingSeconds() {
        return Math.max(0, remainingTicks / 20);
    }

    public String getRemainingTimeFormatted() {
        int secs = getRemainingSeconds();
        return String.format("%d:%02d", secs / 60, secs % 60);
    }

    public boolean isTimedOut() {
        return remainingTicks <= 0;
    }

    public void applyDeathPenalty() {
        remainingTicks -= DEATH_PENALTY_TICKS;
    }

    public void addTime(int ticks) {
        remainingTicks += ticks;
    }

    public boolean hasModifier(DungeonModifier modifier) {
        return modifiers.contains(modifier);
    }

    public float getSpawnerCountMultiplier() {
        float mult = 1.0f;
        for (DungeonModifier mod : modifiers) mult *= mod.getSpawnerCountMultiplier();
        return mult;
    }

    public float getSharpTrapMultiplier() {
        float mult = 1.0f;
        for (DungeonModifier mod : modifiers) mult *= mod.getSharpTrapMultiplier();
        return mult;
    }

    public float getBluntTrapMultiplier() {
        float mult = 1.0f;
        for (DungeonModifier mod : modifiers) mult *= mod.getBluntTrapMultiplier();
        return mult;
    }

    public float getFireTrapMultiplier() {
        float mult = 1.0f;
        for (DungeonModifier mod : modifiers) mult *= mod.getFireTrapMultiplier();
        return mult;
    }

    public void addPlayer(UUID playerId) {
        players.add(playerId);
    }

    public void removePlayer(UUID playerId) {
        players.remove(playerId);
    }

    public void markCompleted() {
        this.completed = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void tick() {
        ticksAlive++;
        if (!completed && !timerPaused && remainingTicks > 0) {
            remainingTicks--;
        }
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public static int getDefaultDurationTicks() {
        return TIME_LIMIT_TICKS;
    }
}
