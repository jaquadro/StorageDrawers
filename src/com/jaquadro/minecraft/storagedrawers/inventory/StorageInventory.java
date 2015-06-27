package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.inventory.IInventoryAdapter;
import com.jaquadro.minecraft.storagedrawers.api.inventory.SlotType;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ILockable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.storage.IUpgradeProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class StorageInventory implements IDrawerInventory
{
    private static final int[] emptySlots = new int[0];

    private final IDrawerGroup group;
    private final ISideManager sideMan;
    private final IUpgradeProvider upgrade;

    private final int[] inventorySlots;

    public StorageInventory (IDrawerGroup drawerGroup, ISideManager sideManager, IUpgradeProvider upgradeProvider) {
        group = drawerGroup;
        sideMan = sideManager;
        upgrade = upgradeProvider;

        inventorySlots = new int[group.getDrawerCount() * SlotType.values.length];
        for (int i = 0, n = inventorySlots.length; i < n; i++)
            inventorySlots[i] = i;
    }

    @Override
    public int getDrawerSlot (int inventorySlot) {
        return inventorySlot % group.getDrawerCount();
    }

    @Override
    public SlotType getInventorySlotType (int inventorySlot) {
        return SlotType.values[inventorySlot / group.getDrawerCount()];
    }

    @Override
    public int getInventorySlot (int drawerSlot, SlotType type) {
        return group.getDrawerCount() * type.ordinal() + drawerSlot;
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack) {
        if (!StorageDrawers.config.cache.enableSidedInput)
            return false;

        int lowerThresh = SlotType.INPUT.ordinal() * group.getDrawerCount();
        int upperThresh = lowerThresh + group.getDrawerCount();
        if (slot < lowerThresh || slot >= upperThresh)
            return false;

        int baseSlot = getDrawerSlot(slot);
        if (!group.isDrawerEnabled(baseSlot))
            return false;

        IDrawer drawer = group.getDrawer(baseSlot);
        if (drawer == null)
            return false;

        if (drawer.isEmpty() && drawer instanceof ILockable && ((ILockable) drawer).isLocked(LockAttribute.LOCK_EMPTY))
            return false;

        return drawer.canItemBeStored(stack);
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack) {
        if (!StorageDrawers.config.cache.enableSidedOutput)
            return false;

        int lowerThresh = SlotType.OUTPUT.ordinal() * group.getDrawerCount();
        int upperThresh = lowerThresh + group.getDrawerCount();
        if (slot < lowerThresh || slot >= upperThresh)
            return false;

        int baseSlot = getDrawerSlot(slot);
        if (!group.isDrawerEnabled(baseSlot))
            return false;

        IDrawer drawer = group.getDrawer(baseSlot);
        if (drawer == null)
            return false;

        if (drawer.getStoredItemCount() == 0)
            return false;

        return drawer.canItemBeExtracted(stack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int side) {
        int[] autoSides = sideMan.getSlotsForSide(side);
        for (int aside : autoSides) {
            if (side == aside)
                return inventorySlots;
        }

        return emptySlots;
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack item, int side) {
        return canInsertItem(slot, item);
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack item, int side) {
        return canExtractItem(slot, item);
    }

    @Override
    public int getSizeInventory () {
        return inventorySlots.length;
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        if (slot < 0 || slot >= getSizeInventory())
            return null;

        IDrawer drawer = group.getDrawer(getDrawerSlot(slot));
        if (drawer == null)
            return null;

        if (!(drawer instanceof IInventoryAdapter))
            return null;

        IInventoryAdapter adapter = (IInventoryAdapter)drawer;
        adapter.syncInventory();

        return adapter.getInventoryStack(getInventorySlotType(slot));
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        if (slot < 0 || slot >= getSizeInventory())
            return null;

        IDrawer drawer = group.getDrawer(getDrawerSlot(slot));
        if (drawer == null)
            return null;

        if (!(drawer instanceof IInventoryAdapter))
            return null;

        IInventoryAdapter adapter = (IInventoryAdapter)drawer;
        adapter.syncInventory();

        ItemStack stack = adapter.getInventoryStack(SlotType.OUTPUT).copy();
        stack.stackSize = Math.min(stack.stackSize, count);
        adapter.getInventoryStack(SlotType.OUTPUT).stackSize -= stack.stackSize;

        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack item) {
        if (slot < 0 || slot >= getSizeInventory())
            return;

        IDrawer drawer = group.getDrawer(getDrawerSlot(slot));
        if (drawer == null)
            return;

        if (!(drawer instanceof IInventoryAdapter))
            return;

        IInventoryAdapter adapter = (IInventoryAdapter)drawer;
        switch (getInventorySlotType(slot)) {
            case INPUT:
                if (drawer.isEmpty()) {
                    setInventorySlotContents(drawer, item);
                    return;
                }

                adapter.setInStack(item);
                break;
            case OUTPUT:
                adapter.setOutStack(item);
                break;
        }
    }

    private void setInventorySlotContents (IDrawer drawer, ItemStack item) {
        if (item == null)
            drawer.setStoredItem(null, 0);
        else {
            drawer.setStoredItem(item, 0);
            if (!drawer.canItemBeStored(item))
                drawer = findDrawer(item);
            if (drawer == null)
                return;

            int insertCount = item.stackSize;
            if (insertCount > drawer.getMaxCapacity())
                insertCount = drawer.getMaxCapacity();

            drawer.setStoredItemCount(insertCount);

            if (!upgrade.isVoid())
                item.stackSize = insertCount;
        }
    }

    private IDrawer findDrawer (ItemStack item) {
        for (int i = 0; i < group.getDrawerCount(); i++) {
            if (!group.isDrawerEnabled(i))
                continue;

            IDrawer drawer = group.getDrawer(i);
            if (drawer.canItemBeStored(item))
                return drawer;
        }

        return null;
    }

    @Override
    public String getInventoryName () {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName () {
        return false;
    }

    @Override
    public int getInventoryStackLimit () {
        return 64;
    }

    @Override
    public void markDirty () {
        for (int i = 0, n = group.getDrawerCount(); i < n; i++) {
            if (!group.isDrawerEnabled(i))
                continue;

            IDrawer drawer = group.getDrawer(i);
            if (drawer instanceof IInventoryAdapter)
                ((IInventoryAdapter) drawer).syncInventory();
        }
    }

    @Override
    public boolean syncInventoryIfNeeded () {
        boolean synced = false;

        for (int i = 0, n = group.getDrawerCount(); i < n; i++) {
            if (!group.isDrawerEnabled(i))
                continue;

            IDrawer drawer = group.getDrawer(i);
            if (drawer instanceof IInventoryAdapter)
                synced |= ((IInventoryAdapter) drawer).syncInventoryIfNeeded();
        }

        return synced;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory () { }

    @Override
    public void closeInventory () { }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack item) {
        if (slot < 0 || slot >= getSizeInventory())
            return false;

        IDrawer drawer = group.getDrawer(getDrawerSlot(slot));
        if (drawer == null)
            return false;

        switch (getInventorySlotType(slot)) {
            case INPUT:
                return drawer.canItemBeStored(item);
            case OUTPUT:
                return drawer.canItemBeExtracted(item);
            default:
                return false;
        }
    }
}