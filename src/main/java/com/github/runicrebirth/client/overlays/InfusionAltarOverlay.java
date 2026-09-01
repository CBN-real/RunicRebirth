package com.github.runicrebirth.client.overlays;

import com.github.runicrebirth.blocks.entity.InfusionAltarBlockEntity;
import com.github.runicrebirth.crafting.InfusionRecipe;
import com.github.runicrebirth.init.ModBlocks;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

public class InfusionAltarOverlay implements GuiLayer {

    public static final InfusionAltarOverlay INSTANCE = new InfusionAltarOverlay();

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;

        if (!(mc.hitResult instanceof BlockHitResult blockHit) || blockHit.getType() == HitResult.Type.MISS) return;

        InfusionAltarBlockEntity altar = findAltar(mc, blockHit.getBlockPos());
        if (altar == null) return;

        int screenW = graphics.guiWidth();
        int screenH = graphics.guiHeight();
        int centerX = screenW / 2;
        int centerY = screenH / 2;

        if (altar.hasResult()) {
            renderResultReady(graphics, mc, altar, centerX, centerY);
            return;
        }

        if (altar.isCrafting()) {
            renderCraftingProgress(graphics, mc, altar, centerX, centerY);
            return;
        }

        if (altar.getItemCount() == 0 && altar.hasLastRecipe()) {
            renderRepeatRecipe(graphics, mc, altar, centerX, centerY);
            return;
        }

        if (altar.getItemCount() == 0) return;

        Optional<RecipeHolder<InfusionRecipe>> match = altar.findMatchingRecipe();
        if (match.isEmpty()) return;

        InfusionRecipe recipe = match.get().value();
        ItemStack result = recipe.getResult();

        int boxX = centerX + 16;
        int boxY = centerY - 12;

        graphics.item(result, boxX, boxY);
        graphics.itemDecorations(mc.font, result, boxX, boxY);

        String name = result.getHoverName().getString();
        graphics.text(mc.font, name, boxX + 20, boxY + 4, 0xFFFFFF, true);

        String hint = "Cast Infusion to begin";
        graphics.text(mc.font, hint, centerX - mc.font.width(hint) / 2, centerY + 16, 0xAAAAAA, true);
    }

    private InfusionAltarBlockEntity findAltar(Minecraft mc, BlockPos hitPos) {
        BlockEntity be = mc.level.getBlockEntity(hitPos);
        if (be instanceof InfusionAltarBlockEntity altar) return altar;

        if (mc.level.getBlockState(hitPos).is(ModBlocks.INFUSION_ALTAR_PROXY.get())) {
            be = mc.level.getBlockEntity(hitPos.below());
            if (be instanceof InfusionAltarBlockEntity altar) return altar;
        }
        return null;
    }

    private void renderResultReady(GuiGraphicsExtractor graphics, Minecraft mc, InfusionAltarBlockEntity altar,
                                    int centerX, int centerY) {
        ItemStack result = altar.getResultItem();
        int boxX = centerX + 16;
        int boxY = centerY - 12;

        graphics.item(result, boxX, boxY);
        graphics.itemDecorations(mc.font, result, boxX, boxY);

        String name = result.getHoverName().getString();
        graphics.text(mc.font, name, boxX + 20, boxY + 4, 0xFFFFFF, true);

        String hint = "Right-click to take";
        graphics.text(mc.font, hint, centerX - mc.font.width(hint) / 2, centerY + 16, 0x88FF88, true);
    }

    private void renderRepeatRecipe(GuiGraphicsExtractor graphics, Minecraft mc, InfusionAltarBlockEntity altar,
                                     int centerX, int centerY) {
        ItemStack result = altar.getLastRecipeResult();
        int boxX = centerX + 16;
        int boxY = centerY - 12;

        if (!result.isEmpty()) {
            graphics.item(result, boxX, boxY);
            graphics.itemDecorations(mc.font, result, boxX, boxY);

            String name = result.getHoverName().getString();
            graphics.text(mc.font, name, boxX + 20, boxY + 10, 0xAAAAAA, true);
        }

        String title = "Repeat recipe?";
        graphics.text(mc.font, title, boxX + 20, boxY, 0xFFCC44, true);

        String hint = "Right-click to repeat";
        graphics.text(mc.font, hint, centerX - mc.font.width(hint) / 2, centerY + 16, 0x88FF88, true);
    }

    private void renderCraftingProgress(GuiGraphicsExtractor graphics, Minecraft mc, InfusionAltarBlockEntity altar,
                                         int centerX, int centerY) {
        int total = altar.getCraftingTotalTicks();
        long elapsed = mc.level.getGameTime() - altar.getCraftingStartTime();
        float progress = total > 0 ? Math.min((float) elapsed / total, 1.0f) : 0.0f;

        String text = "Infusing... " + (int)(progress * 100) + "%";
        graphics.text(mc.font, text, centerX - mc.font.width(text) / 2, centerY + 16, 0xCCA0FF, true);

        int barW = 80;
        int barH = 4;
        int barX = centerX - barW / 2;
        int barY = centerY + 28;
        graphics.fill(barX, barY, barX + barW, barY + barH, 0x44FFFFFF);
        graphics.fill(barX, barY, barX + (int)(barW * progress), barY + barH, 0xFFCCA0FF);
    }
}
