package com.dynious.refinedrelocation.api.relocator;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface IItemRelocator
{
    public TileEntity getTileEntity();

    public IRelocatorModule getRelocatorModule(int side);

    public ItemStack insert(ItemStack itemStack, int side, boolean simulate);

    public boolean getRedstoneState();

    public TileEntity[] getConnectedInventories();

    public IItemRelocator[] getConnectedRelocators();

    public boolean connectsToSide(int side);

    public boolean isStuffedOnSide(int side);
}
