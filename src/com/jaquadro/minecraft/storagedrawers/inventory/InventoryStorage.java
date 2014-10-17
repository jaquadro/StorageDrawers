package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryStorage implements IInventory
{
    private TileEntityDrawersBase tile;

    public InventoryStorage (TileEntityDrawersBase tileEntity) {
        tile = tileEntity;
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

        return stack;
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        return tile.decrStackSize(slot, count);
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
        return Integer.MAX_VALUE;
    }

    @Override
    public void markDirty () {
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
