package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.advancement.SpellAdvancementHelper;
import com.github.runicrebirth.advancement.triggers.ModCriteriaTriggers;
import com.github.runicrebirth.api.events.ShapeRecognizedEvent;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import com.github.runicrebirth.api.registry.ElementRegistry;
import com.github.runicrebirth.api.registry.ShapeRegistry;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.SpellComponent;
import com.github.runicrebirth.api.spells.SpellStack;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.items.SpellWriter;
import com.github.runicrebirth.magic.recognition.Recognizers;
import com.github.runicrebirth.magic.recognition.ShapeRecognizer;
import com.github.runicrebirth.magic.recognition.StrokePoint;
import com.github.runicrebirth.util.Log;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → server: player submitted a drawing. Payload is a list-of-strokes,
 * each a list of 2D points. Server runs $P, appends recognized component to active stack.
 */
public record DrawSubmitC2SPacket(List<List<StrokePoint>> strokes, Identifier elementId, @org.jetbrains.annotations.Nullable String hintShapeId) implements CustomPacketPayload {

    public static final int MAX_STROKES = 16;
    public static final int MAX_POINTS_PER_STROKE = 256;
    // Per-shape recognition thresholds live on ShapeRegistry.Shape now; see ModShapes.init.

    public static final Type<DrawSubmitC2SPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "draw_submit"));

    public static final StreamCodec<FriendlyByteBuf, DrawSubmitC2SPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            buf.writeVarInt(packet.strokes.size());
            for (List<StrokePoint> stroke : packet.strokes) {
                buf.writeVarInt(stroke.size());
                for (StrokePoint p : stroke) {
                    buf.writeDouble(p.x());
                    buf.writeDouble(p.y());
                }
            }
            buf.writeIdentifier(packet.elementId);
            buf.writeBoolean(packet.hintShapeId != null);
            if (packet.hintShapeId != null) buf.writeUtf(packet.hintShapeId);
        },
        buf -> {
            int strokeCount = buf.readVarInt();
            if (strokeCount < 0 || strokeCount > MAX_STROKES) {
                throw new IllegalArgumentException("Invalid stroke count: " + strokeCount);
            }
            List<List<StrokePoint>> strokes = new ArrayList<>(strokeCount);
            for (int s = 0; s < strokeCount; s++) {
                int n = buf.readVarInt();
                if (n < 0 || n > MAX_POINTS_PER_STROKE) {
                    throw new IllegalArgumentException("Invalid point count: " + n);
                }
                List<StrokePoint> stroke = new ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    stroke.add(new StrokePoint(buf.readDouble(), buf.readDouble()));
                }
                strokes.add(stroke);
            }
            Identifier elementId = buf.readIdentifier();
            String hintShapeId = buf.readBoolean() ? buf.readUtf() : null;
            return new DrawSubmitC2SPacket(strokes, elementId, hintShapeId);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(DrawSubmitC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            boolean isCircuit = heldStack.getItem() instanceof com.github.runicrebirth.items.RunicCircuitItem;
            if (!(heldStack.getItem() instanceof SpellWriter) && !isCircuit) return;

            if (isCircuit) {
                handleCircuitSubmit(packet, player, heldStack);
                return;
            }

            SpellStack activeStack = SpellWriter.resolveActiveStack(heldStack);
            if (activeStack.validSpell()) return;

            Identifier selectedElement = packet.elementId();
            if (Log.DRAW_DEBUG) RunicRebirth.LOGGER.info("[RunicRebirth] Selected element: {}", selectedElement);

            ShapeRecognizer.Result result = Recognizers.get().recognizeStrokes(packet.strokes, packet.hintShapeId, 0.65);
            if (result == null || result.id() == null) {
                if (Log.DRAW_DEBUG) RunicRebirth.LOGGER.info("[RunicRebirth] No shape match (no result)");
                player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
                return;
            }

            Identifier id = Identifier.parse(result.id());
            double threshold = ShapeRegistry.thresholdFor(id);
            if (result.score() / 10 < threshold) {
                SpellComponent nearComp = ShapeRegistry.componentFor(id);
                net.minecraft.network.chat.Component shapeName = nearComp != null
                    ? nearComp.displayName()
                    : net.minecraft.network.chat.Component.literal(id.getPath());
                player.sendOverlayMessage(
                    shapeName.copy().withStyle(net.minecraft.ChatFormatting.RED));
                if (Log.DRAW_DEBUG) RunicRebirth.LOGGER.info(
                    String.format("[RunicRebirth] Rejected shape '%s' (score=%.3f < min=%.3f)",
                        id, result.score() / 10, threshold));
                player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
                return;
            }

            SpellComponent component = ShapeRegistry.componentFor(id);
            if (component == null) {
                if (Log.DRAW_DEBUG) RunicRebirth.LOGGER.info(
                    String.format("[RunicRebirth] Recognized shape '%s' but no registered component", id));
                player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
                return;
            }

            if (component instanceof SpellType spellType && !SpellAdvancementHelper.hasSpellUnlocked(player, spellType)) {
                player.sendOverlayMessage(
                    net.minecraft.network.chat.Component.translatable("runicrebirth.spell.locked",
                        spellType.displayName()));
                if (Log.DRAW_DEBUG) RunicRebirth.LOGGER.info(
                    String.format("[RunicRebirth] Rejected locked spell '%s' for player %s", id, player.getName().getString()));
                player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
                return;
            }

            boolean added = activeStack.append(component);
            if (!added) {
                player.sendOverlayMessage(
                    component.displayName().copy()
                        .append(net.minecraft.network.chat.Component.literal(" cannot be added"))
                        .withStyle(net.minecraft.ChatFormatting.RED));
                player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
                return;
            }

            if (component instanceof SpellType) {
                DungeonData dungData = DungeonData.of(player);
                long total = dungData.incrementSpellsDrawn();
                ModCriteriaTriggers.SPELL_DRAWN.get().trigger(player, total);
            }

            Element element = ElementRegistry.get(packet.elementId());
            if (element != null) activeStack.setElement(element);

            SpellWriter.writeActiveStack(heldStack, activeStack);

            if (Log.DRAW_DEBUG) {
                int idx = SpellWriter.getActiveIndex(heldStack);
                RunicRebirth.LOGGER.info(
                    String.format("[RunicRebirth] Recognized shape '%s' (score=%.3f) → appended %s to stack[%d], %d components",
                        id, result.score(), component.id(), idx, activeStack.components().size()));
            }

            player.sendOverlayMessage(
                component.displayName().copy().withStyle(net.minecraft.ChatFormatting.GREEN));
            player.playSound(ModSounds.CANVAS_SUCCESS.get(), 1.0f, 1.0f);
            NeoForge.EVENT_BUS.post(new ShapeRecognizedEvent(player, id, result.score(), component));
            StackChangedS2CPacket.sendTo(player);
        });
    }

    private static void handleCircuitSubmit(DrawSubmitC2SPacket packet, ServerPlayer player, ItemStack circuitStack) {
        com.github.runicrebirth.items.RunicCircuitItem circuit = (com.github.runicrebirth.items.RunicCircuitItem) circuitStack.getItem();
        if (circuit.isInscribed(circuitStack)) return;

        MagicData data = MagicData.of(player);
        SpellStack building = data.getOrCreatePendingCircuit();

        int maxSlots = circuit.getModifierSlots(circuitStack);
        if (building.size() >= maxSlots) return;

        ShapeRecognizer.Result result = Recognizers.get().recognizeStrokes(packet.strokes, packet.hintShapeId, 0.65);
        if (result == null || result.id() == null) {
            player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
            return;
        }

        Identifier id = Identifier.parse(result.id());
        double threshold = ShapeRegistry.thresholdFor(id);
        if (result.score() / 10 < threshold) {
            SpellComponent nearComp = ShapeRegistry.componentFor(id);
            net.minecraft.network.chat.Component shapeName = nearComp != null
                ? nearComp.displayName()
                : net.minecraft.network.chat.Component.literal(id.getPath());
            player.sendOverlayMessage(
                shapeName.copy().withStyle(net.minecraft.ChatFormatting.RED));
            player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
            return;
        }

        SpellComponent component = ShapeRegistry.componentFor(id);
        if (component == null) {
            player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
            return;
        }

        if (component instanceof SpellType spellType && !SpellAdvancementHelper.hasSpellUnlocked(player, spellType)) {
            player.sendOverlayMessage(
                net.minecraft.network.chat.Component.translatable("runicrebirth.spell.locked",
                    spellType.displayName()));
            player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
            return;
        }

        if (component instanceof SpellType && building.validSpell()) {
            player.sendOverlayMessage(
                net.minecraft.network.chat.Component.literal("Circuit already has a spell.")
                    .withStyle(net.minecraft.ChatFormatting.RED));
            player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
            return;
        }

        boolean added = building.append(component);
        if (!added) {
            player.sendOverlayMessage(
                component.displayName().copy()
                    .append(net.minecraft.network.chat.Component.literal(" cannot be added"))
                    .withStyle(net.minecraft.ChatFormatting.RED));
            player.playSound(ModSounds.CANVAS_FAILED.get(), 1.0f, 1.0f);
            return;
        }

        Element element = ElementRegistry.get(packet.elementId());
        if (element != null) building.setElement(element);

        player.sendOverlayMessage(
            component.displayName().copy().withStyle(net.minecraft.ChatFormatting.GREEN));
        player.playSound(ModSounds.CANVAS_SUCCESS.get(), 1.0f, 1.0f);
        NeoForge.EVENT_BUS.post(new ShapeRecognizedEvent(player, id, result.score(), component));
        StackChangedS2CPacket.sendTo(player);
    }

    public static void finalizeCircuit(ServerPlayer player) {
        MagicData data = MagicData.of(player);
        if (!data.hasPendingCircuit()) return;

        ItemStack circuitStack = player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);
        if (!(circuitStack.getItem() instanceof com.github.runicrebirth.items.RunicCircuitItem circuit)) {
            data.clearPendingCircuit();
            return;
        }
        if (circuit.isInscribed(circuitStack)) {
            data.clearPendingCircuit();
            return;
        }

        SpellStack pending = data.pendingCircuitSpell();
        com.github.runicrebirth.api.spells.WandStacksData.StackEntry entry =
            SpellWriter.toEntry(pending, true, pending.size());
        circuitStack.set(com.github.runicrebirth.init.ModDataComponents.CIRCUIT_SPELL.get(), entry);
        data.clearPendingCircuit();

        player.playSound(ModSounds.CANVAS_SUCCESS.get(), 1.0f, 1.0f);
    }
}
