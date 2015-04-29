package com.dynious.refinedrelocation.api.tileentity.grid;


import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public interface ISortingGrid extends IGrid
{
    /**
     * Filters an ItemStack to all members of the SortingMember group
     *
     * @param itemStack The ItemStack to be filtered to all childs and this SortingMember
     * @param simulate  Simulate the insertion of items (only return result, no action)
     * @return The ItemStack that was not able to fit in any ISortingInventory
     */
    public ItemStack filterStackToGroup(ItemStack itemStack, TileEntity requester, int slot, boolean simulate);

    /**
     * Returns a List of all ItemStacks present in the Grid
     *
     * @return List of all ItemStacks in the Grid
     */
    public List<LocalizedStack> getItemsInGrid();

    /**
     * Should be called when a change is made in one of the members on the Sorting System.
     */
    public void onInventoryChange();
}
