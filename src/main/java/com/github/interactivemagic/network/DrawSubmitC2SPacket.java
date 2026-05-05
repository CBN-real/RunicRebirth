package com.github.interactivemagic.network;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.api.events.ShapeRecognizedEvent;
import com.github.interactivemagic.api.registry.ShapeRegistry;
import com.github.interactivemagic.api.spells.SpellComponent;
import com.github.interactivemagic.api.spells.SpellStack;
import com.github.interactivemagic.capabilities.magic.MagicData;
import com.github.interactivemagic.items.SpellWriter;
import com.github.interactivemagic.magic.recognition.Recognizers;
import com.github.interactivemagic.magic.recognition.ShapeRecognizer;
import com.github.interactivemagic.magic.recognition.StrokePoint;
import com.github.interactivemagic.util.Log;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → server: player submitted a drawing. Payload is a list-of-strokes,
 * each a list of 2D points. Server runs $P, appends recognized component to active stack.
 */
public record DrawSubmitC2SPacket(List<List<StrokePoint>> strokes, ResourceLocation elementId) implements CustomPacketPayload {

    public static final int MAX_STROKES = 16;
    public static final int MAX_POINTS_PER_STROKE = 512;
    // Per-shape recognition thresholds live on ShapeRegistry.Shape now; see ModShapes.init.

    public static final Type<DrawSubmitC2SPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(InteractiveMagic.MODID, "draw_submit"));

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
            buf.writeResourceLocation(packet.elementId);
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
            ResourceLocation elementId = buf.readResourceLocation();
            return new DrawSubmitC2SPacket(strokes, elementId);
        }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(DrawSubmitC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!(heldStack.getItem() instanceof SpellWriter)) return;

            MagicData data = MagicData.of(player);
            SpellStack activeStack = data.activeStack();
            if (activeStack == null || activeStack.validSpell()) return;

            ResourceLocation selectedElement = packet.elementId();
            if (Log.DRAW_DEBUG) InteractiveMagic.LOGGER.info("[InteractiveMagic] Selected element: {}", selectedElement);

            ShapeRecognizer.Result result = Recognizers.get().recognizeStrokes(packet.strokes);
            if (result == null || result.id() == null) {
                if (Log.DRAW_DEBUG) InteractiveMagic.LOGGER.info("[InteractiveMagic] No shape match (no result)");
                data.setDrawing(false);
                return;
            }

            ResourceLocation id = ResourceLocation.parse(result.id());
            double threshold = ShapeRegistry.thresholdFor(id);
            if (result.score() < threshold) {
                if (Log.DRAW_DEBUG) InteractiveMagic.LOGGER.info(
                    String.format("[InteractiveMagic] Rejected shape '%s' (score=%.3f < min=%.3f)",
                        id, result.score(), threshold));
                data.setDrawing(false);
                return;
            }

            SpellComponent component = ShapeRegistry.componentFor(id);
            if (component == null) {
                if (Log.DRAW_DEBUG) InteractiveMagic.LOGGER.info(
                    String.format("[InteractiveMagic] Recognized shape '%s' but no registered component", id));
                data.setDrawing(false);
                return;
            }

            activeStack.append(component);
            if (Log.DRAW_DEBUG) InteractiveMagic.LOGGER.info(
                String.format("[InteractiveMagic] Recognized shape '%s' (score=%.3f) → appended %s to stack[%d], %d components",
                    id, result.score(), component.id(), data.activeStackIndex(), data.activeStack().components().size()));

            NeoForge.EVENT_BUS.post(new ShapeRecognizedEvent(player, id, result.score(), component));
            data.setDrawing(false);
            StackChangedS2CPacket.sendTo(player);
        });
    }
}
