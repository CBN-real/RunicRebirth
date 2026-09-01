package com.github.runicrebirth.datagen.book.page;

import com.github.runicrebirth.compat.modonomicon.SpellPage;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

// TODO: Verify SpellPageModel serializes type field in output JSON
public class SpellPageModel extends BookPageModel<SpellPageModel> {

    private String spellName = "";
    private String damage = "0";
    private String range = "0";
    private String damageType = "BLUNT";
    private float offsetX = 0f;
    private float offsetY = 0f;
    private float offsetZ = 0f;
    private boolean hideStats = false;

    protected SpellPageModel() {
        super(SpellPage.PAGE_TYPE);
    }

    public static SpellPageModel create() {
        return new SpellPageModel();
    }

    public SpellPageModel withSpellName(String spellName) {
        this.spellName = spellName;
        return this;
    }

    public SpellPageModel withDamage(String damage) {
        this.damage = damage;
        return this;
    }

    public SpellPageModel withRange(String range) {
        this.range = range;
        return this;
    }

    public SpellPageModel withDamageType(String damageType) {
        this.damageType = damageType;
        return this;
    }

    public SpellPageModel withOffsetX(float offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public SpellPageModel withOffsetY(float offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    public SpellPageModel withOffsetZ(float offsetZ) {
        this.offsetZ = offsetZ;
        return this;
    }

    public SpellPageModel withHideStats(boolean hideStats) {
        this.hideStats = hideStats;
        return this;
    }

    @Override
    public com.klikli_dev.modonomicon.book.page.BookPage toBookPage(HolderLookup.Provider provider) {
        return new SpellPage(spellName, damage, range, damageType, offsetX, offsetY, offsetZ, hideStats, "", condition(provider));
    }

    @Override
    public JsonObject toJson(Identifier entryId, HolderLookup.Provider provider) {
        var json = super.toJson(entryId, provider);
        json.addProperty("spell_name", spellName);
        json.addProperty("damage", damage);
        json.addProperty("range", range);
        json.addProperty("damage_type", damageType);
        json.addProperty("offset_x", offsetX);
        json.addProperty("offset_y", offsetY);
        json.addProperty("offset_z", offsetZ);
        if (hideStats) json.addProperty("hide_stats", true);
        return json;
    }
}
