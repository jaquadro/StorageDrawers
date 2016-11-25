package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;

public interface IStorageProvider
{
    boolean isCentrallyManaged ();

    int getSlotCount (int slot);

    void setSlotCount (int slot, int amount);

    int getSlotStackCapacity (int slot);

    boolean isLocked (int slot, LockAttribute attr);

    boolean isVoid (int slot);

    boolean isShrouded (int slot);

    boolean setIsShrouded (int slot, boolean state);

    boolean isShowingQuantity (int slot);

    boolean setIsShowingQuantity (int slot, boolean state);

    boolean isStorageUnlimited (int slot);

    boolean isVendingUnlimited (int slot);

    boolean isRedstone (int slot);

    void markAmountDirty (int slot);

    void markDirty (int slot);
}
