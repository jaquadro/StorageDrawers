package com.dynious.refinedrelocation.api.tileentity.grid;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class LocalizedStack
{
    public final ItemStack STACK;
    public final IInventory INVENTORY;
    public final int SLOT;

    public LocalizedStack(ItemStack stack, IInventory inventory, int slot)
    {
        this.STACK = stack;
        this.INVENTORY = inventory;
        this.SLOT = slot;
    }

    public int getStackSize()
    {
        return STACK.stackSize;
    }

    public void alterStackSize(int alteration)
    {
        STACK.stackSize += alteration;
    }
}
