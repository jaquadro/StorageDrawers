package com.dynious.refinedrelocation.api.tileentity.grid;

import com.dynious.refinedrelocation.api.tileentity.handlers.IGridMemberHandler;

import java.util.List;

public interface IGrid
{
    /**
     * Adds a member to the list of members
     *
     * @param member Member to be added
     */
    public void addMember(IGridMemberHandler member);

    /**
     * Removes a member from the list of members
     *
     * @param member Member to be removed
     */
    public void removeMember(IGridMemberHandler member);

    /**
     * Sets grid of all members to null and deletes the member list
     */
    public void resetMembers();

    /**
     * Returns all members of this grid
     */
    public List<IGridMemberHandler> getMembers();

    /**
     * Merges this grid into the give grid
     *
     * @param grid The new Grid for this Grid should merge with
     */
    public void mergeToGrid(IGrid grid);
}
