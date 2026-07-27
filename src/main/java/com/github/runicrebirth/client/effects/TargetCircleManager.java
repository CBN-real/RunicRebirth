package com.github.runicrebirth.client.effects;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.renderers.ModRenderTypes;
import com.github.runicrebirth.entities.spells.AbstractCircleEntity;
import com.github.runicrebirth.entities.spells.EnergyCracklingEntity;
import com.github.runicrebirth.entities.spells.TargetCircleEntity;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.items.SpellWriter;
import com.github.runicrebirth.util.RaycastBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
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

@OnlyIn(Dist.CLIENT)
public final class TargetCircleManager {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        RunicRebirth.MODID, "textures/entity/runic_templates/arcane_runic_template.png");

    private static final String TEAM_NAME = "runicrebirth_target";

    private static final int CRACKLING_COLOR = 0x8855FF;

    private static final @Nullable Method SET_SHARED_FLAG;
    static {
        Method m = null;
        try {
            m = Entity.class.getDeclaredMethod("setSharedFlag", int.class, boolean.class);
            m.setAccessible(true);
        } catch (Exception ignored) {}
        SET_SHARED_FLAG = m;
    }

    @Nullable private static TargetCircleEntity s_circle = null;
    @Nullable private static EnergyCracklingEntity s_crackling = null;
    @Nullable private static Entity s_outlineTarget = null;
    @Nullable private static Vec3 s_circlePos = null;
    private static boolean s_isLookTarget = false;

    private TargetCircleManager() {}

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        LocalPlayer player = mc.player;

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            boolean holdsWriter = player.getMainHandItem().getItem() instanceof SpellWriter
                || player.getOffhandItem().getItem() instanceof SpellWriter;

            float pt = event.getPartialTick().getGameTimeDeltaPartialTick(false);

            // --- look-raycast target ---
            Entity lookEntity = null;
            Vec3 lookPos = null;
            float lookScale = 1.0f;

            if (holdsWriter) {
                Vec3 eye = player.getEyePosition(pt);
                Vec3 dir = player.getViewVector(pt);
                Vec3 end = eye.add(dir.scale(64.0));

                HitResult hit = RaycastBuilder.begin(mc.level, player)
                    .start(eye).end(end)
                    .checkForBlocks(true)
                    .inflate(1.0f)
                    .cast();

                if (hit instanceof EntityHitResult ehr) {
                    lookEntity = ehr.getEntity();
                    lookPos = new Vec3(lookEntity.getX(), lookEntity.getY(), lookEntity.getZ());
                    lookScale = 1.0f + (lookEntity.getBbHeight() / 4.0f);
                } else if (hit instanceof BlockHitResult bhr && bhr.getType() != HitResult.Type.MISS) {
                    BlockPos bp = bhr.getBlockPos();
                    lookPos = new Vec3(bp.getX() + 0.5, bp.getY() + 1.001, bp.getZ() + 0.5);
                }
            }

            // --- find active spell circle targeting an entity ---
            Entity spellTargetEntity = null;
            for (Entity e : mc.level.entitiesForRendering()) {
                if (e instanceof AbstractCircleEntity circle) {
                    int tid = circle.getTargetEntityId();
                    if (tid != -1) {
                        Entity target = mc.level.getEntity(tid);
                        if (target != null) {
                            spellTargetEntity = target;
                            break;
                        }
                    }
                }
            }

            // --- determine final target ---
            Entity finalOutline;
            Vec3 finalPos;
            float finalScale;

            if (lookEntity != null) {
                finalOutline = lookEntity;
                finalPos = lookPos;
                finalScale = lookScale;
                s_isLookTarget = true;
            } else if (spellTargetEntity != null) {
                finalOutline = spellTargetEntity;
                finalPos = new Vec3(spellTargetEntity.getX(), spellTargetEntity.getY(), spellTargetEntity.getZ());
                finalScale = 1.0f + (spellTargetEntity.getBbHeight() / 4.0f);
                s_isLookTarget = false;
            } else if (lookPos != null) {
                finalOutline = null;
                finalPos = lookPos;
                finalScale = 1.0f;
                s_isLookTarget = true;
            } else {
                clearOutline(mc.level);
                s_circle = null;
                s_circlePos = null;
                s_crackling = null;
                s_isLookTarget = false;
                return;
            }

            s_circlePos = finalPos;

            if (s_circle == null) {
                s_circle = new TargetCircleEntity(ModEntities.TARGET_CIRCLE.get(), mc.level);
            }
            s_circle.setPos(finalPos.x, finalPos.y, finalPos.z);
            s_circle.setTargetScale(finalScale);
            s_circle.tickCount = (int)(mc.level.getGameTime());

            if (s_crackling == null) {
                s_crackling = new EnergyCracklingEntity(mc.level, 0.45f, CRACKLING_COLOR, 1, 0.22f, 0.8f, 0.6f);
            }
            s_crackling.tickCount = (int)(mc.level.getGameTime());

            if (finalOutline != s_outlineTarget) {
                clearOutline(mc.level);
                s_outlineTarget = finalOutline;
                if (s_outlineTarget != null) {
                    ensureRedTeam(mc.level);
                    mc.level.getScoreboard().addPlayerToTeam(
                        s_outlineTarget.getScoreboardName(),
                        mc.level.getScoreboard().getPlayerTeam(TEAM_NAME));
                }
            }

            // Solid outline when looking; blink every 5/10 ticks when spell-targeting only
            if (s_outlineTarget != null) {
                long t = mc.level.getGameTime();
                boolean shouldGlow = s_isLookTarget || ((t % 10) < 5);
                if (shouldGlow) {
                    setGlowFlag(s_outlineTarget, true);
                }
            }

        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            if (s_outlineTarget != null) {
                setGlowFlag(s_outlineTarget, false);
            }

            if (s_circle == null || s_circlePos == null) return;

            Vec3 camPos = event.getCamera().getPosition();
            MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
            float pt = event.getPartialTick().getGameTimeDeltaPartialTick(false);

            // Target circle
            event.getPoseStack().pushPose();
            event.getPoseStack().translate(
                s_circlePos.x - camPos.x,
                s_circlePos.y - camPos.y,
                s_circlePos.z - camPos.z);
            float sc = s_circle.getTargetScale();
            event.getPoseStack().scale(sc, sc, sc);

            @SuppressWarnings("unchecked")
            EntityRenderer<TargetCircleEntity> circleRenderer =
                (EntityRenderer<TargetCircleEntity>) mc.getEntityRenderDispatcher().getRenderer(s_circle);
            circleRenderer.render(s_circle, 0f, pt, event.getPoseStack(), buf, LightTexture.FULL_BRIGHT);

            event.getPoseStack().popPose();
            buf.endBatch(ModRenderTypes.entityTranslucentNoCullNoShade(TEXTURE));

            // Energy crackling at circle center
            if (s_crackling != null) {
                event.getPoseStack().pushPose();
                event.getPoseStack().translate(
                    s_circlePos.x - camPos.x,
                    s_circlePos.y - camPos.y,
                    s_circlePos.z - camPos.z);

                @SuppressWarnings("unchecked")
                EntityRenderer<EnergyCracklingEntity> cracklingRenderer =
                    (EntityRenderer<EnergyCracklingEntity>) mc.getEntityRenderDispatcher().getRenderer(s_crackling);
                cracklingRenderer.render(s_crackling, 0f, pt, event.getPoseStack(), buf, LightTexture.FULL_BRIGHT);

                event.getPoseStack().popPose();
                buf.endBatch(RenderType.lightning());
            }
        }
    }

    private static void setGlowFlag(Entity entity, boolean glow) {
        if (SET_SHARED_FLAG == null) return;
        try {
            SET_SHARED_FLAG.invoke(entity, 6, glow);
        } catch (Exception ignored) {}
    }

    private static void clearOutline(ClientLevel level) {
        if (s_outlineTarget == null) return;
        setGlowFlag(s_outlineTarget, false);
        Scoreboard sb = level.getScoreboard();
        PlayerTeam team = sb.getPlayerTeam(TEAM_NAME);
        if (team != null) {
            sb.removePlayerFromTeam(s_outlineTarget.getScoreboardName(), team);
        }
        s_outlineTarget = null;
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
