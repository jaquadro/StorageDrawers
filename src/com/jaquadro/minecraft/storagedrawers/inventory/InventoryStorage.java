package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersBase;
import cpw.mods.fml.common.asm.transformers.ItemStackTransformer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryStorage implements IInventory
{
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

    private TileEntityDrawersBase tile;
    private InventorySnapshot snapshot;

    public InventoryStorage (TileEntityDrawersBase tileEntity) {
        tile = tileEntity;
        snapshot = new InventorySnapshot(tile.getSizeInventory());
    }

    @Override
    public int getSizeInventory () {
        return tile.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        ItemStack stack = tile.getSingleItemStack(slot);
        if (stack == null)
            return null;

        stack = stack.copy();
        stack.stackSize = tile.getItemCount(slot);

        return snapshot.takeSnapshot(stack, slot);
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        return snapshot.takeSnapshot(tile.decrStackSize(slot, count), slot);
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {

    }

    @Override
    public String getInventoryName () {
        return tile.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName () {
        return tile.hasCustomInventoryName();
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
            if (diff != 0)
                tile.getStackInSlot(i).stackSize += diff;
        }

        tile.markDirty();
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public void openInventory () {

    }

    @Override
    public void closeInventory () {

    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        return tile.isItemValidForSlot(slot, stack);
    }
}
