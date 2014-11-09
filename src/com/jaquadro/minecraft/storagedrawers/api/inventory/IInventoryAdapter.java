package com.jaquadro.minecraft.storagedrawers.api.inventory;

import net.minecraft.item.ItemStack;

public interface IInventoryAdapter
{
    public ItemStack getInventoryStack (SlotType slotType);

    public void syncInventory ();
}
