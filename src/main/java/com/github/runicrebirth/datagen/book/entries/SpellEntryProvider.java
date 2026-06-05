package com.github.runicrebirth.datagen.book.entries;

import com.github.runicrebirth.datagen.book.condition.SpellUnlockConditionModel;
import com.github.runicrebirth.datagen.book.page.SpellPageModel;
import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEntityPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class SpellEntryProvider extends EntryProvider {

    private final String id;
    private final String name;
    private final String damage;
    private final String range;
    private final String damageType;
    private final String entityId;
    private final String iconTexture;
    private final float entityScale;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final ResourceLocation advancementId;
    private final String unlockHint;
    private boolean hideStats;

    public SpellEntryProvider(CategoryProvider parent, String id, String name,
                              String damage, String range, String damageType,
                              String entityId, String iconTexture, float entityScale,
                              float offsetX, float offsetY, float offsetZ,
                              ResourceLocation advancementId, String unlockHint) {
        super(parent);
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.range = range;
        this.damageType = damageType;
        this.entityId = entityId;
        this.iconTexture = iconTexture;
        this.entityScale = entityScale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.advancementId = advancementId;
        this.unlockHint = unlockHint;
    }

    public SpellEntryProvider withHideStats(boolean hideStats) {
        this.hideStats = hideStats;
        return this;
    }

    @Override
    protected BookEntryModel additionalSetup(BookEntryModel entry) {
        if (advancementId != null) {
            var condition = SpellUnlockConditionModel.create()
                .withAdvancementId(advancementId)
                .withSpellName(name)
                .withUnlockHint(unlockHint != null ? unlockHint : "");
            entry.withCondition(condition);
        }
        return super.additionalSetup(entry);
    }

    @Override
    protected void generatePages() {
        this.page("stats", () -> SpellPageModel.create()
            .withSpellName(name)
            .withDamage(damage)
            .withRange(range)
            .withDamageType(damageType)
            .withOffsetX(offsetX)
            .withOffsetY(offsetY)
            .withOffsetZ(offsetZ)
            .withHideStats(hideStats));

        this.page("entity", () -> BookEntityPageModel.create()
            .withEntityId(entityId)
            .withScale(entityScale)
            .withRotate(false)
            .withDefaultRotation(-45f)
            .withText(this.context().pageText()));
        this.pageText("The " + name + " spell.");
    }

    @Override
    protected String entryName() {
        return name;
    }

    @Override
    protected String entryDescription() {
        return "The " + name + " spell.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return Pair.of(0, 0);
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(
            ResourceLocation.fromNamespaceAndPath("runicrebirth", iconTexture), 16, 16);
    }

    @Override
    protected String entryId() {
        return id;
    }
}
