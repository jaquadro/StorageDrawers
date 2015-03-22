package com.jaquadro.minecraft.storagedrawers.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CompDrawerData extends BaseDrawerData
{
    private static final ItemStack nullStack = new ItemStack((Item)null);

    private ICentralInventory central;
    private int slot;

    public CompDrawerData (ICentralInventory centralInventory, int slot) {
        this.slot = slot;
        this.central = centralInventory;
    }

    @Override
    public ItemStack getStoredItemPrototype () {
        return central.getStoredItemPrototype(slot);
    }

    @Override
    public void setStoredItem (ItemStack itemPrototype, int amount) {
        central.setStoredItem(slot, itemPrototype, amount);
        refresh();

        // markDirty
    }

    @Override
    public int getStoredItemCount () {
        return central.getStoredItemCount(slot);
    }

    @Override
    public void setStoredItemCount (int amount) {
        central.setStoredItemCount(slot, amount);
    }

    @Override
    public int getMaxCapacity () {
        return central.getMaxCapacity(slot);
    }

    @Override
    public int getMaxCapacity (ItemStack itemPrototype) {
        return central.getMaxCapacity(slot, itemPrototype);
    }

    @Override
    public int getRemainingCapacity () {
        return central.getRemainingCapacity(slot);
    }

    @Override
    public int getStoredItemStackSize () {
        return central.getStoredItemStackSize(slot);
    }

    @Override
    public boolean canItemBeStored (ItemStack itemPrototype) {
        if (getStoredItemPrototype() == null)
            return true;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean canItemBeExtracted (ItemStack itemPrototype) {
        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean isEmpty () {
        return getStoredItemPrototype() == null;
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        central.writeToNBT(slot, tag);
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        central.readFromNBT(slot, tag);
        refresh();
    }

    public void refresh () {
        reset();
        refreshOreDictMatches();
    }
}
