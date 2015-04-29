package com.dynious.refinedrelocation.api.tileentity;

public interface IInventoryChangeListener
{
    /**
     * This will be called when a change is made in one of the inventories in the Grid of the TileEntity
     * is in.
     */
    public void onInventoryChanged();
}
