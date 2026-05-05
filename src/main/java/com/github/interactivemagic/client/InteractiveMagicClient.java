package com.github.interactivemagic.client;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.client.input.ModKeyMappings;
import com.github.interactivemagic.client.overlays.SpellStackOverlay;
import com.github.interactivemagic.client.particles.EarthElementParticle;
import com.github.interactivemagic.client.particles.FireElementParticle;
import com.github.interactivemagic.client.particles.WindElementParticle;
import com.github.interactivemagic.client.renderers.entities.MagicArrowRenderer;
import com.github.interactivemagic.client.renderers.entities.MagicBallistaRenderer;
import com.github.interactivemagic.client.renderers.entities.MagicBindingRenderer;
import com.github.interactivemagic.client.renderers.entities.MagicHammerRenderer;
import com.github.interactivemagic.client.renderers.entities.MagicMeteorRenderer;
import com.github.interactivemagic.client.renderers.entities.MagicProjectileRenderer;
import com.github.interactivemagic.client.renderers.entities.MagicShieldRenderer;
import com.github.interactivemagic.client.renderers.entities.MagicSlashRenderer;
import com.github.interactivemagic.client.renderers.entities.BasicCircleRenderer;
import com.github.interactivemagic.init.ModEntities;
import com.github.interactivemagic.init.ModParticles;
import com.github.interactivemagic.network.SwitchStackC2SPacket;
import com.github.interactivemagic.util.MinecraftInstanceHelper;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.resources.ResourceLocation;

@Mod(value = InteractiveMagic.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = InteractiveMagic.MODID, value = Dist.CLIENT)
public class InteractiveMagicClient {

    public InteractiveMagicClient(IEventBus modBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(this::registerLayerDefinitions);
        modBus.addListener(this::registerEntityRenderers);
        modBus.addListener(this::registerKeyMappings);
        modBus.addListener(this::registerGuiLayers);
        modBus.addListener(this::registerParticles);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftInstanceHelper.instance = () -> Minecraft.getInstance().player;
        SpellCircleQueries.register();
        InteractiveMagic.LOGGER.info("[InteractiveMagic] Client setup complete");
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
        event.registerEntityRenderer(ModEntities.BASIC_CIRCLE.get(), BasicCircleRenderer::new);
    }

    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        ModKeyMappings.register(event);
    }

    public void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet(ModParticles.ARCANE_ELEMENT.get(), EarthElementParticle.Provider::new);
        event.registerSpriteSet(ModParticles.FIRE_ELEMENT.get(), FireElementParticle.Provider::new);
      event.registerSpriteSet(ModParticles.WIND_ELEMENT.get(), WindElementParticle.Provider::new);
      event.registerSpriteSet(ModParticles.EARTH_ELEMENT.get(), EarthElementParticle.Provider::new);
    }

    public void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
            net.neoforged.neoforge.client.gui.VanillaGuiLayers.HOTBAR,
            ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "spell_stacks"),
            SpellStackOverlay.INSTANCE);
    }

    @SubscribeEvent
    public static void onClientPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof net.minecraft.client.player.LocalPlayer)) return;
        while (ModKeyMappings.SWITCH_SPELL_STACK.consumeClick()) {
            PacketDistributor.sendToServer(new SwitchStackC2SPacket());
        }
        ClientMagicData.tickCastAnim();
    }
}
