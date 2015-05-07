package com.dynious.refinedrelocation.api.tileentity;

import com.dynious.refinedrelocation.api.tileentity.grid.SpecialLocalizedStack;

public interface ISpecialSortingInventory extends ISortingInventory
{
    /**
     * Returns a SpecialLocalizedStack for the giving slot, which will enable altering the amount of items
     * in your inventory without altering the stack size of the ItemStack in the slot.
     *
     * @param slot The slot this stack is in.
     * @return The new SpecialLocalizedStack for this slot.
     */
    public SpecialLocalizedStack getLocalizedStackInSlot(int slot);

    /**
     * This should alter the amount of items in your inventory in the given slot.
     *
     * @param slot The slot that should change size.
     * @param alteration The change (positive is addition, negative is removal).
     */
    public void alterStackSize(int slot, int alteration);
}
