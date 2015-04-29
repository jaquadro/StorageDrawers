package com.dynious.refinedrelocation.api.tileentity.handlers;

import com.dynious.refinedrelocation.api.tileentity.grid.IGrid;
import net.minecraft.tileentity.TileEntity;

public interface IGridMemberHandler
{
    /**
     * Get the owner of this Handler
     *
     * @return The TileEntity this Handler is linked to
     */
    public TileEntity getOwner();

    /**
     * Should be called on first tick from its TileEntity
     */
    public void onTileAdded();

    /**
     * Should be called by invalidate() and onChunkUnload() from its TileEntity
     */
    public void onTileRemoved();

    /**
     * Get the Grid of this GridMember
     *
     * @return The Grid of this GridMember
     */
    public IGrid getGrid();

    /**
     * Sets the Grid of a GridMember
     *
     * @param newLeader The new Grid for this GridMember
     */
    public void setGrid(IGrid newLeader);

    /**
     * @return Boolean if the GridMember can join a group
     */
    public boolean canJoinGroup();
}
