package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IFractionalDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IItemLockable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IShroudable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class CompDrawerData extends BaseDrawerData implements IFractionalDrawer, IVoidable, IShroudable, IItemLockable
{
    private ICentralInventory central;
    private int slot;

    public CompDrawerData (ICentralInventory centralInventory, int slot) {
        this.slot = slot;
        this.central = centralInventory;
    }

    @Override
    @Nonnull
    public ItemStack getStoredItemPrototype () {
        return central.getStoredItemPrototype(slot);
    }

    @Override
    public void setStoredItem (@Nonnull ItemStack itemPrototype, int amount) {
        central.setStoredItem(slot, itemPrototype, amount);
        refresh();

        // markDirty
    }

    @Override
    public IDrawer setStoredItemRedir (@Nonnull ItemStack itemPrototype, int amount) {
        IDrawer target = central.setStoredItem(slot, itemPrototype, amount);
        refresh();

        return target;
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
    public int getMaxCapacity (@Nonnull ItemStack itemPrototype) {
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
    protected int getItemCapacityForInventoryStack () {
        return central.getItemCapacityForInventoryStack(slot);
    }

    @Override
    public boolean canItemBeStored (@Nonnull ItemStack itemPrototype) {
        if (getStoredItemPrototype().func_190926_b() && !isItemLocked(LockAttribute.LOCK_EMPTY))
            return true;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean canItemBeExtracted (@Nonnull ItemStack itemPrototype) {
        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean isEmpty () {
        return getStoredItemPrototype().func_190926_b();
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

    @Override
    public int getConversionRate () {
        return central.getConversionRate(slot);
    }

    @Override
    public int getStoredItemRemainder () {
        return central.getStoredItemRemainder(slot);
    }

    @Override
    public boolean isSmallestUnit () {
        return central.isSmallestUnit(slot);
    }

    public void refresh () {
        reset();
        refreshOreDictMatches();
    }

    @Override
    public boolean isVoid () {
        return central.isVoidSlot(slot);
    }

    @Override
    public boolean isShrouded () {
        return central.isShroudedSlot(slot);
    }

    @Override
    public boolean setIsShrouded (boolean state) {
        return central.setIsSlotShrouded(slot, state);
    }

    @Override
    public boolean isItemLocked (LockAttribute attr) {
        return central.isLocked(slot, attr);
    }

    @Override
    public boolean canItemLock (LockAttribute attr) {
        return false;
    }

    @Override
    public void setItemLocked (LockAttribute attr, boolean isLocked) { }
}
