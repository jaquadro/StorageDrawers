package com.dynious.refinedrelocation.api;

import com.dynious.refinedrelocation.api.filter.IFilterGUI;
import com.dynious.refinedrelocation.api.relocator.IItemRelocator;
import com.dynious.refinedrelocation.api.relocator.IRelocatorModule;
import com.dynious.refinedrelocation.api.tileentity.IFilterTileGUI;
import com.dynious.refinedrelocation.api.tileentity.handlers.ISortingInventoryHandler;
import com.dynious.refinedrelocation.api.tileentity.handlers.ISortingMemberHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class APIUtils
{
    private static IAPIHandler apiHandler;

    static
    {
        try
        {
            Class c = Class.forName("com.dynious.refinedrelocation.APIHandler");
            apiHandler = (IAPIHandler) c.getField("instance").get(c);
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Opens the Filtering GUI for the TileEntity at the given position.
     * The TileEntity should implement IFilterTileGUI.
     */
    public static void openFilteringGUI(EntityPlayer entityPlayer, World world, int x, int y, int z)
    {
        entityPlayer.openGui(apiHandler.getModInstance(), apiHandler.getFilteringGUIID(), world, x, y, z);
    }

    /**
     * Creates a new instance of the standard IFilterGUI used for Sorting Chests & Filtered BlockExtenders/Buffers etc.
     * If you want the IFilterGUI settings to be saved call writeToNBT(...) and readFromNBT(...).
     *
     * @return a new instance of the standard IFilterGUI
     */
    public static IFilterGUI createStandardFilter(IFilterTileGUI filterTile)
    {
        return apiHandler.createStandardFilter(filterTile);
    }

    /**
     * Creates a new SortingMemberHandler instance. Use with ISortingMember implementers.
     *
     * @param owner The TileEntity this SortingMemberHandler will be used with.
     * @return a new SortingMemberHandler instance.
     */
    public static ISortingMemberHandler createSortingMemberHandler(TileEntity owner)
    {
        return apiHandler.createSortingMemberHandler(owner);
    }

    /**
     * Creates a new SortingInventoryHandler instance. Use with ISortingInventory implementers.
     *
     * @param owner The TileEntity this SortingInventoryHandler will be used with.
     * @return a new SortingInventoryHandler instance.
     */
    public static ISortingInventoryHandler createSortingInventoryHandler(TileEntity owner)
    {
        return apiHandler.createSortingInventoryHandler(owner);
    }

    /**
     * Registers a module for attachment to relocators. To correctly save the module your
     * module MUST be registered using this.
     *
     * @param identifier The identifier of this module
     * @param clazz      The class of this module
     */
    public static void registerRelocatorModule(String identifier, Class<? extends IRelocatorModule> clazz) throws IllegalArgumentException
    {
        apiHandler.registerRelocatorModule(identifier, clazz);
    }

    /**
     * Will open a GUI for the module by calling the getGUI() and getContainer() methods in your module.
     *
     * @param relocator The identifier of this module
     * @param player    The Player that the GUI should open for
     * @param side      The side of the module that should open a GUI
     */
    public static void openRelocatorModuleGUI(IItemRelocator relocator, EntityPlayer player, int side)
    {
        apiHandler.openRelocatorModuleGUI(relocator, player, side);
    }

    /**
     * Will register a class of an item that will be able to inserted into the toolbox.
     * This class can also be a parent class or interface of this item.
     *
     * @param clazz The class, parent class or interface of the item to be added to the toolbox
     */
    public static void registerToolboxClazz(Class clazz)
    {
        apiHandler.registerToolboxClazz(clazz);
    }

    /**
     * Utility method to insert items into a TileEntity (IInventory / IItemDuct etc.)
     *
     * @param tile TileEntity to insert into
     * @param itemStack ItemStack to insert
     * @param side Side to insert into
     * @param simulate Simulate insertion (don't actually add it, just return when would happen)
     * @return The ItemStack that was unable to be added
     */
    public static ItemStack insert(TileEntity tile, ItemStack itemStack, ForgeDirection side, boolean simulate)
    {
        return apiHandler.insert(tile, itemStack, side, simulate);
    }
}
