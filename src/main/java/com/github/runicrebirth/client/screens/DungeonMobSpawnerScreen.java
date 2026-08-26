package com.github.runicrebirth.client.screens;

import com.github.runicrebirth.blocks.entity.DungeonMobSpawnerBlockEntity;
import com.github.runicrebirth.network.DungeonMobSpawnerSyncC2SPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class DungeonMobSpawnerScreen extends Screen {

    private static final int VISIBLE_ROWS = 6;
    // Layout constants (all Y values from screen top)
    private static final int INPUT_Y   = 30;
    private static final int LIST_TOP  = INPUT_Y + 48; // = 78
    private static final int SCROLL_Y  = LIST_TOP + VISIBLE_ROWS * 18 + 2; // = 78+108+2=188
    private static final int RADIUS_Y  = SCROLL_Y + 46; // = 234

    private final BlockPos spawnerPos;
    private final List<DungeonMobSpawnerBlockEntity.MobWaveEntry> entries = new ArrayList<>();
    private float spawnRadius = 0.5f;

    private int listScrollOffset = 0;

    private EditBox mobIdBox;
    private EditBox waveBox;
    private EditBox countBox;
    private EditBox radiusBox;

    public DungeonMobSpawnerScreen(BlockPos pos, CompoundTag config) {
        super(Component.literal("Dungeon Mob Spawner"));
        this.spawnerPos = pos;
        loadConfig(config);
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
        RenderSystem.disableDepthTest();
        assert this.minecraft != null;
        this.minecraft.getMainRenderTarget().bindWrite(false);
    }

    private void loadConfig(CompoundTag tag) {
        entries.clear();
        if (tag.contains("wave_entries", Tag.TAG_LIST)) {
            ListTag list = tag.getList("wave_entries", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++)
                entries.add(DungeonMobSpawnerBlockEntity.MobWaveEntry.fromNbt(list.getCompound(i)));
        }
        if (tag.contains("spawn_radius")) spawnRadius = tag.getFloat("spawn_radius");
    }

    @Override
    protected void init() {
        super.init();
        buildContent();
    }

    private void buildContent() {
        clearWidgets();
        listScrollOffset = Math.max(0, Math.min(listScrollOffset, Math.max(0, entries.size() - VISIBLE_ROWS)));

        int cx = width / 2;

        // Mob ID input (full width row)
        mobIdBox = addRenderableWidget(new EditBox(font, cx - 110, INPUT_Y, 220, 16,
            Component.literal("Mob ID (e.g. minecraft:zombie)")));
        mobIdBox.setMaxLength(120);
        // Wave + Count + Add button row
        waveBox = addRenderableWidget(new EditBox(font, cx - 110, INPUT_Y + 22, 45, 16, Component.literal("Wave")));
        waveBox.setValue("1");
        countBox = addRenderableWidget(new EditBox(font, cx - 56, INPUT_Y + 22, 45, 16, Component.literal("Count")));
        countBox.setValue("1");
        addRenderableWidget(Button.builder(Component.literal("Add Entry"), b -> addEntry())
            .pos(cx - 2, INPUT_Y + 22).size(112, 16).build());

        // Entry list
        int visEnd = Math.min(listScrollOffset + VISIBLE_ROWS, entries.size());
        for (int i = listScrollOffset; i < visEnd; i++) {
            var entry = entries.get(i);
            final int idx = i;
            int rowY = LIST_TOP + (i - listScrollOffset) * 18;
            String label = "W" + entry.waveNumber() + " | " + entry.mobTypeId() + " x" + entry.count();
            addRenderableWidget(Button.builder(Component.literal(label), b -> {})
                .pos(cx - 110, rowY).size(200, 16).build());
            addRenderableWidget(Button.builder(Component.literal("✕"), b -> {
                entries.remove(idx);
                buildContent();
            }).pos(cx + 92, rowY).size(18, 16).build());
        }

        // Scroll buttons
        if (listScrollOffset > 0)
            addRenderableWidget(Button.builder(Component.literal("▲"),
                b -> { listScrollOffset--; buildContent(); })
                .pos(cx - 18, SCROLL_Y).size(16, 16).build());
        if (visEnd < entries.size())
            addRenderableWidget(Button.builder(Component.literal("▼"),
                b -> { listScrollOffset++; buildContent(); })
                .pos(cx + 2, SCROLL_Y).size(16, 16).build());
        if (!entries.isEmpty())
            addRenderableWidget(Button.builder(Component.literal("Clear All"),
                b -> { entries.clear(); buildContent(); })
                .pos(cx - 40, SCROLL_Y + 22).size(80, 16).build());

        // Spawn radius
        radiusBox = addRenderableWidget(new EditBox(font, cx - 20, RADIUS_Y, 50, 16, Component.literal("Radius")));
        radiusBox.setValue(String.valueOf(spawnRadius));
        addRenderableWidget(Button.builder(Component.literal("Apply"), b -> {
            try { spawnRadius = Math.max(0.1f, Float.parseFloat(radiusBox.getValue().trim())); }
            catch (NumberFormatException ignored) {}
        }).pos(cx + 34, RADIUS_Y).size(40, 16).build());

        // Save / Cancel
        addRenderableWidget(Button.builder(Component.literal("Save & Close"), b -> saveAndClose())
            .pos(cx - 82, height - 26).size(80, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> onClose())
            .pos(cx + 2, height - 26).size(80, 20).build());
    }

    private void addEntry() {
        try {
            String mobId = mobIdBox.getValue().trim();
            int wave  = Integer.parseInt(waveBox.getValue().trim());
            int count = Integer.parseInt(countBox.getValue().trim());
            if (mobId.isEmpty() || wave <= 0 || count <= 0) return;
            if (!mobId.contains(":")) mobId = "minecraft:" + mobId;
            entries.add(new DungeonMobSpawnerBlockEntity.MobWaveEntry(
                ResourceLocation.parse(mobId), wave, count));
            listScrollOffset = Math.max(0, entries.size() - VISIBLE_ROWS);
            buildContent();
        } catch (NumberFormatException ignored) {}
    }

    private void saveAndClose() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (var entry : entries) list.add(entry.toNbt());
        tag.put("wave_entries", list);
        tag.putFloat("spawn_radius", spawnRadius);
        PacketDistributor.sendToServer(new DungeonMobSpawnerSyncC2SPacket(spawnerPos, tag));
        onClose();
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        int cx = width / 2;
        gfx.drawCenteredString(font, "Dungeon Mob Spawner", cx, 8, 0xFFFFFF);
        gfx.fill(cx - 130, 20, cx + 130, 21, 0xFFAAAAAA);
        // Row labels above input boxes
        gfx.drawString(font, "Wave:", cx - 110, INPUT_Y + 40, 0xAAAAAA);
        gfx.drawString(font, "Count:", cx - 56, INPUT_Y + 40, 0xAAAAAA);
        // Entries header
        if (!entries.isEmpty())
            gfx.drawString(font, "Entries (" + entries.size() + "):", cx - 170, LIST_TOP + 4, 0xFFFFAA);
        else
            gfx.drawCenteredString(font, "No entries. Add mob wave entries above.", cx, LIST_TOP + 4, 0x888888);
        // Radius label
        gfx.drawString(font, "Spawn Radius:", cx - 95, RADIUS_Y + 3, 0xAAAAAA);
        super.render(gfx, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
