package com.github.runicrebirth.client.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlashOverlay implements LayeredDraw.Layer {

    public static final FlashOverlay INSTANCE = new FlashOverlay();

    private static int flashStartTick = -1;
    private static float peakAlpha = 0f;
    private static int flashDurationTicks = 10;

    private FlashOverlay() {}

    public static void trigger(float radius) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        float intensity = Math.min(0.8f, radius / 4f);
        if (intensity < 0.05f) return;
        flashStartTick = mc.player.tickCount;
        peakAlpha = intensity;
        flashDurationTicks = 10 + (int)(radius * 2f);
    }

    public static void tick() {
        if (flashStartTick < 0) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.player.tickCount - flashStartTick >= flashDurationTicks) {
            flashStartTick = -1;
            peakAlpha = 0f;
            flashDurationTicks = 10;
        }
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta) {
        if (flashStartTick < 0 || peakAlpha <= 0f) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        float elapsed = (mc.player.tickCount - flashStartTick) + delta.getGameTimeDeltaPartialTick(true);
        float progress = elapsed / flashDurationTicks;
        if (progress >= 1f) return;

        float alpha = peakAlpha * (1f - progress);
        int a = (int) (alpha * 255);
        if (a <= 0) return;

        int color = (a << 24) | 0xFFFFFF;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), color);
        RenderSystem.disableBlend();
    }

    public static void clear() {
        flashStartTick = -1;
        peakAlpha = 0f;
    }
}
