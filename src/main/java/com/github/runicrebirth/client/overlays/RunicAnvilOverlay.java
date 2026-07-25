package com.github.runicrebirth.client.overlays;

import com.github.runicrebirth.blocks.entity.RunicAnvilBlockEntity;
import com.github.runicrebirth.blocks.entity.RunicAnvilBlockEntity.AnvilAction;
import com.github.runicrebirth.crafting.RunicAnvilRecipe;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RunicAnvilOverlay implements LayeredDraw.Layer {

    public static final RunicAnvilOverlay INSTANCE = new RunicAnvilOverlay();

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;

        if (!(mc.hitResult instanceof BlockHitResult blockHit) || blockHit.getType() == HitResult.Type.MISS) return;

        RunicAnvilBlockEntity anvil = findAnvil(mc, blockHit.getBlockPos());
        if (anvil == null) return;

        int screenW = graphics.guiWidth();
        int screenH = graphics.guiHeight();
        int centerX = screenW / 2;
        int centerY = screenH / 2;

        if (anvil.hasResult()) {
            renderResultReady(graphics, mc, anvil, centerX, centerY);
            return;
        }

        if (anvil.isCrafting()) {
            renderCraftingProgress(graphics, mc, anvil, centerX, centerY);
            return;
        }

        if (anvil.getItemCount() == 0 && anvil.hasLastRecipe()) {
            renderRepeatRecipe(graphics, mc, anvil, centerX, centerY);
            return;
        }

        if (anvil.getItemCount() == 0) return;

        AnvilAction action = anvil.getAnvilAction();

        if (action == AnvilAction.REPAIR) {
            renderRepairPreview(graphics, mc, anvil, centerX, centerY);
        } else if (action == AnvilAction.INSCRIBE) {
            renderInscriptionPreview(graphics, mc, anvil, centerX, centerY);
        } else if (action == AnvilAction.DEINSCRIBE) {
            renderDeinscriptionPreview(graphics, mc, anvil, centerX, centerY);
        } else if (action == AnvilAction.NO_SLOTS) {
            renderNoSlots(graphics, mc, anvil, centerX, centerY);
        }
    }

    private RunicAnvilBlockEntity findAnvil(Minecraft mc, BlockPos hitPos) {
        BlockEntity be = mc.level.getBlockEntity(hitPos);
        if (be instanceof RunicAnvilBlockEntity anvil) return anvil;
        return null;
    }

    private void renderResultReady(GuiGraphics graphics, Minecraft mc, RunicAnvilBlockEntity anvil,
                                    int centerX, int centerY) {
        ItemStack result = anvil.getResultItem();
        int boxX = centerX + 16;
        int boxY = centerY - 12;

        graphics.renderItem(result, boxX, boxY);
        graphics.renderItemDecorations(mc.font, result, boxX, boxY);

        String name = result.getHoverName().getString();
        graphics.drawString(mc.font, name, boxX + 20, boxY + 4, 0xFFFFFF, true);

        String hint = "Right-click to take";
        graphics.drawString(mc.font, hint, centerX - mc.font.width(hint) / 2, centerY + 16, 0x88FF88, true);
    }

    private void renderCraftingProgress(GuiGraphics graphics, Minecraft mc, RunicAnvilBlockEntity anvil,
                                         int centerX, int centerY) {
        int total = anvil.getCraftingTotalTicks();
        long elapsed = mc.level.getGameTime() - anvil.getCraftingStartTime();
        float progress = total > 0 ? Math.min((float) elapsed / total, 1.0f) : 0.0f;

        String text = "Forging... " + (int)(progress * 100) + "%";
        graphics.drawString(mc.font, text, centerX - mc.font.width(text) / 2, centerY + 16, 0xCCA0FF, true);

        int barW = 80;
        int barH = 4;
        int barX = centerX - barW / 2;
        int barY = centerY + 28;
        graphics.fill(barX, barY, barX + barW, barY + barH, 0x44FFFFFF);
        graphics.fill(barX, barY, barX + (int)(barW * progress), barY + barH, 0xFFCCA0FF);
    }

    private void renderRepeatRecipe(GuiGraphics graphics, Minecraft mc, RunicAnvilBlockEntity anvil,
                                     int centerX, int centerY) {
        var holder = mc.level.getRecipeManager().byKey(anvil.getLastRecipeId());
        if (holder.isEmpty() || !(holder.get().value() instanceof RunicAnvilRecipe recipe)) return;

        ItemStack result = recipe.getResultItem(mc.level.registryAccess());
        int boxX = centerX + 16;
        int boxY = centerY - 12;

        graphics.renderItem(result, boxX, boxY);
        graphics.renderItemDecorations(mc.font, result, boxX, boxY);

        String title = "Repeat recipe?";
        graphics.drawString(mc.font, title, boxX + 20, boxY, 0xFFCC44, true);

        String name = result.getHoverName().getString();
        graphics.drawString(mc.font, name, boxX + 20, boxY + 10, 0xAAAAAA, true);

        String hint = "Right-click to repeat";
        graphics.drawString(mc.font, hint, centerX - mc.font.width(hint) / 2, centerY + 16, 0x88FF88, true);
    }

    private void renderRepairPreview(GuiGraphics graphics, Minecraft mc, RunicAnvilBlockEntity anvil,
                                      int centerX, int centerY) {
        ItemStack preview = anvil.getInscriptionPreview();
        if (preview.isEmpty()) return;

        int boxX = centerX + 16;
        int boxY = centerY - 12;

        graphics.renderItem(preview, boxX, boxY);
        graphics.renderItemDecorations(mc.font, preview, boxX, boxY);

        String name = preview.getHoverName().getString();
        graphics.drawString(mc.font, name, boxX + 20, boxY + 4, 0xFFFFFF, true);

        String hint = "Cast Forge to begin";
        graphics.drawString(mc.font, hint, centerX - mc.font.width(hint) / 2, centerY + 16, 0xAAAAAA, true);
    }

    private void renderInscriptionPreview(GuiGraphics graphics, Minecraft mc, RunicAnvilBlockEntity anvil,
                                           int centerX, int centerY) {
        ItemStack wand = anvil.getInscriptionPreview();
        int boxX = centerX + 16;
        int boxY = centerY - 12;

        if (!wand.isEmpty()) {
            graphics.renderItem(wand, boxX, boxY);
            graphics.renderItemDecorations(mc.font, wand, boxX, boxY);
        }

        String label = "Inscribing spell";
        graphics.drawString(mc.font, label, boxX + 20, boxY + 4, 0x55FF55, true);

        String hint = "Cast Forge to inscribe";
        graphics.drawString(mc.font, hint, centerX - mc.font.width(hint) / 2, centerY + 16, 0xAAAAAA, true);
    }

    private void renderDeinscriptionPreview(GuiGraphics graphics, Minecraft mc, RunicAnvilBlockEntity anvil,
                                             int centerX, int centerY) {
        ItemStack wand = anvil.getInscriptionPreview();
        int boxX = centerX + 16;
        int boxY = centerY - 12;

        if (!wand.isEmpty()) {
            graphics.renderItem(wand, boxX, boxY);
            graphics.renderItemDecorations(mc.font, wand, boxX, boxY);
        }

        String label = "Removing inscription";
        graphics.drawString(mc.font, label, boxX + 20, boxY + 4, 0xFFAA44, true);

        String hint = "Cast Forge to remove";
        graphics.drawString(mc.font, hint, centerX - mc.font.width(hint) / 2, centerY + 16, 0xAAAAAA, true);
    }

    private void renderNoSlots(GuiGraphics graphics, Minecraft mc, RunicAnvilBlockEntity anvil,
                                int centerX, int centerY) {
        ItemStack wand = anvil.getInscriptionPreview();
        int boxX = centerX + 16;
        int boxY = centerY - 12;

        if (!wand.isEmpty()) {
            graphics.renderItem(wand, boxX, boxY);
            graphics.renderItemDecorations(mc.font, wand, boxX, boxY);
        }

        int x1 = boxX + 1;
        int y1 = boxY + 1;
        int x2 = boxX + 15;
        int y2 = boxY + 15;
        int thickness = 2;
        int red = 0xFFFF4444;

        graphics.fill(x1, y1, x1 + (x2 - x1), y1 + thickness, red);
        graphics.fill(x1, y1, x1 + thickness, y1 + (y2 - y1), red);
        graphics.fill(x2 - thickness, y1, x2, y2, red);
        graphics.fill(x1, y2 - thickness, x2, y2, red);

        for (int i = 0; i <= 12; i++) {
            int px = x1 + i;
            int py = y1 + i;
            graphics.fill(px, py, px + thickness, py + thickness, red);
            graphics.fill(x2 - i - thickness, py, x2 - i, py + thickness, red);
        }

        String label = "No Inscription Slots Available";
        graphics.drawString(mc.font, label, boxX + 20, boxY + 4, 0xFF4444, true);

        String hint = "Wand is fully inscribed";
        graphics.drawString(mc.font, hint, centerX - mc.font.width(hint) / 2, centerY + 16, 0xAAAAAA, true);
    }
}
