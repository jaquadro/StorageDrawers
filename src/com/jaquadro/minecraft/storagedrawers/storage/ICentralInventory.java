package com.jaquadro.minecraft.storagedrawers.storage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ICentralInventory
{
    public ItemStack getStoredItemPrototype (int slot);

    public void setStoredItem (int slot, ItemStack itemPrototype, int amount);

    public int getStoredItemCount (int slot);

    public void setStoredItemCount (int slot, int amount);

    public int getMaxCapacity (int slot);

    public int getMaxCapacity (int slot, ItemStack itemPrototype);

    public int getRemainingCapacity (int slot);

    public int getStoredItemStackSize (int slot);

    public int getItemCapacityForInventoryStack (int slot);

    public boolean isVoidSlot (int slot);

    public void readFromNBT (int slot, NBTTagCompound tag);

    public void writeToNBT (int slot, NBTTagCompound tag);
}
