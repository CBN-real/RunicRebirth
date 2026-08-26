package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.advancement.triggers.ModCriteriaTriggers;
import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.unlock.UnlockBonusCalculator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WeaponAbilityC2SPacket() implements CustomPacketPayload {

    public static final Type<WeaponAbilityC2SPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "weapon_ability"));

    public static final StreamCodec<FriendlyByteBuf, WeaponAbilityC2SPacket> STREAM_CODEC =
        StreamCodec.of((buf, pkt) -> {}, buf -> new WeaponAbilityC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(WeaponAbilityC2SPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();

            IMagicWeapon weapon = null;
            if (main.getItem() instanceof IMagicWeapon w) weapon = w;
            else if (off.getItem() instanceof IMagicWeapon w) weapon = w;
            if (weapon == null) return;

            MagicData magicData = MagicData.of(player);
            ResourceLocation cooldownId = weapon.getWeaponCooldownId();
            boolean wasOnCooldown = magicData.isOnCooldown(cooldownId);
            weapon.activate(player);

            if (!wasOnCooldown && magicData.isOnCooldown(cooldownId)) {
                float mult = UnlockBonusCalculator.getWeaponActiveCooldownMultiplier(player);
                if (mult < 1.0f) {
                    int current = magicData.remainingCooldownTicks(cooldownId);
                    magicData.startCooldown(cooldownId, Math.max(20, (int)(current * mult)));
                }
                DungeonData dungData = DungeonData.of(player);
                int total = dungData.incrementWeaponActiveUses();
                ModCriteriaTriggers.WEAPON_ACTIVE_USED.get().trigger(player, total);
            }
        });
    }
}
