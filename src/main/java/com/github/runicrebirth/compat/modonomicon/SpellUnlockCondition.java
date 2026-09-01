package com.github.runicrebirth.compat.modonomicon;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.context.BookConditionContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class SpellUnlockCondition extends BookCondition {

    public static final Identifier TYPE =
        Identifier.fromNamespaceAndPath("runicrebirth", "spell_unlock");

    // TODO: Verify BookCondition codec integration in 26.1.x Modonomicon
    public static final MapCodec<SpellUnlockCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Identifier.CODEC.fieldOf("advancement_id").forGetter(SpellUnlockCondition::getAdvancementId),
        Codec.STRING.optionalFieldOf("spell_name", "Unknown Spell").forGetter(SpellUnlockCondition::getSpellName),
        Codec.STRING.optionalFieldOf("unlock_hint", "").forGetter(SpellUnlockCondition::getUnlockHint)
    ).apply(i, SpellUnlockCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpellUnlockCondition> STREAM_CODEC = StreamCodec.of(
        (buf, cond) -> {
            buf.writeIdentifier(cond.getAdvancementId());
            buf.writeUtf(cond.getSpellName());
            buf.writeUtf(cond.getUnlockHint());
        },
        buf -> new SpellUnlockCondition(buf.readIdentifier(), buf.readUtf(), buf.readUtf())
    );

    private final Identifier advancementId;
    private final String spellName;
    private final String unlockHint;

    public SpellUnlockCondition(Identifier advancementId, String spellName, String unlockHint) {
        super(null);
        this.advancementId = advancementId;
        this.spellName = spellName;
        this.unlockHint = unlockHint;
    }

    public static com.klikli_dev.modonomicon.data.BookConditionType<SpellUnlockCondition> BOOK_CONDITION_TYPE;

    public Identifier getAdvancementId() { return advancementId; }
    public String getSpellName() { return spellName; }
    public String getUnlockHint() { return unlockHint; }

    @Override
    public com.klikli_dev.modonomicon.data.BookConditionType<?> type() {
        return BOOK_CONDITION_TYPE;
    }

    @Override
    public boolean test(BookConditionContext context, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            var advancement = serverPlayer.level().getServer().getAdvancements().get(this.advancementId);
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
