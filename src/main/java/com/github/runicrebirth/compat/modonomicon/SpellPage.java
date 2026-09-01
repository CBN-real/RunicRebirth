package com.github.runicrebirth.compat.modonomicon;

import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class SpellPage extends BookPage {

    public static final Identifier PAGE_TYPE =
        Identifier.fromNamespaceAndPath("runicrebirth", "spell_page");

    // TODO: Verify BookPage anchor/condition codec integration in 26.1.x Modonomicon
    public static final MapCodec<SpellPage> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Codec.STRING.optionalFieldOf("spell_name", "").forGetter(SpellPage::getSpellName),
        Codec.STRING.optionalFieldOf("damage", "0").forGetter(SpellPage::getDamage),
        Codec.STRING.optionalFieldOf("range", "0").forGetter(SpellPage::getRange),
        Codec.STRING.optionalFieldOf("damage_type", "BLUNT").forGetter(SpellPage::getDamageType),
        Codec.FLOAT.optionalFieldOf("offset_x", 0f).forGetter(SpellPage::getOffsetX),
        Codec.FLOAT.optionalFieldOf("offset_y", 0f).forGetter(SpellPage::getOffsetY),
        Codec.FLOAT.optionalFieldOf("offset_z", 0f).forGetter(SpellPage::getOffsetZ),
        Codec.BOOL.optionalFieldOf("hide_stats", false).forGetter(SpellPage::isHideStats)
    ).apply(i, (spellName, damage, range, damageType, oX, oY, oZ, hideStats) ->
        new SpellPage(spellName, damage, range, damageType, oX, oY, oZ, hideStats, "", new BookNoneCondition())));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpellPage> STREAM_CODEC = StreamCodec.of(
        (buf, page) -> {
            buf.writeUtf(page.getSpellName());
            buf.writeUtf(page.getDamage());
            buf.writeUtf(page.getRange());
            buf.writeUtf(page.getDamageType());
            buf.writeFloat(page.getOffsetX());
            buf.writeFloat(page.getOffsetY());
            buf.writeFloat(page.getOffsetZ());
            buf.writeBoolean(page.isHideStats());
        },
        buf -> new SpellPage(
            buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf(),
            buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean(),
            "", new BookNoneCondition()
        )
    );

    private final String spellName;
    private final String damage;
    private final String range;
    private final String damageType;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final boolean hideStats;

    public SpellPage(String spellName, String damage, String range, String damageType,
                     float offsetX, float offsetY, float offsetZ, boolean hideStats,
                     String anchor, BookCondition condition) {
        super(anchor, condition);
        this.spellName = spellName;
        this.damage = damage;
        this.range = range;
        this.damageType = damageType;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.hideStats = hideStats;
    }

    public static com.klikli_dev.modonomicon.data.BookPageType<SpellPage> BOOK_PAGE_TYPE;

    public String getSpellName() { return spellName; }
    public String getDamage() { return damage; }
    public String getRange() { return range; }
    public String getDamageType() { return damageType; }
    public float getOffsetX() { return offsetX; }
    public float getOffsetY() { return offsetY; }
    public float getOffsetZ() { return offsetZ; }
    public boolean isHideStats() { return hideStats; }

    @Override
    public com.klikli_dev.modonomicon.data.BookPageType<?> type() {
        return BOOK_PAGE_TYPE;
    }

    @Override
    public boolean matchesQuery(String query, net.minecraft.world.level.Level level) {
        return spellName.toLowerCase().contains(query.toLowerCase());
    }
}
