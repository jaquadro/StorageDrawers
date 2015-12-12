package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

public class InventoryStorage implements IInventory
{
    public StorageRenderItem activeRenderItem;

    private static class InventorySnapshot {
        private ItemStack[] snapshotItems;
        private int[] snapshotCounts;

        public InventorySnapshot (int size) {
            snapshotItems = new ItemStack[size];
            snapshotCounts = new int[snapshotItems.length];
        }

        public ItemStack takeSnapshot (ItemStack stack, int slot) {
            if (stack != null) {
                snapshotItems[slot] = stack;
                snapshotCounts[slot] = stack.stackSize;
            }
            else {
                snapshotItems[slot] = null;
                snapshotCounts[slot] = 0;
            }

            return stack;
        }

        public int getDiff (int slot) {
            if (snapshotItems[slot] == null)
                return 0;

            return snapshotItems[slot].stackSize - snapshotCounts[slot];
        }

        public int splitDiff (int slot) {
            int diff = getDiff(slot);
            if (diff != 0) {
                snapshotCounts[slot] = snapshotItems[slot].stackSize;
            }

            return diff;
        }
    }

    private TileEntityDrawers tile;
    private InventorySnapshot snapshot;

    public InventoryStorage (TileEntityDrawers tileEntity) {
        tile = tileEntity;
        snapshot = new InventorySnapshot(tile.getSizeInventory());
    }

    @Override
    public int getSizeInventory () {
        return tile.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        int drawerSlot = tile.getDrawerInventory().getDrawerSlot(slot);
        if (!tile.isDrawerEnabled(drawerSlot))
            return null;

        IDrawer drawer = tile.getDrawer(drawerSlot);
        ItemStack stack = drawer.getStoredItemCopy();
        if (stack == null)
            return null;

        stack.stackSize = drawer.getStoredItemCount();

        ItemStack snapStack = snapshot.takeSnapshot(stack, slot);

        if (activeRenderItem != null)
            activeRenderItem.overrideStack = snapStack;

        return snapStack;
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        return snapshot.takeSnapshot(tile.decrStackSize(slot, count), slot);
    }

    @Override
    public ItemStack removeStackFromSlot (int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {

    }

    @Override
    public String getName () {
        return tile.getName();
    }

    @Override
    public boolean hasCustomName () {
        return tile.hasCustomName();
    }

    @Override
    public IChatComponent getDisplayName () {
        return tile.getDisplayName();
    }

    @Override
    public int getInventoryStackLimit () {
        int limit = tile.getInventoryStackLimit();
        return (limit > 64) ? 64 : limit;
    }

    @Override
    public void markDirty () {
        for (int i = 0, n = getSizeInventory(); i < n; i++) {
            int diff = snapshot.splitDiff(i);
            if (diff != 0) {
                ItemStack stack = tile.getStackInSlot(i);
                if (stack != null)
                    stack.stackSize += diff;
                //else if (diff > 0)
                //    tile.setInventorySlotContents(i, snapshot.);
            }
        }

        tile.markDirty();
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public void openInventory (EntityPlayer player) { }

    @Override
    public void closeInventory (EntityPlayer player) { }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        return tile.isItemValidForSlot(slot, stack);
    }

    @Override
    public int getField (int id) {
        return 0;
    }

    @Override
    public void setField (int id, int value) {

    }

    @Override
    public int getFieldCount () {
        return 0;
    }

    @Override
    public void clear () {

    }
}
