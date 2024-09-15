package com.jaquadro.minecraft.storagedrawers.api.capabilities;

import net.minecraft.world.item.ItemStack;

// Matches interface of Forge/NeoForge IItemHandler
public interface IItemHandler
{
    int getSlots();

    ItemStack getStackInSlot(int var1);

    ItemStack insertItem(int var1, ItemStack var2, boolean var3);

    ItemStack extractItem(int var1, int var2, boolean var3);

    int getSlotLimit(int var1);

    boolean isItemValid(int var1, ItemStack var2);
}
