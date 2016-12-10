package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class InventoryContainerProxy implements IInventory
{
    private IInventory parent;
    private Container container;

    public InventoryContainerProxy (IInventory parentInventory, Container container) {
        this.parent = parentInventory;
        this.container = container;
    }

    @Override
    public int getSizeInventory () {
        return parent.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        return parent.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        ItemStack stack = parent.getStackInSlot(slot);
        if (stack == null)
            return null;

        int stackCount = stack.stackSize;
        ItemStack result = parent.decrStackSize(slot, count);

        ItemStack stackAfter = parent.getStackInSlot(slot);
        if (stack != stackAfter || stackCount != stackAfter.stackSize)
            container.onCraftMatrixChanged(this);

        return result;
    }

    @Override
    public ItemStack removeStackFromSlot (int index) {
        ItemStack stack = parent.removeStackFromSlot(index);
        if (stack == null)
            return null;

        container.onCraftMatrixChanged(this);
        return stack;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {
        parent.setInventorySlotContents(slot, stack);
        container.onCraftMatrixChanged(this);
    }

    @Override
    public String getName () {
        return parent.getName();
    }

    @Override
    public ITextComponent getDisplayName () {
        return parent.getDisplayName();
    }

    @Override
    public boolean hasCustomName () {
        return parent.hasCustomName();
    }

    @Override
    public int getInventoryStackLimit () {
        return parent.getInventoryStackLimit();
    }

    @Override
    public void markDirty () {
        parent.markDirty();
    }

    @Override
    public boolean isUsableByPlayer (EntityPlayer player) {
        return parent.isUsableByPlayer(player);
    }

    @Override
    public void openInventory (EntityPlayer player) {
        parent.openInventory(player);
    }

    @Override
    public void closeInventory (EntityPlayer player) {
        parent.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        return parent.isItemValidForSlot(slot, stack);
    }

    @Override
    public int getField (int id) {
        return parent.getField(id);
    }

    @Override
    public void setField (int id, int value) {
        parent.setField(id, value);
    }

    @Override
    public int getFieldCount () {
        return parent.getFieldCount();
    }

    @Override
    public void clear () {
        parent.clear();
    }
}
