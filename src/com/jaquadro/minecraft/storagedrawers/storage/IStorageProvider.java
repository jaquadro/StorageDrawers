package com.jaquadro.minecraft.storagedrawers.storage;

import net.minecraft.item.ItemStack;

public interface IStorageProvider
{
    public boolean isCentrallyManaged ();

    public int getSlotCount (int slot);

    public void setSlotCount (int slot, int amount);

    public int getSlotStackCapacity (int slot);

    public boolean isLocked (int slot);

    public boolean isVoid (int slot);

    public boolean isShrouded (int slot);

    public boolean setIsShrouded (int slot, boolean state);

    public void markAmountDirty (int slot);

    public void markDirty (int slot);
}
