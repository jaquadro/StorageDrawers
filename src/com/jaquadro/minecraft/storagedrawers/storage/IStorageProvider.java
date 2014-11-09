package com.jaquadro.minecraft.storagedrawers.storage;

public interface IStorageProvider
{
    public boolean isCountCentrallyManaged ();

    public int getSlotCount (int slot);

    public int setSlotCount (int slot);

    public int getSlotStackCapacity (int slot);

    public void markAmountDirty (int slot);

    public void markDirty (int slot);
}
