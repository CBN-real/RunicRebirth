package com.github.runicrebirth.network;

import com.github.runicrebirth.advancement.SpellAdvancementHelper;
import com.github.runicrebirth.blocks.entity.MeditationCushionBlockEntity;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import com.github.runicrebirth.entities.EarthVeinCircleEntity;
import com.github.runicrebirth.unlock.UnlockBonusCalculator;
import com.github.runicrebirth.unlock.UnlockEntry;
import com.github.runicrebirth.unlock.UnlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record AttemptUnlockC2SPacket(Identifier entryId, BlockPos cushionPos) implements CustomPacketPayload {

    public static final Type<AttemptUnlockC2SPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("runicrebirth", "attempt_unlock"));

    public static final StreamCodec<FriendlyByteBuf, AttemptUnlockC2SPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeIdentifier(pkt.entryId);
                buf.writeBlockPos(pkt.cushionPos);
            },
            buf -> new AttemptUnlockC2SPacket(buf.readIdentifier(), buf.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(AttemptUnlockC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            Optional<UnlockEntry> opt = UnlockRegistry.byId(packet.entryId);
            if (opt.isEmpty()) return;
            UnlockEntry entry = opt.get();

            DungeonData data = DungeonData.of(player);

            if (data.isUnlocked(packet.entryId)) return;

            if (entry.getParentId() != null && !data.isUnlocked(entry.getParentId())) {
                player.sendOverlayMessage(Component.literal("Parent unlock required."));
                return;
            }

            for (Identifier advCond : entry.getAdvancementConditions()) {
                if (!SpellAdvancementHelper.hasAdvancement(player, advCond)) {
                    player.sendOverlayMessage(Component.literal("Required advancement not met."));
                    return;
                }
            }

            if (!data.spendKnowledgePoints(entry.getKpCost())) {
                player.sendOverlayMessage(Component.literal("Insufficient Knowledge Points."));
                return;
            }

            data.unlockEntry(packet.entryId);
            DungeonDataSyncS2CPacket.sendTo(player);

            if (packet.entryId().getNamespace().equals("runicrebirth")
                    && packet.entryId().getPath().startsWith("artificer_commander_yotor_")) {
                int newSlots = UnlockBonusCalculator.getExtraDroneSlots(player);
                int prevSlots = 0;
                String path = packet.entryId().getPath();
                if (path.equals("artificer_commander_yotor_2")) prevSlots = 1;
                else if (path.equals("artificer_commander_yotor_3")) prevSlots = 2;
                int slotsToAdd = newSlots - prevSlots;
                if (slotsToAdd > 0) {
                    Identifier modId = Identifier.fromNamespaceAndPath("runicrebirth",
                        "commander_yotor_" + path.charAt(path.length() - 1));
                    top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
                        var slots = inv.getCurios().get("runic_drone");
                        if (slots != null) {
                            slots.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                modId, slotsToAdd, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE));
                        }
                    });
                }
            }

            if (!(player.level() instanceof ServerLevel serverLevel)) return;
            BlockEntity be = serverLevel.getBlockEntity(packet.cushionPos);
            if (be instanceof MeditationCushionBlockEntity cushionBE && cushionBE.isActive()) {
                int entityId = cushionBE.getEarthVeinEntityId();
                if (entityId != -1) {
                    Entity entity = serverLevel.getEntity(entityId);
                    if (entity instanceof EarthVeinCircleEntity circle) {
                        circle.triggerUnlockAnim();
                    }
                }
            }
        });
    }
}
