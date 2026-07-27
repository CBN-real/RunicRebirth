package com.github.runicrebirth.client;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.client.input.ModKeyMappings;
import com.github.runicrebirth.client.overlays.InfusionAltarOverlay;
import com.github.runicrebirth.client.overlays.RunicAnvilOverlay;
import com.github.runicrebirth.client.overlays.SpellStackOverlay;
import com.github.runicrebirth.client.particles.ArcaneElementParticle;
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
import com.github.runicrebirth.client.renderers.blocks.InfusionAltarRenderer;
import com.github.runicrebirth.client.renderers.blocks.RunicAnvilRenderer;
import com.github.runicrebirth.client.renderers.blocks.OculusControllerRenderer;
import com.github.runicrebirth.client.renderers.blocks.OculusPillarRenderer;
import com.github.runicrebirth.client.renderers.blocks.OculusPortalRenderer;
import com.github.runicrebirth.client.renderers.blocks.RunesteelPylonRenderer;
import com.github.runicrebirth.client.renderers.entities.DrawingCanvasRenderer;
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
import com.github.runicrebirth.client.effects.CameraShakeHandler;
import com.github.runicrebirth.client.effects.CrackManager;
import com.github.runicrebirth.client.effects.TargetCircleManager;
import com.github.runicrebirth.client.renderers.entities.TargetCircleRenderer;
import com.github.runicrebirth.compat.modonomicon.ModonomiconCompat;
import com.github.runicrebirth.init.ModBlockEntities;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModParticles;
import com.github.runicrebirth.network.ActivateRingC2SPacket;
import com.github.runicrebirth.network.SwitchStackC2SPacket;
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
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.DyedItemColor;
import com.github.runicrebirth.init.ModItems;
import com.github.runicrebirth.items.armor.AcolyteSetItem;

@Mod(value = RunicRebirth.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = RunicRebirth.MODID, value = Dist.CLIENT)
public class RunicRebirthClient {

    public RunicRebirthClient(IEventBus modBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(this::registerLayerDefinitions);
        modBus.addListener(this::registerEntityRenderers);
        modBus.addListener(this::registerKeyMappings);
        modBus.addListener(this::registerGuiLayers);
        modBus.addListener(this::registerParticles);
        modBus.addListener(this::registerItemColors);
        modBus.addListener(this::registerClientExtensions);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftInstanceHelper.instance = () -> Minecraft.getInstance().player;
        SpellCircleQueries.register();
        NeoForge.EVENT_BUS.addListener(CameraShakeHandler::onCameraSetup);
        NeoForge.EVENT_BUS.addListener(CrackManager::onRenderLevel);
        NeoForge.EVENT_BUS.addListener(TremorBlockParticle::renderAll);
        NeoForge.EVENT_BUS.addListener(TargetCircleManager::onRenderLevelStage);
        event.enqueueWork(ModonomiconCompat::registerPageRenderers);
        RunicRebirth.LOGGER.info("[RunicRebirth] Client setup complete");
    }

    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {

    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
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
        event.registerEntityRenderer(ModEntities.ENERGY_CRACKLING.get(), EnergyCracklingRenderer::new);
        event.registerEntityRenderer(ModEntities.DRAWING_CANVAS.get(), DrawingCanvasRenderer::new);
        event.registerEntityRenderer(ModEntities.TARGET_CIRCLE.get(), TargetCircleRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.OCULUS_PORTAL.get(), OculusPortalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.OCULUS_CONTROLLER.get(), OculusControllerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.OCULUS_PILLAR.get(), OculusPillarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUNESTEEL_PYLON.get(), RunesteelPylonRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.INFUSION_ALTAR.get(), InfusionAltarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUNIC_ANVIL.get(), RunicAnvilRenderer::new);
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
    }

    public void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
            (stack, layer) -> layer > 0 ? -1 : DyedItemColor.getOrDefault(stack, AcolyteSetItem.DEFAULT_DYE_COLOR),
            ModItems.ACOLYTE_WIZARD_HAT.get(),
            ModItems.ACOLYTE_ROBES.get(),
            ModItems.ACOLYTE_PANTS.get(),
            ModItems.ACOLYTE_BOOTS.get()
        );
    }

    public void registerClientExtensions(RegisterClientExtensionsEvent event) {
        var castingExtensions = new SpellWriterClientExtensions();
        event.registerItem(castingExtensions,
            ModItems.ACOLYTE_WAND.get(),
            ModItems.ADEPT_STAFF.get());
    }

    public void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
            net.neoforged.neoforge.client.gui.VanillaGuiLayers.HOTBAR,
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spell_stacks"),
            SpellStackOverlay.INSTANCE);
        event.registerAbove(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "spell_stacks"),
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "infusion_altar"),
            InfusionAltarOverlay.INSTANCE);
        event.registerAbove(
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "infusion_altar"),
            ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "runic_anvil"),
            RunicAnvilOverlay.INSTANCE);
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

    @SubscribeEvent
    public static void onClientPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof net.minecraft.client.player.LocalPlayer)) return;
        while (ModKeyMappings.SWITCH_SPELL_STACK.consumeClick()) {
            PacketDistributor.sendToServer(new SwitchStackC2SPacket());
        }
        while (ModKeyMappings.ACTIVATE_RING.consumeClick()) {
            PacketDistributor.sendToServer(new ActivateRingC2SPacket());
        }
        ClientMagicData.tickCastAnim();
        CameraShakeHandler.tick();
        CrackManager.tick();
    }
}
