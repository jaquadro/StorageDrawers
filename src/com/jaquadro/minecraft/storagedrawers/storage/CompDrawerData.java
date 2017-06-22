package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IFractionalDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;

public class CompDrawerData extends BaseDrawerData implements IFractionalDrawer
{
    @CapabilityInject(IDrawerAttributes.class)
    static Capability<IDrawerAttributes> ATTR_CAPABILITY = null;

    private ICentralInventory central;
    private int slot;

    IDrawerAttributes attrs;

    public CompDrawerData (ICentralInventory centralInventory, ICapabilityProvider capProvider, int slot) {
        this.slot = slot;
        this.central = centralInventory;

        attrs = capProvider.getCapability(ATTR_CAPABILITY, null);
        if (attrs == null)
            attrs = new EmptyDrawerAttributes();
    }

    @Override
    @Nonnull
    public ItemStack getStoredItemPrototype () {
        return central.getStoredItemPrototype(slot);
    }

    @Override
    public IDrawer setStoredItem (@Nonnull ItemStack itemPrototype, int amount) {
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
    public int getDefaultMaxCapacity () {
        return central.getDefaultMaxCapacity(slot);
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
        if (getStoredItemPrototype().isEmpty() && !attrs.isItemLocked(LockAttribute.LOCK_EMPTY))
            return true;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean canItemBeExtracted (@Nonnull ItemStack itemPrototype) {
        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean isEmpty () {
        return getStoredItemPrototype().isEmpty();
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

    /*@Override
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
    public boolean isShowingQuantity () {
        return central.isSlotShowingQuantity(slot);
    }

    @Override
    public boolean setIsShowingQuantity (boolean state) {
        return central.setIsSlotShowingQuantity(slot, state);
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
    public void setItemLocked (LockAttribute attr, boolean isLocked) { }*/
}
