package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.advancement.triggers.ModCriteriaTriggers;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.items.curios.IActivatableRing;
import com.github.runicrebirth.unlock.UnlockBonusCalculator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public record ActivateRingC2SPacket(int slotIndex) implements CustomPacketPayload {

    public static final Type<ActivateRingC2SPacket> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "activate_ring"));

    public static final StreamCodec<FriendlyByteBuf, ActivateRingC2SPacket> STREAM_CODEC =
        StreamCodec.of(
            (buf, pkt) -> buf.writeVarInt(pkt.slotIndex()),
            buf -> new ActivateRingC2SPacket(buf.readVarInt())
        );

    private static final String[] SLOT_IDS = {
        "thumb_spell_ring", "index_spell_ring", "middle_spell_ring",
        "ring_spell_ring", "pinkie_spell_ring"
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ActivateRingC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            int idx = packet.slotIndex();
            if (idx < 0 || idx >= SLOT_IDS.length) return;
            String slotId = SLOT_IDS[idx];
            CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
                var slots = inv.getCurios().get(slotId);
                if (slots == null) return;
                ItemStack stack = slots.getStacks().getStackInSlot(0);
                if (!stack.isEmpty() && stack.getItem() instanceof IActivatableRing ring) {
                    MagicData magicData = MagicData.of(player);
                    Map<Identifier, Integer> beforeCooldowns = new HashMap<>(magicData.cooldowns());

                    ring.activate(player, stack);

                    float ringMult = UnlockBonusCalculator.getRingCooldownMultiplier(player);
                    if (ringMult < 1.0f) {
                        for (var entry : new ArrayList<>(magicData.cooldowns().entrySet())) {
                            if (!beforeCooldowns.containsKey(entry.getKey())) {
                                magicData.startCooldown(entry.getKey(),
                                    Math.max(20, (int)(entry.getValue() * ringMult)));
                            }
                        }
                    }

                    DungeonData dungData = DungeonData.of(player);
                    int total = dungData.incrementRingActivations();
                    ModCriteriaTriggers.RING_ACTIVATED.get().trigger(player, total);
                }
            });
        });
    }
}
