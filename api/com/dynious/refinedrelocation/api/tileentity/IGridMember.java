package com.dynious.refinedrelocation.api.tileentity;

import com.dynious.refinedrelocation.api.tileentity.handlers.IGridMemberHandler;

/**
 * Base class, DO NOT USE THIS!
 */
public interface IGridMember
{
    /**
     * This should return the ISortingMemberHandler of this tile. It cannot be null.
     *
     * @return The GridMemberHandler of this tile
     */
    public IGridMemberHandler getHandler();
}
