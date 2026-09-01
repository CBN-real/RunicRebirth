package com.github.runicrebirth.datagen.book.condition;

import com.github.runicrebirth.compat.modonomicon.SpellUnlockCondition;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class SpellUnlockConditionModel extends BookConditionModel<SpellUnlockConditionModel> {

    private Identifier advancementId;
    private String spellName;
    private String unlockHint;

    protected SpellUnlockConditionModel() {
        super(SpellUnlockCondition.TYPE);
    }

    public static SpellUnlockConditionModel create() {
        return new SpellUnlockConditionModel();
    }

    public SpellUnlockConditionModel withAdvancementId(Identifier id) {
        this.advancementId = id;
        return this;
    }

    public SpellUnlockConditionModel withSpellName(String name) {
        this.spellName = name;
        return this;
    }

    public SpellUnlockConditionModel withUnlockHint(String hint) {
        this.unlockHint = hint;
        return this;
    }

    @Override
    public com.klikli_dev.modonomicon.book.conditions.BookCondition toBookCondition(net.minecraft.core.HolderLookup.Provider provider) {
        return new SpellUnlockCondition(advancementId, spellName, unlockHint);
    }

    @Override
    public JsonObject toJson(Identifier conditionParentId, HolderLookup.Provider provider) {
        var json = super.toJson(conditionParentId, provider);
        json.addProperty("advancement_id", advancementId.toString());
        json.addProperty("spell_name", spellName);
        json.addProperty("unlock_hint", unlockHint);
        return json;
    }
}
