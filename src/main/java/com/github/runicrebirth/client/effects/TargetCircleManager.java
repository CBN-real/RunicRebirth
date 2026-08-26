package com.github.runicrebirth.client.effects;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.entities.spells.AbstractCircleEntity;
import com.github.runicrebirth.entities.spells.AoeTrackerEntity;
import com.github.runicrebirth.entities.spells.EnergyCracklingEntity;
import com.github.runicrebirth.entities.spells.TargetCircleEntity;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.items.SpellWriter;
import com.github.runicrebirth.util.RaycastBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public final class TargetCircleManager {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");

    /** Native radius of aoe_tracker.geo.json disc in blocks (13.5 Bedrock units / 16). */
    private static final float AOE_MODEL_RADIUS = 13.5f / 16.0f;

    private static final String TEAM_NAME = "runicrebirth_target";
    private static final int CRACKLING_COLOR = 0x8855FF;
    private static final double LERP_STIFFNESS = 14.0;

    private static final @Nullable Method SET_SHARED_FLAG;
    static {
        Method m = null;
        try {
            m = Entity.class.getDeclaredMethod("setSharedFlag", int.class, boolean.class);
            m.setAccessible(true);
        } catch (Exception ignored) {}
        SET_SHARED_FLAG = m;
    }

    // Look-target slot (one circle at whatever the player is aiming at)
    @Nullable private static TargetCircleEntity s_lookCircle = null;
    @Nullable private static EnergyCracklingEntity s_lookCrackling = null;
    @Nullable private static Vec3 s_lookDisplayPos = null;
    private static long s_lookLastNanos = 0;
    @Nullable private static Entity s_lookOutlineTarget = null;
    @Nullable private static AoeTrackerEntity s_lookAoeTracker = null;

    // One slot per AbstractCircleEntity that has an active entity target, keyed by circle entity ID
    private static final Map<Integer, SpellSlot> s_spellSlots = new HashMap<>();

    private static boolean s_hidden = false;

    public static void toggleHidden() { s_hidden = !s_hidden; }

    private TargetCircleManager() {}

    private static class SpellSlot {
        TargetCircleEntity circle;
        EnergyCracklingEntity crackling;
        Vec3 displayPos;
        long lastNanos;
        Entity outlineTarget;
        AoeTrackerEntity aoeTracker;
    }

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        LocalPlayer player = mc.player;

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            float pt = event.getPartialTick().getGameTimeDeltaPartialTick(false);

            // --- Look-target slot ---
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            boolean mainHandValid = mainHand.getItem() instanceof SpellWriter
                && (SpellWriter.resolveActiveStack(mainHand).validSpell() || ClientMagicData.hasCharges());
            boolean offHandValid = offHand.getItem() instanceof SpellWriter
                && (SpellWriter.resolveActiveStack(offHand).validSpell() || ClientMagicData.hasCharges());
            boolean holdsWriter = mainHandValid || offHandValid;

            if (holdsWriter) {
                ItemStack rangeSource = mainHandValid ? mainHand : offHand;
                float aoeRadius = SpellWriter.resolveActiveAoeRadius(rangeSource);
                com.github.runicrebirth.api.spells.SpellType activeType = SpellWriter.resolveActiveType(rangeSource);
                double spellRange = (activeType != null && activeType.bypassesRangeCheck())
                    ? 64.0
                    : SpellWriter.resolveActiveRange(rangeSource);
                Vec3 eye = player.getEyePosition(pt);
                Vec3 dir = player.getViewVector(pt);
                Vec3 end = eye.add(dir.scale(spellRange));
                HitResult hit = RaycastBuilder.begin(mc.level, player)
                    .start(eye).end(end).checkForBlocks(true).inflate(1.0f).cast();

                Entity lookEntity = null;
                Vec3 lookPos = null;
                float lookScale = 1.0f;

                if (hit instanceof EntityHitResult ehr) {
                    lookEntity = ehr.getEntity();
                    lookPos = new Vec3(
                        Mth.lerp(pt, lookEntity.xOld, lookEntity.getX()),
                        Mth.lerp(pt, lookEntity.yOld, lookEntity.getY()),
                        Mth.lerp(pt, lookEntity.zOld, lookEntity.getZ()));
                    lookScale = 1.0f + (lookEntity.getBbHeight() / 4.0f);
                } else if (hit instanceof BlockHitResult bhr && bhr.getType() != HitResult.Type.MISS) {
                    BlockPos bp = bhr.getBlockPos();
                    lookPos = new Vec3(bp.getX() + 0.5, bp.getY() + 1.001, bp.getZ() + 0.5);
                }

                if (lookPos != null) {
                    long nowNanos = System.nanoTime();
                    double dt = s_lookLastNanos == 0 ? 0.0 : Math.min((nowNanos - s_lookLastNanos) * 1e-9, 0.1);
                    s_lookLastNanos = nowNanos;
                    double alpha = dt > 0 ? 1.0 - Math.exp(-LERP_STIFFNESS * dt) : 1.0;
                    s_lookDisplayPos = s_lookDisplayPos == null ? lookPos : s_lookDisplayPos.lerp(lookPos, alpha);

                    if (s_lookCircle == null) s_lookCircle = new TargetCircleEntity(ModEntities.TARGET_CIRCLE.get(), mc.level);
                    s_lookCircle.setPos(s_lookDisplayPos.x, s_lookDisplayPos.y, s_lookDisplayPos.z);
                    s_lookCircle.setTargetScale(lookScale);
                    s_lookCircle.tickCount = (int) mc.level.getGameTime();

                    if (s_lookCrackling == null)
                        s_lookCrackling = new EnergyCracklingEntity(mc.level, 0.45f, CRACKLING_COLOR, 1, 0.22f, 0.8f, 0.6f);
                    s_lookCrackling.tickCount = (int) mc.level.getGameTime();

                    if (aoeRadius > 0) {
                        if (s_lookAoeTracker == null)
                            s_lookAoeTracker = new AoeTrackerEntity(ModEntities.AOE_TRACKER.get(), mc.level);
                        s_lookAoeTracker.setPos(s_lookDisplayPos.x, s_lookDisplayPos.y, s_lookDisplayPos.z);
                        s_lookAoeTracker.setAoeScale(aoeRadius / AOE_MODEL_RADIUS);
                        s_lookAoeTracker.tickCount = (int) mc.level.getGameTime();
                    } else {
                        s_lookAoeTracker = null;
                    }

                    if (lookEntity != s_lookOutlineTarget) {
                        if (s_lookOutlineTarget != null) removeOutlineIfUnused(mc.level, s_lookOutlineTarget, null);
                        s_lookOutlineTarget = lookEntity;
                        if (s_lookOutlineTarget != null) addOutline(mc.level, s_lookOutlineTarget);
                    }
                } else {
                    clearLookSlot(mc.level);
                }
            } else {
                clearLookSlot(mc.level);
            }

            // --- Spell-target slots (one per active AbstractCircleEntity with entity target) ---
            Set<Integer> activeSpellIds = new HashSet<>();
            for (Entity e : mc.level.entitiesForRendering()) {
                if (!(e instanceof AbstractCircleEntity circle)) continue;
                int tid = circle.getTargetEntityId();
                if (tid == -1) continue;
                Entity target = mc.level.getEntity(tid);
                if (target == null) continue;

                int cid = circle.getId();
                activeSpellIds.add(cid);

                Vec3 targetPos = new Vec3(
                    Mth.lerp(pt, target.xOld, target.getX()),
                    Mth.lerp(pt, target.yOld, target.getY()),
                    Mth.lerp(pt, target.zOld, target.getZ()));
                float scale = 1.0f + (target.getBbHeight() / 4.0f);

                SpellSlot slot = s_spellSlots.computeIfAbsent(cid, k -> new SpellSlot());

                long nowNanos = System.nanoTime();
                double dt = slot.lastNanos == 0 ? 0.0 : Math.min((nowNanos - slot.lastNanos) * 1e-9, 0.1);
                slot.lastNanos = nowNanos;
                double alpha = dt > 0 ? 1.0 - Math.exp(-LERP_STIFFNESS * dt) : 1.0;
                slot.displayPos = slot.displayPos == null ? targetPos : slot.displayPos.lerp(targetPos, alpha);

                if (slot.circle == null) slot.circle = new TargetCircleEntity(ModEntities.TARGET_CIRCLE.get(), mc.level);
                slot.circle.setPos(slot.displayPos.x, slot.displayPos.y, slot.displayPos.z);
                slot.circle.setTargetScale(scale);
                slot.circle.tickCount = (int) mc.level.getGameTime();

                if (slot.crackling == null)
                    slot.crackling = new EnergyCracklingEntity(mc.level, 0.45f, CRACKLING_COLOR, 1, 0.22f, 0.8f, 0.6f);
                slot.crackling.tickCount = (int) mc.level.getGameTime();

                String spellTypeId = circle.getSpellTypeId();
                float slotAoeRadius = 0f;
                if (!spellTypeId.isEmpty()) {
                    SpellType st = SpellTypeRegistry.get(
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, spellTypeId));
                    if (st != null) slotAoeRadius = st.baseAoeRadius() * circle.getCircleScale();
                }
                if (slotAoeRadius > 0) {
                    if (slot.aoeTracker == null)
                        slot.aoeTracker = new AoeTrackerEntity(ModEntities.AOE_TRACKER.get(), mc.level);
                    slot.aoeTracker.setPos(slot.displayPos.x, slot.displayPos.y, slot.displayPos.z);
                    slot.aoeTracker.setAoeScale(slotAoeRadius / AOE_MODEL_RADIUS);
                    slot.aoeTracker.tickCount = (int) mc.level.getGameTime();
                } else {
                    slot.aoeTracker = null;
                }

                if (target != slot.outlineTarget) {
                    if (slot.outlineTarget != null) removeOutlineIfUnused(mc.level, slot.outlineTarget, cid);
                    slot.outlineTarget = target;
                    addOutline(mc.level, slot.outlineTarget);
                }
            }

            // Remove stale spell slots
            Iterator<Map.Entry<Integer, SpellSlot>> it = s_spellSlots.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, SpellSlot> entry = it.next();
                if (!activeSpellIds.contains(entry.getKey())) {
                    SpellSlot slot = entry.getValue();
                    it.remove(); // remove first so isEntityUsedAsOutline sees updated map
                    if (slot.outlineTarget != null) removeOutlineIfUnused(mc.level, slot.outlineTarget, null);
                }
            }

            // Apply glow flags: look target = solid; spell-only targets = blinking
            if (s_lookOutlineTarget != null) setGlowFlag(s_lookOutlineTarget, true);
            long t = mc.level.getGameTime();
            boolean blink = (t % 10) < 5;
            for (SpellSlot slot : s_spellSlots.values()) {
                if (slot.outlineTarget != null && slot.outlineTarget != s_lookOutlineTarget && blink) {
                    setGlowFlag(slot.outlineTarget, true);
                }
            }

        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            // Clear all glow flags
            if (s_lookOutlineTarget != null) setGlowFlag(s_lookOutlineTarget, false);
            for (SpellSlot slot : s_spellSlots.values()) {
                if (slot.outlineTarget != null) setGlowFlag(slot.outlineTarget, false);
            }

            Vec3 camPos = event.getCamera().getPosition();
            MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
            float pt = event.getPartialTick().getGameTimeDeltaPartialTick(false);

            if (!s_hidden) {
                if (s_lookCircle != null && s_lookDisplayPos != null) {
                    renderSlot(s_lookCircle, s_lookCrackling, s_lookDisplayPos, event, camPos, buf, pt);
                    renderAoeTracker(s_lookAoeTracker, s_lookDisplayPos, event, camPos, buf, pt);
                }
                for (SpellSlot slot : s_spellSlots.values()) {
                    if (slot.circle != null && slot.displayPos != null) {
                        renderSlot(slot.circle, slot.crackling, slot.displayPos, event, camPos, buf, pt);
                        renderAoeTracker(slot.aoeTracker, slot.displayPos, event, camPos, buf, pt);
                    }
                }
            }
        }
    }

    private static void renderAoeTracker(@Nullable AoeTrackerEntity aoeTracker, Vec3 pos,
                                         RenderLevelStageEvent event, Vec3 camPos,
                                         MultiBufferSource.BufferSource buf, float pt) {
        if (aoeTracker == null) return;
        Minecraft mc = Minecraft.getInstance();
        event.getPoseStack().pushPose();
        event.getPoseStack().translate(pos.x - camPos.x, pos.y - camPos.y, pos.z - camPos.z);
        float sc = aoeTracker.getAoeScale();
        event.getPoseStack().scale(sc, sc, sc);
        @SuppressWarnings("unchecked")
        EntityRenderer<AoeTrackerEntity> renderer =
            (EntityRenderer<AoeTrackerEntity>) mc.getEntityRenderDispatcher().getRenderer(aoeTracker);
        renderer.render(aoeTracker, 0f, pt, event.getPoseStack(), buf, LightTexture.FULL_BRIGHT);
        event.getPoseStack().popPose();
        buf.endBatch(ModRenderTypes.entityTranslucentNoCullNoShade(TEXTURE));
    }

    private static void renderSlot(TargetCircleEntity circle, @Nullable EnergyCracklingEntity crackling,
                                   Vec3 pos, RenderLevelStageEvent event, Vec3 camPos,
                                   MultiBufferSource.BufferSource buf, float pt) {
        Minecraft mc = Minecraft.getInstance();

        event.getPoseStack().pushPose();
        event.getPoseStack().translate(pos.x - camPos.x, pos.y - camPos.y, pos.z - camPos.z);
        float sc = circle.getTargetScale();
        event.getPoseStack().scale(sc, sc, sc);
        @SuppressWarnings("unchecked")
        EntityRenderer<TargetCircleEntity> circleRenderer =
            (EntityRenderer<TargetCircleEntity>) mc.getEntityRenderDispatcher().getRenderer(circle);
        circleRenderer.render(circle, 0f, pt, event.getPoseStack(), buf, LightTexture.FULL_BRIGHT);
        event.getPoseStack().popPose();
        buf.endBatch(ModRenderTypes.entityTranslucentNoCullNoShade(TEXTURE));

        if (crackling != null) {
            event.getPoseStack().pushPose();
            event.getPoseStack().translate(pos.x - camPos.x, pos.y - camPos.y, pos.z - camPos.z);
            @SuppressWarnings("unchecked")
            EntityRenderer<EnergyCracklingEntity> cracklingRenderer =
                (EntityRenderer<EnergyCracklingEntity>) mc.getEntityRenderDispatcher().getRenderer(crackling);
            cracklingRenderer.render(crackling, 0f, pt, event.getPoseStack(), buf, LightTexture.FULL_BRIGHT);
            event.getPoseStack().popPose();
            buf.endBatch(RenderType.lightning());
        }
    }

    private static void clearLookSlot(ClientLevel level) {
        if (s_lookOutlineTarget != null) {
            removeOutlineIfUnused(level, s_lookOutlineTarget, null);
            s_lookOutlineTarget = null;
        }
        s_lookCircle = null;
        s_lookCrackling = null;
        s_lookDisplayPos = null;
        s_lookLastNanos = 0;
        s_lookAoeTracker = null;
    }

    /** Remove entity from team only if no other active slot (excluding skipSpellId) still references it. */
    private static void removeOutlineIfUnused(ClientLevel level, Entity entity, @Nullable Integer skipSpellId) {
        if (entity == s_lookOutlineTarget) return;
        for (Map.Entry<Integer, SpellSlot> entry : s_spellSlots.entrySet()) {
            if (skipSpellId != null && entry.getKey().equals(skipSpellId)) continue;
            if (entry.getValue().outlineTarget == entity) return;
        }
        setGlowFlag(entity, false);
        Scoreboard sb = level.getScoreboard();
        PlayerTeam team = sb.getPlayerTeam(TEAM_NAME);
        if (team != null) sb.removePlayerFromTeam(entity.getScoreboardName(), team);
    }

    private static void addOutline(ClientLevel level, Entity entity) {
        ensureRedTeam(level);
        level.getScoreboard().addPlayerToTeam(
            entity.getScoreboardName(),
            level.getScoreboard().getPlayerTeam(TEAM_NAME));
    }

    private static void setGlowFlag(Entity entity, boolean glow) {
        if (SET_SHARED_FLAG == null) return;
        try {
            SET_SHARED_FLAG.invoke(entity, 6, glow);
        } catch (Exception ignored) {}
    }

    private static void ensureRedTeam(ClientLevel level) {
        Scoreboard sb = level.getScoreboard();
        if (sb.getPlayerTeam(TEAM_NAME) != null) return;
        PlayerTeam team = sb.addPlayerTeam(TEAM_NAME);
        team.setColor(ChatFormatting.RED);
        team.setNameTagVisibility(Team.Visibility.NEVER);
        team.setDeathMessageVisibility(Team.Visibility.NEVER);
    }
}
