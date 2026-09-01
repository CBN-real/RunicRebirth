package com.github.runicrebirth.unlock;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnlockEntry {
    private final Identifier id;
    private final String displayName;
    private final int kpCost;
    @Nullable private final Identifier parentId;
    private final UnlockCategory category;
    private final Identifier icon;
    private final int gridCol;
    private final int gridRow;
    private final List<Identifier> advancementConditions;
    private final List<ItemStack> itemConditions;

    public UnlockEntry(Identifier id, String displayName, int kpCost,
                       @Nullable Identifier parentId, UnlockCategory category,
                       Identifier icon, int gridCol, int gridRow,
                       List<Identifier> advancementConditions,
                       List<ItemStack> itemConditions) {
        this.id = id;
        this.displayName = displayName;
        this.kpCost = kpCost;
        this.parentId = parentId;
        this.category = category;
        this.icon = icon;
        this.gridCol = gridCol;
        this.gridRow = gridRow;
        this.advancementConditions = List.copyOf(advancementConditions);
        this.itemConditions = List.copyOf(itemConditions);
    }

    public Identifier getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getKpCost() { return kpCost; }
    @Nullable public Identifier getParentId() { return parentId; }
    public UnlockCategory getCategory() { return category; }
    public Identifier getIcon() { return icon; }
    public int getGridCol() { return gridCol; }
    public int getGridRow() { return gridRow; }
    public List<Identifier> getAdvancementConditions() { return advancementConditions; }
    public List<ItemStack> getItemConditions() { return itemConditions; }
}
