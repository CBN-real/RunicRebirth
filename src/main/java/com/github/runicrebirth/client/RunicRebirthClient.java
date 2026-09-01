package com.github.runicrebirth.client;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.animations.MeditationAnimLayer;
import com.github.runicrebirth.client.animations.RunicWeaponAnimLayer;
import com.github.runicrebirth.client.input.ModKeyMappings;
import com.github.runicrebirth.client.overlays.FlashOverlay;
import com.github.runicrebirth.client.overlays.InfusionAltarOverlay;
import com.github.runicrebirth.client.overlays.RunicAnvilOverlay;
import com.github.runicrebirth.client.overlays.SpellBladeOverlay;
import com.github.runicrebirth.client.overlays.SpellRingOverlay;
import com.github.runicrebirth.client.overlays.SpellStackOverlay;
import com.github.runicrebirth.client.particles.ArcaneElementParticle;
import com.github.runicrebirth.client.particles.CriticalHitParticle;
import com.github.runicrebirth.client.particles.HoverEffectParticle;
import com.github.runicrebirth.client.particles.ResistedParticle;
import com.github.runicrebirth.client.particles.ArcaneInkParticle;
import com.github.runicrebirth.client.particles.ArcaneTinyParticle;
import com.github.runicrebirth.client.particles.EarthElementParticle;
import com.github.runicrebirth.client.particles.EarthInkParticle;
import com.github.runicrebirth.client.particles.EarthTinyParticle;
import com.github.runicrebirth.client.particles.FireElementParticle;
import com.github.runicrebirth.client.particles.FireInkParticle;
import com.github.runicrebirth.client.particles.FireTinyParticle;
import com.github.runicrebirth.client.particles.IceElementParticle;
import com.github.runicrebirth.client.particles.IceInkParticle;
import com.github.runicrebirth.client.particles.IceTinyParticle;
import com.github.runicrebirth.client.particles.WindElementParticle;
import com.github.runicrebirth.client.particles.WindInkParticle;
import com.github.runicrebirth.client.particles.WindTinyParticle;
import com.github.runicrebirth.client.renderers.blocks.AdeptStatueRenderer;
import com.github.runicrebirth.client.renderers.blocks.AncientArcaneTurretRenderer;
import com.github.runicrebirth.client.renderers.blocks.DungeonBoulderSpawnerRenderer;
import com.github.runicrebirth.client.renderers.blocks.DungeonDoorRenderer;
import com.github.runicrebirth.client.renderers.blocks.DungeonSwingingAxeRenderer;
import com.github.runicrebirth.client.renderers.entities.DungeonBoulderRenderer;
import com.github.runicrebirth.client.renderers.blocks.CrumblingPlatformRenderer;
import com.github.runicrebirth.client.renderers.blocks.DungeonMobSpawnerRenderer;
import com.github.runicrebirth.client.renderers.blocks.DungeonPressurePlateRenderer;
import com.github.runicrebirth.client.renderers.entities.CrumblingPlatformFallingRenderer;
import com.github.runicrebirth.client.renderers.blocks.RunelightLanternRenderer;
import com.github.runicrebirth.client.renderers.blocks.RunelightTorchRenderer;
import com.github.runicrebirth.client.renderers.blocks.InfusionAltarRenderer;
import com.github.runicrebirth.client.renderers.blocks.RunicAnvilRenderer;
import com.github.runicrebirth.client.renderers.blocks.RunesteelCacheRenderer;
import com.github.runicrebirth.client.renderers.blocks.RunesteelPortcullisRenderer;
import com.github.runicrebirth.client.renderers.blocks.OculusControllerRenderer;
import com.github.runicrebirth.client.renderers.blocks.OculusPillarRenderer;
import com.github.runicrebirth.client.renderers.blocks.OculusPortalRenderer;
import com.github.runicrebirth.client.renderers.blocks.RunesteelPylonRenderer;
import com.github.runicrebirth.client.renderers.blocks.RunicLeverRenderer;
import com.github.runicrebirth.client.renderers.blocks.SectBannerRenderer;
import com.github.runicrebirth.client.renderers.blocks.TatteredSectBannerRenderer;
import com.github.runicrebirth.client.renderers.blocks.SectBannerVariantRenderer;
import com.github.runicrebirth.client.renderers.entities.AncientArcaneDroneRenderer;
import com.github.runicrebirth.client.renderers.entities.DrawingCanvasRenderer;
import com.github.runicrebirth.client.renderers.entities.RunesteelGolemRenderer;
import com.github.runicrebirth.client.renderers.entities.ZombifiedRunebladeAcolyteRenderer;
import com.github.runicrebirth.client.renderers.entities.SkeletalMageAcolyteRenderer;
import com.github.runicrebirth.client.renderers.entities.SkeletalWizardAcolyteRenderer;
import com.github.runicrebirth.client.renderers.entities.ZombifiedArtificerAcolyteRenderer;
import com.github.runicrebirth.client.renderers.entities.ArcaneTetherRenderer;
import com.github.runicrebirth.client.renderers.entities.FrozenEffectRenderer;
import com.github.runicrebirth.client.renderers.entities.EnergyCracklingRenderer;
import com.github.runicrebirth.client.sounds.EnergyCracklingSoundInstance;
import com.github.runicrebirth.client.sounds.MagicBindingSoundInstance;
import com.github.runicrebirth.entities.spells.EnergyCracklingEntity;
import com.github.runicrebirth.entities.spells.MagicBindingEntity;
import com.github.runicrebirth.client.renderers.entities.InfusionCircleRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicArrowRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicBallistaCircleRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicBallistaRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicBeamRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicBlastRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicBindingRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicBallistaDemoRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicExplosionRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicHammerRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicMeteorDemoRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicSlashDemoRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicMeteorCircleRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicMeteorRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicProjectileRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicShieldRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicSlashCircleRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicSlashRenderer;
import com.github.runicrebirth.client.renderers.entities.AdvancedCircleRenderer;
import com.github.runicrebirth.client.renderers.entities.BasicCircleRenderer;
import com.github.runicrebirth.client.renderers.entities.IntermediateCircleRenderer;
import com.github.runicrebirth.client.DungeonRoomBoundsRenderer;
import com.github.runicrebirth.client.effects.CameraShakeHandler;
import com.github.runicrebirth.client.effects.CrackManager;
import com.github.runicrebirth.client.effects.ShockwaveManager;
import com.github.runicrebirth.client.effects.TargetCircleManager;
import com.github.runicrebirth.client.renderers.entities.ArcaneDroneRenderer;
import com.github.runicrebirth.client.renderers.entities.HammerDroneRenderer;
import com.github.runicrebirth.client.renderers.entities.MagicHandRenderer;
import com.github.runicrebirth.client.renderers.entities.PhantomMinerRenderer;
import com.github.runicrebirth.client.renderers.entities.AoeTrackerRenderer;
import com.github.runicrebirth.client.renderers.entities.TargetCircleRenderer;
import com.github.runicrebirth.client.renderers.entities.EarthVeinCircleRenderer;
import com.github.runicrebirth.client.renderers.entities.EarthVeinRunesRenderer;
import com.github.runicrebirth.client.screens.RunicKeyRingScreen;
import com.github.runicrebirth.compat.modonomicon.ModonomiconCompat;
import com.github.runicrebirth.init.ModBlocks;
import com.github.runicrebirth.init.ModMenuTypes;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.network.ActivateRingC2SPacket;
import com.github.runicrebirth.network.SwitchStackC2SPacket;
import com.github.runicrebirth.network.WeaponAbilityC2SPacket;
import com.github.runicrebirth.client.renderers.entities.ThrownRunicDaggerRenderer;
import com.github.runicrebirth.particle.TremorBlockParticle;
import com.github.runicrebirth.util.MinecraftInstanceHelper;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import com.github.runicrebirth.init.ModItems;

@Mod(value = RunicRebirth.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = RunicRebirth.MODID, value = Dist.CLIENT)
public class RunicRebirthClient {

    public RunicRebirthClient(IEventBus modBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(this::registerLayerDefinitions);
        modBus.addListener(this::registerEntityRenderers);
        modBus.addListener(this::addPlayerRenderLayers);
        modBus.addListener(this::registerKeyMappings);
        modBus.addListener(this::registerGuiLayers);
        modBus.addListener(this::registerParticles);
        // DyedItemColor via ItemColor removed — use DataComponents.DYED_COLOR on ApprenticeSetItem + ItemTintSource.Dye via Client Items JSON tints array
        modBus.addListener(this::registerClientExtensions);
        modBus.addListener(this::registerMenuScreens);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftInstanceHelper.instance = () -> Minecraft.getInstance().player;
        SpellCircleQueries.register();
        NeoForge.EVENT_BUS.addListener(CameraShakeHandler::onCameraSetup);
        NeoForge.EVENT_BUS.addListener(CrackManager::onRenderLevel);
        NeoForge.EVENT_BUS.addListener(TremorBlockParticle::renderAll);
        NeoForge.EVENT_BUS.addListener(TargetCircleManager::onAfterOpaqueBlocks);
        NeoForge.EVENT_BUS.addListener(DungeonRoomBoundsRenderer::onRenderLevelStage);
        event.enqueueWork(() -> {
            RunicWeaponAnimLayer.register();
            MeditationAnimLayer.register();
            ModonomiconCompat.registerPageRenderers();
            // Block render layers are now data-driven via model JSON "render_type" field in 26.1.2
        });
        RunicRebirth.LOGGER.info("[RunicRebirth] Client setup complete");
    }

    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addPlayerRenderLayers(EntityRenderersEvent.AddLayers event) {
        for (var skinModel : net.minecraft.world.entity.player.PlayerModelType.values()) {
            var renderer = event.getPlayerRenderer(skinModel);
            if (renderer instanceof net.minecraft.client.renderer.entity.LivingEntityRenderer livingRenderer) {
                try {
                    java.lang.reflect.Field f = net.neoforged.fml.util.ObfuscationReflectionHelper.findField(
                        net.minecraft.client.renderer.entity.LivingEntityRenderer.class, "layers");
                    java.util.List layers = (java.util.List) f.get(livingRenderer);
                    layers.add(0, new RingArmPoseLayer(livingRenderer));
                } catch (Exception e) {
                    RunicRebirth.LOGGER.error("[RunicRebirth] Failed to insert RingArmPoseLayer before armor layer", e);
                    livingRenderer.addLayer(new RingArmPoseLayer(livingRenderer));
                }
            }
        }
    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SEAT.get(), ctx -> new net.minecraft.client.renderer.entity.EntityRenderer<com.github.runicrebirth.entities.SeatEntity, net.minecraft.client.renderer.entity.state.EntityRenderState>(ctx) {
            @Override
            public net.minecraft.client.renderer.entity.state.EntityRenderState createRenderState() {
                return new net.minecraft.client.renderer.entity.state.EntityRenderState();
            }
        });
        event.registerEntityRenderer(ModEntities.MAGIC_PROJECTILE.get(), MagicProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_ARROW.get(), MagicArrowRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_SLASH.get(), MagicSlashRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_METEOR.get(), MagicMeteorRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_SHIELD.get(), MagicShieldRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_HAMMER.get(), MagicHammerRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_BINDING.get(), MagicBindingRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_BALLISTA.get(), MagicBallistaRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_BEAM.get(), MagicBeamRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_BLAST.get(), MagicBlastRenderer::new);
        event.registerEntityRenderer(ModEntities.BASIC_CIRCLE.get(), BasicCircleRenderer::new);
        event.registerEntityRenderer(ModEntities.INTERMEDIATE_CIRCLE.get(), IntermediateCircleRenderer::new);
        event.registerEntityRenderer(ModEntities.ADVANCED_CIRCLE.get(), AdvancedCircleRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_BALLISTA_CIRCLE.get(), MagicBallistaCircleRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_SLASH_CIRCLE.get(), MagicSlashCircleRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_METEOR_CIRCLE.get(), MagicMeteorCircleRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_EXPLOSION.get(), MagicExplosionRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_SLASH_DEMO.get(), MagicSlashDemoRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_METEOR_DEMO.get(), MagicMeteorDemoRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_BALLISTA_DEMO.get(), MagicBallistaDemoRenderer::new);
        event.registerEntityRenderer(ModEntities.INFUSION_CIRCLE.get(), InfusionCircleRenderer::new);
        event.registerEntityRenderer(ModEntities.FROZEN_EFFECT.get(), FrozenEffectRenderer::new);
        event.registerEntityRenderer(ModEntities.EARTH_QUICKSAND.get(),
            ctx -> new net.minecraft.client.renderer.entity.EntityRenderer<com.github.runicrebirth.entities.spells.EarthQuicksandEntity, net.minecraft.client.renderer.entity.state.EntityRenderState>(ctx) {
                @Override public net.minecraft.client.renderer.entity.state.EntityRenderState createRenderState() { return new net.minecraft.client.renderer.entity.state.EntityRenderState(); }
            });
        event.registerEntityRenderer(ModEntities.ARCANE_TETHER.get(), ArcaneTetherRenderer::new);
        event.registerEntityRenderer(ModEntities.ENERGY_CRACKLING.get(), EnergyCracklingRenderer::new);
        event.registerEntityRenderer(ModEntities.DRAWING_CANVAS.get(), DrawingCanvasRenderer::new);
        event.registerEntityRenderer(ModEntities.PHANTOM_MINER.get(), PhantomMinerRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGIC_HAND.get(), MagicHandRenderer::new);
        event.registerEntityRenderer(ModEntities.ARCANE_DRONE.get(), ArcaneDroneRenderer::new);
        event.registerEntityRenderer(ModEntities.ANCIENT_ARCANE_DRONE.get(), AncientArcaneDroneRenderer::new);
        event.registerEntityRenderer(ModEntities.HAMMER_DRONE.get(), HammerDroneRenderer::new);
        event.registerEntityRenderer(ModEntities.TARGET_CIRCLE.get(), TargetCircleRenderer::new);
        event.registerEntityRenderer(ModEntities.AOE_TRACKER.get(), AoeTrackerRenderer::new);
        event.registerEntityRenderer(ModEntities.RUNESTEEL_GOLEM.get(), RunesteelGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.ZOMBIFIED_RUNEBLADE_ACOLYTE.get(), ZombifiedRunebladeAcolyteRenderer::new);
        event.registerEntityRenderer(ModEntities.SKELETAL_MAGE_ACOLYTE.get(), SkeletalMageAcolyteRenderer::new);
        event.registerEntityRenderer(ModEntities.SKELETAL_WIZARD_ACOLYTE.get(), SkeletalWizardAcolyteRenderer::new);
        event.registerEntityRenderer(ModEntities.ZOMBIFIED_ARTIFICER_ACOLYTE.get(), ZombifiedArtificerAcolyteRenderer::new);
        event.registerEntityRenderer(ModEntities.CRUMBLING_PLATFORM_FALLING.get(), CrumblingPlatformFallingRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWN_RUNIC_DAGGER.get(), ThrownRunicDaggerRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.OCULUS_PORTAL.get(), OculusPortalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.OCULUS_CONTROLLER.get(), OculusControllerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.OCULUS_PILLAR.get(), OculusPillarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUNESTEEL_PYLON.get(), RunesteelPylonRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.INFUSION_ALTAR.get(), InfusionAltarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUNIC_ANVIL.get(), RunicAnvilRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUNESTEEL_CACHE.get(), RunesteelCacheRenderer::new);
        event.registerEntityRenderer(ModEntities.DUNGEON_BOULDER.get(), DungeonBoulderRenderer::new);
        event.registerEntityRenderer(ModEntities.EARTH_VEIN_CIRCLE.get(), EarthVeinCircleRenderer::new);
        event.registerEntityRenderer(ModEntities.EARTH_VEIN_RUNES.get(), EarthVeinRunesRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.DUNGEON_BOULDER_SPAWNER.get(), DungeonBoulderSpawnerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ANCIENT_ARCANE_TURRET.get(), AncientArcaneTurretRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.DUNGEON_SWINGING_AXE.get(), DungeonSwingingAxeRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUNESTEEL_PORTCULLIS.get(), RunesteelPortcullisRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.DUNGEON_DOOR.get(), DungeonDoorRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUNELIGHT_TORCH.get(), RunelightTorchRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUNELIGHT_LANTERN.get(), RunelightLanternRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.DUNGEON_PRESSURE_PLATE.get(), DungeonPressurePlateRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRUMBLING_PLATFORM.get(), CrumblingPlatformRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.DUNGEON_MOB_SPAWNER.get(), DungeonMobSpawnerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUNIC_LEVER.get(), RunicLeverRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SECT_BANNER.get(), SectBannerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TATTERED_SECT_BANNER.get(), TatteredSectBannerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SECT_BANNER_VARIANT.get(), SectBannerVariantRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ADEPT_STATUE.get(), AdeptStatueRenderer::new);
    }

    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        ModKeyMappings.register(event);
    }

    public void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet(ModParticles.ARCANE_ELEMENT.get(), ArcaneElementParticle.Provider::new);
      event.registerSpriteSet(ModParticles.ARCANE_TINY.get(), ArcaneTinyParticle.Provider::new);
        event.registerSpriteSet(ModParticles.FIRE_ELEMENT.get(), FireElementParticle.Provider::new);
      event.registerSpriteSet(ModParticles.FIRE_TINY.get(), FireTinyParticle.Provider::new);
      event.registerSpriteSet(ModParticles.WIND_ELEMENT.get(), WindElementParticle.Provider::new);
      event.registerSpriteSet(ModParticles.WIND_TINY.get(), WindTinyParticle.Provider::new);
      event.registerSpriteSet(ModParticles.EARTH_ELEMENT.get(), EarthElementParticle.Provider::new);
      event.registerSpriteSet(ModParticles.EARTH_TINY.get(), EarthTinyParticle.Provider::new);
      event.registerSpriteSet(ModParticles.ICE_ELEMENT.get(), IceElementParticle.Provider::new);
      event.registerSpriteSet(ModParticles.ICE_TINY.get(), IceTinyParticle.Provider::new);
      event.registerSpriteSet(ModParticles.ARCANE_INK.get(), ArcaneInkParticle.Provider::new);
      event.registerSpriteSet(ModParticles.FIRE_INK.get(), FireInkParticle.Provider::new);
      event.registerSpriteSet(ModParticles.ICE_INK.get(), IceInkParticle.Provider::new);
      event.registerSpriteSet(ModParticles.EARTH_INK.get(), EarthInkParticle.Provider::new);
      event.registerSpriteSet(ModParticles.WIND_INK.get(), WindInkParticle.Provider::new);
      event.registerSpecial(ModParticles.TREMOR_BLOCK.get(), new TremorBlockParticle.Provider());
      event.registerSpriteSet(ModParticles.HOVER_EFFECT.get(), HoverEffectParticle.Provider::new);
      event.registerSpriteSet(ModParticles.CRITICAL_HIT.get(), CriticalHitParticle.Provider::new);
      event.registerSpriteSet(ModParticles.RESISTED.get(), ResistedParticle.Provider::new);
    }

    public void registerClientExtensions(RegisterClientExtensionsEvent event) {
        var castingExtensions = new SpellWriterClientExtensions();
        event.registerItem(castingExtensions,
            ModItems.ACOLYTE_WAND.get(),
            ModItems.ADEPT_STAFF.get());
    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.RUNIC_KEY_RING.get(), RunicKeyRingScreen::new);
    }

    public void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
            net.neoforged.neoforge.client.gui.VanillaGuiLayers.HOTBAR,
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spell_stacks"),
            SpellStackOverlay.INSTANCE);
        event.registerAbove(
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spell_stacks"),
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "infusion_altar"),
            InfusionAltarOverlay.INSTANCE);
        event.registerAbove(
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "infusion_altar"),
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "runic_anvil"),
            RunicAnvilOverlay.INSTANCE);
        event.registerAbove(
            net.neoforged.neoforge.client.gui.VanillaGuiLayers.HOTBAR,
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spell_rings"),
            SpellRingOverlay.INSTANCE);
        event.registerAbove(
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spell_rings"),
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "spell_blade"),
            SpellBladeOverlay.INSTANCE);
        event.registerAboveAll(
            Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "impact_flash"),
            FlashOverlay.INSTANCE);
    }

    @SubscribeEvent
    public static void onRenderLivingPre(net.neoforged.neoforge.client.event.RenderLivingEvent.Pre<?, ?, ?> event) {
        if (!(event.getRenderState() instanceof net.minecraft.client.renderer.entity.state.AvatarRenderState state)) return;
        boolean active = Minecraft.getInstance().player != null && Minecraft.getInstance().player.getId() == state.id
            ? ClientMagicData.isRingCastAnimActive()
            : ClientMagicData.isRingCastAnimActiveFor(state.id);
        if (!active) return;
    }

    @SubscribeEvent
    public static void onClientPlayerRespawn(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.Clone event) {
        CrackManager.clear();
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof EnergyCracklingEntity crackle) {
            Minecraft.getInstance().getSoundManager().play(new EnergyCracklingSoundInstance(crackle));
        } else if (event.getEntity() instanceof MagicBindingEntity binding) {
            Minecraft.getInstance().getSoundManager().play(new MagicBindingSoundInstance(binding));
        }
    }

    private static boolean wasMeditating = false;

    @SubscribeEvent
    public static void onClientPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof net.minecraft.client.player.LocalPlayer localPlayer)) return;
        boolean isMeditating = localPlayer.getVehicle() instanceof com.github.runicrebirth.entities.SeatEntity;
        if (isMeditating != wasMeditating) {
            if (isMeditating) MeditationAnimLayer.trigger((net.minecraft.client.player.AbstractClientPlayer) localPlayer);
            else MeditationAnimLayer.stop((net.minecraft.client.player.AbstractClientPlayer) localPlayer);
            wasMeditating = isMeditating;
        }
        while (ModKeyMappings.SWITCH_SPELL_STACK.consumeClick()) {
            ClientPacketDistributor.sendToServer(new SwitchStackC2SPacket());
        }
        for (int _ri = 0; _ri < ModKeyMappings.ACTIVATE_SPELL_RINGS.length; _ri++) {
            final int _slotIdx = _ri;
            while (ModKeyMappings.ACTIVATE_SPELL_RINGS[_slotIdx].consumeClick()) {
                ClientPacketDistributor.sendToServer(new ActivateRingC2SPacket(_slotIdx));
            }
        }
        while (ModKeyMappings.ACTIVATE_WEAPON_ABILITY.consumeClick()) {
            ClientPacketDistributor.sendToServer(new WeaponAbilityC2SPacket());
        }
        if (ModKeyMappings.TOGGLE_TARGET_CIRCLE.consumeClick()) {
            com.github.runicrebirth.client.effects.TargetCircleManager.toggleHidden();
        }
        ClientMagicData.tickCastAnim();
        CameraShakeHandler.tick();
        CrackManager.tick();
        FlashOverlay.tick();
        ShockwaveManager.tick();
    }
}
