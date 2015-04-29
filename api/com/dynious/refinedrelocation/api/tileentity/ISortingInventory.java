package com.dynious.refinedrelocation.api.tileentity;

import com.dynious.refinedrelocation.api.tileentity.handlers.ISortingInventoryHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * This extends the {@link ISortingMember} interface.
 * Tile that implement this interface will be part of the Sorting Network
 * and their inventory will actively be used in the network.
 * The Filter set in the IFilterTile interface will be used as the filter.
 * <p/>
 * Make sure you call all required methods of {@link ISortingMember} as well as
 * setInventorySlotContents(...) when this is called in your tile.
 * <p/>
 * Override markDirty() in your tile and call getHandler().onInventoryChange()!
 * This will make sure the Sorting System knows there have been changes in your inventory.
 * Only call
 * <p/>
 * To open the Filtering GUI for this TileEntity also implement {@link IFilterTileGUI}).
 * <p/>
 * Automatically syncs the inventory to all players in a 5 block radius.
 */
public interface ISortingInventory extends ISortingMember, IInventory, IFilterTile
{
    /**
     * This should return the SortingInventoryHandler of this tile. It cannot be null.
     * Create a new SortingMemberHandler instance using APIUtils.createSortingInventoryHandler(...)
     *
     * @return The SortingInventoryHandler of this tile
     */
    public ISortingInventoryHandler getHandler();

    /**
     * Forcibly sets an ItemStack to the slotIndex
     *
     * @param itemStack The stack to add
     * @param slotIndex The slot index to add the ItemStack in
     * @return Were we able to add the ItemStack?
     */
    public boolean putStackInSlot(ItemStack itemStack, int slotIndex);

    /**
     * This should try to add the ItemStack to the inventory of this TileEntity
     *
     * @param itemStack The stack that should be put in the inventory
     * @param simulate  Simulate the insertion of items (only return result, no action)
     * @return The remaining ItemStack after trying to put the ItemStack in the Inventory
     */
    public ItemStack putInInventory(ItemStack itemStack, boolean simulate);

    /**
     * The Sorting System will try to put items in the highest priority inventory.
     * Blacklisting Chests have a low standard Priority, while whitelisting chests have a normal standard priority.
     * Barrels have the high standard priority, because they only accept one type of item.
     * This will not affect items that do not pass the Filter.
     *
     * @return The Priority of this ISortingInventory
     */
    public Priority getPriority();

    /**
     * Sets the priority of a block to a new value.
     *
     * @param priority The new priority of this tile
     */
    public void setPriority(Priority priority);

    enum Priority
    {
        HIGH,
        NORMAL_HIGH,
        NORMAL,
        NORMAL_LOW,
        LOW
    }
}
