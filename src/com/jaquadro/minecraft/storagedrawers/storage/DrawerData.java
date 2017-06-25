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

    private IStorageProvider storageProvider;
    private ICapabilityProvider capProvider;
    private int slot;

    IDrawerAttributes attrs;

    @Nonnull
    private ItemStack protoStack;
    private int count;

    private int stackCapacity;

    public DrawerData (IStorageProvider provider, ICapabilityProvider capProvider, int slot) {
        storageProvider = provider;
        this.capProvider = capProvider;

        attrs = capProvider.getCapability(ATTR_CAPABILITY, null);
        if (attrs == null)
            attrs = new EmptyDrawerAttributes();

        protoStack = ItemStack.EMPTY;
        this.slot = slot;

        updateAttributeCache();

        postInit();
    }

    @Override
    @Nonnull
    public ItemStack getStoredItemPrototype () {
        return protoStack;
    }

    @Override
    public IDrawer setStoredItem (@Nonnull ItemStack itemPrototype, int amount) {
        setStoredItem(itemPrototype, amount, true);
        return this;
    }

    private void setStoredItem (@Nonnull ItemStack itemPrototype, int amount, boolean mark) {
        itemPrototype = ItemStackHelper.getItemPrototype(itemPrototype);
        if (itemPrototype.isEmpty()) {
            setStoredItemCount(0, false, true);
            protoStack = ItemStack.EMPTY;

            DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
            MinecraftForge.EVENT_BUS.post(event);

            if (mark)
                storageProvider.markDirty(slot);
            return;
        }

        protoStack = itemPrototype;
        protoStack.setCount(1);

        refreshOreDictMatches();
        setStoredItemCount(amount, mark, false);

        DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
        MinecraftForge.EVENT_BUS.post(event);

        if (mark)
            storageProvider.markDirty(slot);
    }

    @Override
    public int getStoredItemCount () {
        if (!protoStack.isEmpty() && attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        return count;
    }

    @Override
    public void setStoredItemCount (int amount) {
        setStoredItemCount(amount, true, true);
    }

    public void setStoredItemCount (int amount, boolean mark, boolean clearOnEmpty) {
        if (protoStack.isEmpty() || attrs.isUnlimitedVending())
            return;

        count = amount;
        if (count > getMaxCapacity())
            count = getMaxCapacity();

        if (amount == 0) {
            if (clearOnEmpty) {
                if (!attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
                    reset();
                if (mark)
                    storageProvider.markDirty(slot);
            }
        }
        else if (mark)
            storageProvider.markAmountDirty(slot);
    }

    @Override
    public int getMaxCapacity () {
        return getMaxCapacity(protoStack);
    }

    @Override
    public int getMaxCapacity (@Nonnull ItemStack itemPrototype) {
        if (itemPrototype.isEmpty())
            return 0;

        if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        return itemPrototype.getItem().getItemStackLimit(itemPrototype) * stackCapacity;
    }

    @Override
    public int getDefaultMaxCapacity () {
        if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        return 64 * stackCapacity;
    }

    @Override
    public int getRemainingCapacity () {
        if (protoStack.isEmpty())
            return 0;

        if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        return getMaxCapacity() - getStoredItemCount();
    }

    @Override
    public int getStoredItemStackSize () {
        if (protoStack.isEmpty())
            return 0;

        return protoStack.getItem().getItemStackLimit(protoStack);
    }

    @Override
    protected int getItemCapacityForInventoryStack () {
        if (attrs.isVoid())
            return Integer.MAX_VALUE;
        else
            return getMaxCapacity();
    }

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

    @Override
    public void attributeChanged () {
        updateAttributeCache();
    }

    private void updateAttributeCache () {
        stackCapacity = storageProvider.getSlotStackCapacity(slot);
    }

    public void writeToNBT (NBTTagCompound tag) {
        if (!protoStack.isEmpty()) {
            tag.setShort("Item", (short) Item.getIdFromItem(protoStack.getItem()));
            tag.setShort("Meta", (short) protoStack.getMetadata());
            tag.setInteger("Count", count);

            if (protoStack.getTagCompound() != null)
                tag.setTag("Tags", protoStack.getTagCompound());
        }
    }

    public void readFromNBT (NBTTagCompound tag) {
        if (tag.hasKey("Item") && tag.hasKey("Count")) {
            Item item = Item.getItemById(tag.getShort("Item"));
            if (item != null) {
                ItemStack stack = new ItemStack(item);
                stack.setItemDamage(tag.getShort("Meta"));
                if (tag.hasKey("Tags"))
                    stack.setTagCompound(tag.getCompoundTag("Tags"));

                setStoredItem(stack, tag.getInteger("Count"), false);
            }
            else {
                reset();
            }
        }
        else {
            reset();
        }
    }

    @Override
    protected void reset () {
        protoStack = ItemStack.EMPTY;
        super.reset();

        DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
    }
}

