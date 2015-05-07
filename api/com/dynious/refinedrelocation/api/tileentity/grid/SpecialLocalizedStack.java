package com.dynious.refinedrelocation.api.tileentity.grid;

import com.dynious.refinedrelocation.api.tileentity.ISpecialSortingInventory;
import net.minecraft.item.ItemStack;

public class SpecialLocalizedStack extends LocalizedStack
{
    private int size;

    public SpecialLocalizedStack(ItemStack stack, ISpecialSortingInventory inventory, int slot, int size)
    {
        super(stack, inventory, slot);
        this.size = size;
    }

    @Override
    public int getStackSize()
    {
        return size;
    }

    @Override
    public void alterStackSize(int alteration)
    {
        ((ISpecialSortingInventory) INVENTORY).alterStackSize(SLOT, alteration);
        size += alteration;
    }
}
