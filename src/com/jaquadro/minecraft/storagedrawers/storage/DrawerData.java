package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.api.event.DrawerPopulatedEvent;
import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.*;
import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;

public class DrawerData extends BaseDrawerData
{
    @CapabilityInject(IDrawerAttributes.class)
    static Capability<IDrawerAttributes> ATTR_CAPABILITY = null;

    //private IStorageProvider storageProvider;
    //private ICapabilityProvider capProvider;
    //private int slot;

    IDrawerAttributes attrs;

    @Nonnull
    private ItemStack protoStack;
    private int count;

    private int stackCapacity;

    public DrawerData (ICapabilityProvider capProvider) {
        //this.capProvider = capProvider;

        attrs = capProvider.getCapability(ATTR_CAPABILITY, null);
        if (attrs == null)
            attrs = new EmptyDrawerAttributes();

        protoStack = ItemStack.EMPTY;

        //updateAttributeCache();

        //postInit();
    }

    @Override
    @Nonnull
    public ItemStack getStoredItemPrototype () {
        return protoStack;
    }

    @Override
    public IDrawer setStoredItem (@Nonnull ItemStack itemPrototype) {
        if (areItemsEqual(itemPrototype)) {
            return this;
        }

        itemPrototype = ItemStackHelper.getItemPrototype(itemPrototype);
        if (itemPrototype.isEmpty()) {
            reset();
            return this;
        }

        protoStack = itemPrototype;
        protoStack.setCount(1);
        count = 0;

        // TODO: Oredict blah blah
        // refreshOreDictMatches();

        onItemChanged();
        return this;
    }

    @Override
    public int getStoredItemCount () {
        if (protoStack.isEmpty())
            return 0;

        if (attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        return count;
    }

    @Override
    public void setStoredItemCount (int amount) {
        if (protoStack.isEmpty() || count == amount)
            return;

        if (attrs.isUnlimitedVending())
            return;

        count = Math.min(amount, getMaxCapacity());
        count = Math.max(count, 0);

        if (amount == 0) {
            if (!attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
                reset();
        } else
            onAmountChanged();
    }

    @Override
    public int adjustStoredItemCount (int amount) {
        if (protoStack.isEmpty() || amount == 0)
            return amount;

        if (amount > 0) {
            if (attrs.isUnlimitedVending())
                return 0;

            int originalCount = count;
            count = Math.min(amount, getMaxCapacity());
            onAmountChanged();

            if (attrs.isVoid())
                return 0;

            return amount - (count - originalCount);
        } else {
            int originalCount = count;
            setStoredItemCount(originalCount + amount);

            return amount - (count - originalCount);
        }
    }

    @Override
    public int getMaxCapacity (@Nonnull ItemStack itemPrototype) {
        if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        if (itemPrototype.isEmpty())
            return 64 * getStackCapacity();

        return itemPrototype.getItem().getItemStackLimit(itemPrototype) * getStackCapacity();
    }

    /*@Override
    public int getDefaultMaxCapacity () {
        if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        return 64 * stackCapacity;
    }*/

    @Override
    public int getRemainingCapacity () {
        if (protoStack.isEmpty())
            return 0;

        if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        return getMaxCapacity() - getStoredItemCount();
    }

    /*@Override
    protected int getItemCapacityForInventoryStack () {
        if (attrs.isVoid())
            return Integer.MAX_VALUE;
        else
            return getMaxCapacity();
    }*/

    @Override
    public boolean canItemBeStored (@Nonnull ItemStack itemPrototype) {
        if (protoStack.isEmpty() && !attrs.isItemLocked(LockAttribute.LOCK_EMPTY))
            return true;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean canItemBeExtracted (@Nonnull ItemStack itemPrototype) {
        if (protoStack.isEmpty())
            return false;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean isEmpty () {
        return protoStack.isEmpty();
    }

    /*@Override
    public void attributeChanged () {
        updateAttributeCache();
    }*/

    /*private void updateAttributeCache () {
        stackCapacity = storageProvider.getSlotStackCapacity(slot);
    }*/

    @Override
    protected void reset () {
        protoStack = ItemStack.EMPTY;
        count = 0;

        super.reset();

        onItemChanged();
    }

    @Override
    public NBTTagCompound serializeNBT () {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound item = new NBTTagCompound();
        protoStack.writeToNBT(item);

        tag.setTag("Item", item);
        tag.setInteger("Count", count);

        return tag;
    }

    @Override
    public void deserializeNBT (NBTTagCompound nbt) {
        ItemStack tagItem = ItemStack.EMPTY;
        int tagCount = 0;

        if (nbt.hasKey("Item"))
            tagItem = new ItemStack(nbt.getCompoundTag("Item"));
        if (nbt.hasKey("Count"))
            tagCount = nbt.getInteger("Count");

        setStoredItem(tagItem);
        setStoredItemCount(tagCount);
    }

    protected int getStackCapacity() {
        return stackCapacity;
    }

    // TODO: Handler should also take care of DrawerPopulatedEvent
    // DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
    // MinecraftForge.EVENT_BUS.post(event);
    protected void onItemChanged() { }

    protected void onAmountChanged() { }
}

