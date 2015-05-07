package com.dynious.refinedrelocation.api.tileentity.handlers;

import com.dynious.refinedrelocation.api.tileentity.grid.ISortingGrid;

public interface ISortingMemberHandler extends IGridMemberHandler
{
    /**
     * Get the SortingGrid of this GridMember
     *
     * @return The SortingGrid of this GridMember
     */
    public ISortingGrid getGrid();
}
