package com.github.runicrebirth.client.screens;

import com.github.runicrebirth.network.DungeonRoomTrackerSyncC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DungeonRoomTrackerScreen extends Screen {

    private static final int TAB_COUNT = 5;
    private static final String[] TAB_LABELS = { "Zone", "Spawners", "Doors", "Activations", "Settings" };

    private final BlockPos trackerPos;

    // All positions stored relative to trackerPos
    private BlockPos corner1 = BlockPos.ZERO;
    private BlockPos corner2 = BlockPos.ZERO;
    private final List<BlockPos> spawners = new ArrayList<>();
    private final List<BlockPos> doors = new ArrayList<>();
    private final List<BlockPos> activations = new ArrayList<>();
    private int timeBonusSeconds = 60;

    // UI state
    private int currentTab = 0;
    private int listScrollOffset = 0;
    private static final int VISIBLE_ROWS = 6;

    // Widgets
    private EditBox xBox, yBox, zBox;
    private EditBox corner1XBox, corner1YBox, corner1ZBox;
    private EditBox corner2XBox, corner2YBox, corner2ZBox;
    private EditBox timeBonusBox;

    public DungeonRoomTrackerScreen(BlockPos pos, CompoundTag config) {
        super(Component.literal("Dungeon Room Tracker"));
        this.trackerPos = pos;
        loadConfig(config);
    }

    private BlockPos toRelative(BlockPos abs) {
        return new BlockPos(abs.getX() - trackerPos.getX(), abs.getY() - trackerPos.getY(), abs.getZ() - trackerPos.getZ());
    }

    private BlockPos toAbsolute(BlockPos rel) {
        return new BlockPos(rel.getX() + trackerPos.getX(), rel.getY() + trackerPos.getY(), rel.getZ() + trackerPos.getZ());
    }

    private void loadConfig(CompoundTag tag) {
        if (tag.contains("corner1")) corner1 = toRelative(BlockPos.of(tag.getLong("corner1")));
        if (tag.contains("corner2")) corner2 = toRelative(BlockPos.of(tag.getLong("corner2")));
        spawners.clear();
        if (tag.contains("spawners"))
            for (long l : tag.getLongArray("spawners")) spawners.add(toRelative(BlockPos.of(l)));
        doors.clear();
        if (tag.contains("doors"))
            for (long l : tag.getLongArray("doors")) doors.add(toRelative(BlockPos.of(l)));
        activations.clear();
        if (tag.contains("activation_blocks"))
            for (long l : tag.getLongArray("activation_blocks")) activations.add(toRelative(BlockPos.of(l)));
        if (tag.contains("time_bonus")) timeBonusSeconds = tag.getInt("time_bonus");
    }

    @Override
    protected void init() {
        super.init();
        refreshTabWidgets();
    }

    private void refreshTabWidgets() { buildScreenContent(); }

    private void buildScreenContent() {
        clearWidgets();
        listScrollOffset = 0;

        int centerX = width / 2;
        int panelTop = 40;

        // Tab buttons
        int tabW = 70;
        int totalTabW = TAB_COUNT * tabW + (TAB_COUNT - 1) * 2;
        int tabStartX = centerX - totalTabW / 2;
        for (int i = 0; i < TAB_COUNT; i++) {
            final int tab = i;
            addRenderableWidget(Button.builder(Component.literal(TAB_LABELS[i]), b -> {
                currentTab = tab;
                refreshTabWidgets();
            }).pos(tabStartX + i * (tabW + 2), 15).size(tabW, 16).build());
        }

        switch (currentTab) {
            case 0 -> buildZoneTab(centerX, panelTop);
            case 1 -> buildListTab(centerX, panelTop, spawners, "Dungeon Mob Spawner positions");
            case 2 -> buildListTab(centerX, panelTop, doors, "Dungeon Door positions");
            case 3 -> buildListTab(centerX, panelTop, activations, "Activation Block positions");
            case 4 -> buildSettingsTab(centerX, panelTop);
        }

        addRenderableWidget(Button.builder(Component.literal("Save & Close"), b -> saveAndClose())
                .pos(centerX - 82, height - 26).size(80, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> onClose())
                .pos(centerX + 2, height - 26).size(80, 20).build());
    }

    private void buildZoneTab(int cx, int top) {
        addRenderableWidget(Button.builder(Component.literal("Use Target as Corner 1"), b -> {
            BlockPos target = getLookedAtBlockRelative();
            if (target != null) { corner1 = target; refreshTabWidgets(); }
        }).pos(cx - 90, top + 20).size(180, 18).build());

        addRenderableWidget(Button.builder(Component.literal("Use Target as Corner 2"), b -> {
            BlockPos target = getLookedAtBlockRelative();
            if (target != null) { corner2 = target; refreshTabWidgets(); }
        }).pos(cx - 90, top + 44).size(180, 18).build());

        corner1XBox = addRenderableWidget(new EditBox(font, cx - 90, top + 68, 54, 16, Component.literal("C1 X")));
        corner1YBox = addRenderableWidget(new EditBox(font, cx - 32, top + 68, 54, 16, Component.literal("C1 Y")));
        corner1ZBox = addRenderableWidget(new EditBox(font, cx + 26, top + 68, 54, 16, Component.literal("C1 Z")));
        corner1XBox.setValue(String.valueOf(corner1.getX()));
        corner1YBox.setValue(String.valueOf(corner1.getY()));
        corner1ZBox.setValue(String.valueOf(corner1.getZ()));

        corner2XBox = addRenderableWidget(new EditBox(font, cx - 90, top + 92, 54, 16, Component.literal("C2 X")));
        corner2YBox = addRenderableWidget(new EditBox(font, cx - 32, top + 92, 54, 16, Component.literal("C2 Y")));
        corner2ZBox = addRenderableWidget(new EditBox(font, cx + 26, top + 92, 54, 16, Component.literal("C2 Z")));
        corner2XBox.setValue(String.valueOf(corner2.getX()));
        corner2YBox.setValue(String.valueOf(corner2.getY()));
        corner2ZBox.setValue(String.valueOf(corner2.getZ()));

        addRenderableWidget(Button.builder(Component.literal("Apply Corners"), b -> applyCorners())
                .pos(cx - 45, top + 114).size(90, 18).build());
        // Green corner summary rendered below the Apply button in render()
    }

    private void applyCorners() {
        try {
            int x1 = Integer.parseInt(corner1XBox.getValue().trim());
            int y1 = Integer.parseInt(corner1YBox.getValue().trim());
            int z1 = Integer.parseInt(corner1ZBox.getValue().trim());
            int x2 = Integer.parseInt(corner2XBox.getValue().trim());
            int y2 = Integer.parseInt(corner2YBox.getValue().trim());
            int z2 = Integer.parseInt(corner2ZBox.getValue().trim());
            corner1 = new BlockPos(x1, y1, z1);
            corner2 = new BlockPos(x2, y2, z2);
        } catch (NumberFormatException ignored) {}
    }

    private void buildListTab(int cx, int top, List<BlockPos> list, String categoryName) {
        // Input boxes start at top+20 so the yellow category title drawn at y=42 is visible above them
        xBox = addRenderableWidget(new EditBox(font, cx - 90, top + 20, 54, 16, Component.literal("X")));
        yBox = addRenderableWidget(new EditBox(font, cx - 32, top + 20, 54, 16, Component.literal("Y")));
        zBox = addRenderableWidget(new EditBox(font, cx + 26, top + 20, 54, 16, Component.literal("Z")));

        addRenderableWidget(Button.builder(Component.literal("Add XYZ"), b -> addFromBoxes(list))
                .pos(cx - 90, top + 40).size(85, 16).build());
        addRenderableWidget(Button.builder(Component.literal("Add Target"), b -> {
            BlockPos target = getLookedAtBlockRelative();
            if (target != null) { list.add(target); refreshTabWidgets(); }
        }).pos(cx - 1, top + 40).size(91, 16).build());

        int listTop = top + 62;
        int visEnd = Math.min(listScrollOffset + VISIBLE_ROWS, list.size());
        for (int i = listScrollOffset; i < visEnd; i++) {
            BlockPos p = list.get(i);
            final int idx = i;
            int rowY = listTop + (i - listScrollOffset) * 18;
            addRenderableWidget(Button.builder(Component.literal("X " + p.getX() + " Y " + p.getY() + " Z " + p.getZ()), b -> {})
                    .pos(cx - 90, rowY).size(150, 16).build());
            addRenderableWidget(Button.builder(Component.literal("✕"), b -> {
                list.remove(idx); refreshTabWidgets();
            }).pos(cx + 62, rowY).size(16, 16).build());
        }

        int scrollY = listTop + VISIBLE_ROWS * 18 + 2;
        if (listScrollOffset > 0) {
            addRenderableWidget(Button.builder(Component.literal("▲"), b -> { listScrollOffset--; refreshTabWidgets(); })
                    .pos(cx - 20, scrollY).size(16, 16).build());
        }
        if (visEnd < list.size()) {
            addRenderableWidget(Button.builder(Component.literal("▼"), b -> { listScrollOffset++; refreshTabWidgets(); })
                    .pos(cx + 4, scrollY).size(16, 16).build());
        }
        if (!list.isEmpty()) {
            addRenderableWidget(Button.builder(Component.literal("Clear All"), b -> { list.clear(); refreshTabWidgets(); })
                    .pos(cx - 40, scrollY + 22).size(80, 16).build());
        }
    }

    private void addFromBoxes(List<BlockPos> list) {
        try {
            int x = Integer.parseInt(xBox.getValue().trim());
            int y = Integer.parseInt(yBox.getValue().trim());
            int z = Integer.parseInt(zBox.getValue().trim());
            list.add(new BlockPos(x, y, z));
            refreshTabWidgets();
        } catch (NumberFormatException ignored) {}
    }

    private void buildSettingsTab(int cx, int top) {
        timeBonusBox = addRenderableWidget(new EditBox(font, cx - 30, top + 30, 60, 16, Component.literal("Seconds")));
        timeBonusBox.setValue(String.valueOf(timeBonusSeconds));
        addRenderableWidget(Button.builder(Component.literal("Apply"), b -> {
            try { timeBonusSeconds = Math.max(0, Integer.parseInt(timeBonusBox.getValue().trim())); }
            catch (NumberFormatException ignored) {}
        }).pos(cx - 15, top + 52).size(30, 16).build());
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);

        int cx = width / 2;

        gfx.drawCenteredString(font, "Dungeon Room Tracker", cx, 3, 0xFFFFFF);
        gfx.fill(cx - 180, 33, cx + 180, 34, 0xFFAAAAAA);

        switch (currentTab) {
            case 0 -> {
                gfx.drawCenteredString(font, "Zone Configuration", cx, 42, 0xFFFFAA);
                // Below the Apply Corners button (ends at y=172), left of the coordinate boxes
                gfx.drawString(font, "C1: " + posToString(corner1), cx - 90, 178, 0xAAFFAA);
                gfx.drawString(font, "C2: " + posToString(corner2), cx - 90, 190, 0xAAFFAA);
            }
            case 1 -> gfx.drawCenteredString(font, "Dungeon Mob Spawners (" + spawners.size() + ")", cx, 42, 0xFFFFAA);
            case 2 -> gfx.drawCenteredString(font, "Dungeon Doors (" + doors.size() + ")", cx, 42, 0xFFFFAA);
            case 3 -> gfx.drawCenteredString(font, "Activation Blocks (" + activations.size() + ")", cx, 42, 0xFFFFAA);
            case 4 -> {
                gfx.drawCenteredString(font, "Settings", cx, 42, 0xFFFFAA);
                gfx.drawString(font, "Time bonus (seconds):", cx - 90, 62, 0xFFFFFF);
            }
        }

        super.render(gfx, mouseX, mouseY, partialTick);
    }

    private String posToString(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }

    @Nullable
    private BlockPos getLookedAtBlockRelative() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            return toRelative(((BlockHitResult) mc.hitResult).getBlockPos());
        }
        return null;
    }

    private void saveAndClose() {
        CompoundTag config = buildConfig();
        PacketDistributor.sendToServer(new DungeonRoomTrackerSyncC2SPacket(trackerPos, config));
        onClose();
    }

    private CompoundTag buildConfig() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("corner1", toAbsolute(corner1).asLong());
        tag.putLong("corner2", toAbsolute(corner2).asLong());
        tag.putLongArray("spawners", spawners.stream().map(this::toAbsolute).mapToLong(BlockPos::asLong).toArray());
        tag.putLongArray("doors", doors.stream().map(this::toAbsolute).mapToLong(BlockPos::asLong).toArray());
        tag.putLongArray("activation_blocks", activations.stream().map(this::toAbsolute).mapToLong(BlockPos::asLong).toArray());
        tag.putInt("time_bonus", timeBonusSeconds);
        return tag;
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
