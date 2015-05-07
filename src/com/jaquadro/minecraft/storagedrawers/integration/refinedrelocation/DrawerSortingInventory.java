package com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.storage.IUpgradeProvider;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class DrawerSortingInventory
{
    /*
    private ISpecialSortingInventory parent;
    private IDrawerGroup group;
    private IInventory inventory;
    private IUpgradeProvider upgrade;

    private ISortingInventoryHandler sortingHandler;
    private DrawerFilter filter;
    private boolean isAttached;

    public DrawerSortingInventory (TileEntity tileEntity, IDrawerGroup group, IInventory inventory, IUpgradeProvider upgrade) {
        parent = (ISpecialSortingInventory)tileEntity;
        this.group = group;
        this.inventory = inventory;
        this.upgrade = upgrade;

        sortingHandler = APIUtils.createSortingInventoryHandler(tileEntity);
        filter = new DrawerFilter(group);
    }

    public void attach () {
        if (!isAttached) {
            getHandler().onTileAdded();
            isAttached = true;
        }
    }

    public void detach () {
        if (isAttached) {
            getHandler().onTileRemoved();
            isAttached = false;
        }
    }

    public boolean isAttached () {
        return isAttached;
    }

    public boolean putStackInSlot (ItemStack itemStack, int slotIndex) {
        inventory.setInventorySlotContents(slotIndex, itemStack);
        return true;
    }

    public ItemStack putInInventory (ItemStack itemStack, boolean simulate) {
        for (int i = 0; i < group.getDrawerCount(); i++) {
            if (!group.isDrawerEnabled(i))
                continue;

            IDrawer drawer = group.getDrawer(i);
            if (drawer.isEmpty() || !drawer.canItemBeStored(itemStack))
                continue;

            int added = upgrade.isVoid() ? itemStack.stackSize : Math.min(drawer.getRemainingCapacity(), itemStack.stackSize);
            if (!simulate)
                drawer.setStoredItemCount(drawer.getStoredItemCount() + added);

            itemStack.stackSize -= added;
            if (itemStack.stackSize == 0)
                return null;
        }

        return itemStack;
    }

    public SpecialLocalizedStack getLocalizedStackInSlot (int slot) {
        ItemStack itemStack = inventory.getStackInSlot(slot);
        if (itemStack != null) {
            int drawerSlot = group.getDrawerInventory().getDrawerSlot(slot);
            if (!group.isDrawerEnabled(drawerSlot))
                return null;

            IDrawer drawer = group.getDrawer(drawerSlot);
            return new SpecialLocalizedStack(itemStack, parent, slot, drawer.getStoredItemCount());
        }

        return null;
    }

    public void alterStackSize (int slot, int alteration) {
        int drawerSlot = group.getDrawerInventory().getDrawerSlot(slot);
        if (!group.isDrawerEnabled(drawerSlot))
            return;

        IDrawer drawer = group.getDrawer(drawerSlot);
        drawer.setStoredItemCount(drawer.getStoredItemCount() + alteration);
    }

    public ISortingInventory.Priority getPriority () {
        return ISortingInventory.Priority.NORMAL_HIGH;
    }

    public void setPriority (ISortingInventory.Priority priority) {

    }

    public IFilter getFilter () {
        return filter;
    }

    public ISortingInventoryHandler getHandler () {
        return sortingHandler;
    }

    public void markDirty () {
        getHandler().onInventoryChange();
    }
    */
}
