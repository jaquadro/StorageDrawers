package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IFractionalDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.*;
import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;

class FractionalStorage
{
    @CapabilityInject(IDrawerAttributes.class)
    static Capability<IDrawerAttributes> ATTR_CAPABILITY = null;

    private int slotCount;
    private ItemStack[] protoStack;
    private int[] convRate;
    private int pooledCount;

    IDrawerAttributes attrs;

    public FractionalStorage (ICapabilityProvider capProvider, int slotCount) {
        this.slotCount = slotCount;

        protoStack = new ItemStack[slotCount];
        for (int i = 0; i < slotCount; i++)
            protoStack[i] = ItemStack.EMPTY;

        convRate = new int[slotCount];

        attrs = capProvider.getCapability(ATTR_CAPABILITY, null);
        if (attrs == null)
            attrs = new EmptyDrawerAttributes();
    }

    @Nonnull
    public ItemStack getStack (int slot) {
        return protoStack[slot];
    }

    public int getConv (int slot) {
        return convRate[slot];
    }

    @Nonnull
    public ItemStack baseStack () {
        return protoStack[0];
    }

    public int baseRate () {
        return convRate[0];
    }

    public int setStoredItem (int slot, @Nonnull ItemStack itemPrototype) {
        itemPrototype = ItemStackHelper.getItemPrototype(itemPrototype);
        if (itemPrototype.isEmpty()) {
            reset();
            return slot;
        }

        if (baseRate() == 0) {
            populateSlots(itemPrototype);
            for (int i = 0; i < slotCount; i++) {
                if (BaseDrawerData.areItemsEqual(protoStack[i], itemPrototype)) {
                    slot = i;
                    pooledCount = 0;
                }
            }

            // TODO: Refresh all slots
            onItemChanged();
        }

        return slot;
    }

    public int getStoredCount (int slot) {
        if (convRate[slot] == 0)
            return 0;

        if (attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        return pooledCount / convRate[slot];
    }

    public void setStoredItemCount (int slot, int amount) {
        if (convRate[slot] == 0)
            return;

        if (attrs.isUnlimitedVending())
            return;

        int oldCount = pooledCount;

        pooledCount = (pooledCount % convRate[slot]) + convRate[slot] * amount;
        pooledCount = Math.min(pooledCount, getMaxCapacity(0) * convRate[0]);
        pooledCount = Math.max(pooledCount, 0);

        if (pooledCount == oldCount)
            return;

        if (pooledCount == 0 && !attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
            reset();
        else
            onAmountChanged();
    }

    public int adjustStoredItemCount (int slot, int amount) {
        if (convRate[slot] == 0 || amount == 0)
            return amount;

        if (amount > 0) {
            if (attrs.isUnlimitedVending())
                return 0;

            int poolMax = getMaxCapacity(0) * convRate[0];
            if (poolMax < 0)
                poolMax = Integer.MAX_VALUE;

            int canAdd = (poolMax - pooledCount) / convRate[slot];
            int willAdd = Math.min(amount, canAdd);
            if (willAdd == 0)
                return amount;

            pooledCount += convRate[slot] * willAdd;

            onAmountChanged();

            if (attrs.isVoid())
                return 0;

            return amount - willAdd;
        }
        else {
            amount = -amount;

            int canRemove = pooledCount / convRate[slot];
            int willRemove = Math.min(amount, canRemove);
            if (willRemove == 0)
                return amount;

            pooledCount -= willRemove;

            if (pooledCount == 0 && !attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
                reset();
            else
                onAmountChanged();

            return amount - willRemove;
        }
    }

    public int getMaxCapacity (int slot) {
        if (baseStack().isEmpty() || convRate[slot] == 0)
            return 0;

        if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending())
            return Integer.MAX_VALUE / convRate[slot];

        return baseStack().getItem().getItemStackLimit(baseStack()) * getStackCapacity() * (baseRate() / convRate[slot]);
    }

    public int getMaxCapacity (int slot, @Nonnull ItemStack itemPrototype) {
        if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending()) {
            if (convRate[slot] == 0)
                return Integer.MAX_VALUE;
            return Integer.MAX_VALUE / convRate[slot];
        }

        if (baseStack().isEmpty()) {
            int itemStackLimit = 64;
            if (!itemPrototype.isEmpty())
                itemStackLimit = itemPrototype.getItem().getItemStackLimit(itemPrototype);
            return itemStackLimit * getStackCapacity();
        }

        if (BaseDrawerData.areItemsEqual(protoStack[slot], itemPrototype))
            return getMaxCapacity(slot);

        return 0;
    }

    public int getRemainingCapacity (int slot) {
        if (baseStack().isEmpty() || convRate[slot] == 0)
            return 0;

        if (attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        int rawMaxCapacity = baseStack().getItem().getItemStackLimit(baseStack()) * getStackCapacity() * baseRate();
        int rawRemaining = rawMaxCapacity - pooledCount;

        return rawRemaining / convRate[slot];
    }

    public boolean isEmpty (int slot) {
        return protoStack[slot].isEmpty();
    }

    private void reset () {
        pooledCount = 0;

        for (int i = 0; i < slotCount; i++) {
            protoStack[i] = ItemStack.EMPTY;
            convRate[i] = 0;
        }

        // TODO: Refresh all slots

        onItemChanged();
    }

    private void populateSlots(@Nonnull ItemStack stack) {

    }

    protected int getStackCapacity() {
        return 0;
    }

    protected void onItemChanged() { }

    protected void onAmountChanged() { }
}

class CDD extends BaseDrawerData
{
    @CapabilityInject(IDrawerAttributes.class)
    static Capability<IDrawerAttributes> ATTR_CAPABILITY = null;

    private FractionalStorage storage;
    private int slot;

    IDrawerAttributes attrs;

    public CDD (ICapabilityProvider capProvider, FractionalStorage storage, int slot) {
        this.storage = storage;
        this.slot = slot;
    }

    @Nonnull
    @Override
    public ItemStack getStoredItemPrototype () {
        return storage.getStack(slot);
    }

    @Override
    public IDrawer setStoredItem (@Nonnull ItemStack itemPrototype) {
        return null;
    }

    @Override
    public int getStoredItemCount () {
        return storage.getStoredCount(slot);
    }
}

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
