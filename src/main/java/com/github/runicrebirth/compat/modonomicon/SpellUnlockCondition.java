package com.github.runicrebirth.compat.modonomicon;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class SpellUnlockCondition extends BookCondition {

    public static final ResourceLocation TYPE =
        ResourceLocation.fromNamespaceAndPath("runicrebirth", "spell_unlock");

    private final ResourceLocation advancementId;
    private final String spellName;
    private final String unlockHint;

    public SpellUnlockCondition(ResourceLocation advancementId, String spellName, String unlockHint) {
        super(null);
        this.advancementId = advancementId;
        this.spellName = spellName;
        this.unlockHint = unlockHint;
    }

    public static SpellUnlockCondition fromJson(ResourceLocation conditionParentId, JsonObject json,
                                                 HolderLookup.Provider provider) {
        var advancementId = ResourceLocation.parse(GsonHelper.getAsString(json, "advancement_id"));
        var spellName = GsonHelper.getAsString(json, "spell_name", "Unknown Spell");
        var unlockHint = GsonHelper.getAsString(json, "unlock_hint", "");
        return new SpellUnlockCondition(advancementId, spellName, unlockHint);
    }

    public static SpellUnlockCondition fromNetwork(RegistryFriendlyByteBuf buffer) {
        var advancementId = buffer.readResourceLocation();
        var spellName = buffer.readUtf();
        var unlockHint = buffer.readUtf();
        return new SpellUnlockCondition(advancementId, spellName, unlockHint);
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.advancementId);
        buffer.writeUtf(this.spellName);
        buffer.writeUtf(this.unlockHint);
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            var advancement = serverPlayer.getServer().getAdvancements().get(this.advancementId);
            return advancement != null && serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        return false;
    }

    @Override
    public List<Component> getTooltip(Player player, BookConditionContext context) {
        return List.of(
            Component.literal(spellName).withStyle(ChatFormatting.BOLD),
            Component.literal(unlockHint).withStyle(ChatFormatting.GRAY)
        );
    }
}
