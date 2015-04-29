package com.dynious.refinedrelocation.api.tileentity;


import com.dynious.refinedrelocation.api.tileentity.handlers.ISortingMemberHandler;

/**
 * This is the interface that will make your TileEntity part of the Sorting Network.
 * <p/>
 * Make sure you call the onTileAdded() and onTileRemoved() at the right time.
 */
public interface ISortingMember extends IGridMember
{
    /**
     * This should return the ISortingMemberHandler of this tile. It cannot be null.
     * Create a new SortingMemberHandler instance using APIUtils.createSortingMemberHandler(...)
     *
     * @return The SortingMemberHandler of this tile
     */
    public ISortingMemberHandler getHandler();
}
