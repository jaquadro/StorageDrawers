/*package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.StandardDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;

public class DrawerData extends BaseDrawerData
{
    @CapabilityInject(IDrawerAttributes.class)
    static Capability<IDrawerAttributes> ATTR_CAPABILITY = null;

    IDrawerAttributes attrs;
    StandardDrawerGroup group;

    @Nonnull
    private ItemStack protoStack;
    private int count;


    public DrawerData (StandardDrawerGroup group) {
        this.group = group;
        attrs = new EmptyDrawerAttributes();
        protoStack = ItemStack.EMPTY;
    }

    public void setCapabilityProvider (ICapabilityProvider capProvider) {
        IDrawerAttributes capAttrs = capProvider.getCapability(ATTR_CAPABILITY, null);
        if (capAttrs != null)
            attrs = capAttrs;
    }

    @Override
    @Nonnull
    public ItemStack getStoredItemPrototype () {
        return protoStack;
    }

    @Override
    @Nonnull
    public IDrawer setStoredItem (@Nonnull ItemStack itemPrototype) {
        return setStoredItem(itemPrototype, true);
    }

    protected IDrawer setStoredItem (@Nonnull ItemStack itemPrototype, boolean notify) {
        if (areItemsEqual(itemPrototype)) {
            return this;
        }

        itemPrototype = ItemStackHelper.getItemPrototype(itemPrototype);
        if (itemPrototype.isEmpty()) {
            reset(notify);
            return this;
        }

        protoStack = itemPrototype;
        protoStack.setCount(1);
        count = 0;

        // TODO: Oredict blah blah
        // refreshOreDictMatches();

        group.syncSlots();
        if (notify)
            onItemChanged();

        return this;
    }

    protected IDrawer setStoredItemRaw (@Nonnull ItemStack itemPrototype) {
        itemPrototype = ItemStackHelper.getItemPrototype(itemPrototype);
        protoStack = itemPrototype;
        protoStack.setCount(1);
        count = 0;

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
        setStoredItemCount(amount, true);
    }

    protected void setStoredItemCount (int amount, boolean notify) {
        if (protoStack.isEmpty() || count == amount)
            return;

        if (attrs.isUnlimitedVending())
            return;

        count = Math.min(amount, getMaxCapacity());
        count = Math.max(count, 0);

        if (amount == 0 && !attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
            reset(notify);
        else {
            if (notify)
                onAmountChanged();
        }
    }

    protected void setStoredItemCountRaw (int amount) {
        count = amount;
    }

    @Override
    public int adjustStoredItemCount (int amount) {
        return adjustStoredItemCount(amount, true);
    }

    protected int adjustStoredItemCount (int amount, boolean notify) {
        if (protoStack.isEmpty() || amount == 0)
            return amount;

        if (amount > 0) {
            if (attrs.isUnlimitedVending())
                return 0;

            int originalCount = count;
            count = Math.min(count + amount, getMaxCapacity());

            if (count != originalCount && notify)
                onAmountChanged();

            if (attrs.isVoid())
                return 0;

            return amount - (count - originalCount);
        }
        else {
            int originalCount = count;
            setStoredItemCount(originalCount + amount, notify);

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

    @Override
    public int getRemainingCapacity () {
        if (protoStack.isEmpty())
            return 0;

        if (attrs.isUnlimitedVending())
            return Integer.MAX_VALUE;

        return getMaxCapacity() - getStoredItemCount();
    }

    @Override
    public int getAcceptingRemainingCapacity () {
        if (protoStack.isEmpty())
            return 0;

        if (attrs.isUnlimitedVending() || attrs.isVoid())
            return Integer.MAX_VALUE;

        return getMaxCapacity() - getStoredItemCount();
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
    protected void reset (boolean notify) {
        protoStack = ItemStack.EMPTY;
        count = 0;

        super.reset(notify);

        group.syncSlots();
        if (notify)
            onItemChanged();
    }

    @Override
    public NBTTagCompound serializeNBT () {
        NBTTagCompound tag = new NBTTagCompound();
        if (protoStack.isEmpty())
            return tag;

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

        setStoredItemRaw(tagItem);
        setStoredItemCountRaw(tagCount);
    }

    public void deserializeLegacyNBT (NBTTagCompound nbt) {
        ItemStack tagItem = ItemStack.EMPTY;
        int tagCount = 0;

        if (nbt.hasKey("Count"))
            tagCount = nbt.getInteger("Count");
        if (nbt.hasKey("Item")) {
            Item item = Item.getItemById(nbt.getShort("Item"));
            if (item != null) {
                tagItem = new ItemStack(item);
                tagItem.setItemDamage(nbt.getShort("Meta"));
                if (nbt.hasKey("Tags"))
                    tagItem.setTagCompound(nbt.getCompoundTag("Tags"));
            }
        }

        setStoredItemRaw(tagItem);
        setStoredItemCountRaw(tagCount);
    }

    protected int getStackCapacity() {
        return 0;
    }

    protected void onItemChanged() { }

    protected void onAmountChanged() { }
}

*/