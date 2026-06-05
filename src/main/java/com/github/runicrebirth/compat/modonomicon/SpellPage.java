package com.github.runicrebirth.compat.modonomicon;

import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class SpellPage extends BookPage {

    public static final ResourceLocation PAGE_TYPE =
        ResourceLocation.fromNamespaceAndPath("runicrebirth", "spell_page");

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

    public String getSpellName() { return spellName; }
    public String getDamage() { return damage; }
    public String getRange() { return range; }
    public String getDamageType() { return damageType; }
    public float getOffsetX() { return offsetX; }
    public float getOffsetY() { return offsetY; }
    public float getOffsetZ() { return offsetZ; }
    public boolean isHideStats() { return hideStats; }

    @Override
    public ResourceLocation getType() {
        return PAGE_TYPE;
    }

    @Override
    public boolean matchesQuery(String query) {
        return spellName.toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(spellName);
        buffer.writeUtf(damage);
        buffer.writeUtf(range);
        buffer.writeUtf(damageType);
        buffer.writeFloat(offsetX);
        buffer.writeFloat(offsetY);
        buffer.writeFloat(offsetZ);
        buffer.writeBoolean(hideStats);
        super.toNetwork(buffer);
    }

    public static SpellPage fromJson(ResourceLocation entryId, JsonObject json, HolderLookup.Provider provider) {
        var spellName = GsonHelper.getAsString(json, "spell_name", "");
        var damage = GsonHelper.getAsString(json, "damage", "0");
        var range = GsonHelper.getAsString(json, "range", "0");
        var damageType = GsonHelper.getAsString(json, "damage_type", "BLUNT");
        var oX = GsonHelper.getAsFloat(json, "offset_x", 0f);
        var oY = GsonHelper.getAsFloat(json, "offset_y", 0f);
        var oZ = GsonHelper.getAsFloat(json, "offset_z", 0f);
        var hideStats = GsonHelper.getAsBoolean(json, "hide_stats", false);
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        var condition = json.has("condition")
            ? BookCondition.fromJson(entryId, json.getAsJsonObject("condition"), provider)
            : new BookNoneCondition();
        return new SpellPage(spellName, damage, range, damageType, oX, oY, oZ, hideStats, anchor, condition);
    }

    public static SpellPage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var spellName = buffer.readUtf();
        var damage = buffer.readUtf();
        var range = buffer.readUtf();
        var damageType = buffer.readUtf();
        var oX = buffer.readFloat();
        var oY = buffer.readFloat();
        var oZ = buffer.readFloat();
        var hideStats = buffer.readBoolean();
        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);
        return new SpellPage(spellName, damage, range, damageType, oX, oY, oZ, hideStats, anchor, condition);
    }
}
