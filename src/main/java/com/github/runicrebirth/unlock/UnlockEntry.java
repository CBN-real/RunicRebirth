package com.github.runicrebirth.unlock;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnlockEntry {
    private final ResourceLocation id;
    private final String displayName;
    private final int kpCost;
    @Nullable private final ResourceLocation parentId;
    private final UnlockCategory category;
    private final ResourceLocation icon;
    private final int gridCol;
    private final int gridRow;
    private final List<ResourceLocation> advancementConditions;
    private final List<ItemStack> itemConditions;

    public UnlockEntry(ResourceLocation id, String displayName, int kpCost,
                       @Nullable ResourceLocation parentId, UnlockCategory category,
                       ResourceLocation icon, int gridCol, int gridRow,
                       List<ResourceLocation> advancementConditions,
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

    public ResourceLocation getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getKpCost() { return kpCost; }
    @Nullable public ResourceLocation getParentId() { return parentId; }
    public UnlockCategory getCategory() { return category; }
    public ResourceLocation getIcon() { return icon; }
    public int getGridCol() { return gridCol; }
    public int getGridRow() { return gridRow; }
    public List<ResourceLocation> getAdvancementConditions() { return advancementConditions; }
    public List<ItemStack> getItemConditions() { return itemConditions; }
}
