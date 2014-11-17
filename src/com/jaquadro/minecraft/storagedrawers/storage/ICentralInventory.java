package com.jaquadro.minecraft.storagedrawers.storage;

import net.minecraft.item.ItemStack;

public interface ICentralInventory
{
    public ItemStack getStoredItemPrototype (int slot);

    public void setStoredItem (int slot, ItemStack itemPrototype, int amount);

    public int getStoredItemCount (int slot);

    public void setStoredItemCount (int slot, int amount);

    public int getMaxCapacity (int slot);

    public int getRemainingCapacity (int slot);

    public int getStoredItemStackSize (int slot);
}
