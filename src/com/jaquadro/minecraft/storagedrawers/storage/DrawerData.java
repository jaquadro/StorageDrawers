package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.api.event.DrawerPopulatedEvent;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IItemLockable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IShroudable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

public class DrawerData extends BaseDrawerData implements IVoidable, IShroudable, IItemLockable
{
    private IStorageProvider storageProvider;
    private int slot;

    @Nonnull
    private ItemStack protoStack;

    private boolean isUnlimited;
    private int stackCapacity;

    public DrawerData (IStorageProvider provider, int slot) {
        storageProvider = provider;
        protoStack = ItemStack.field_190927_a;
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
        if (itemPrototype.func_190926_b()) {
            setStoredItemCount(0, false, true);
            protoStack = ItemStack.field_190927_a;

            DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
            MinecraftForge.EVENT_BUS.post(event);

            if (mark)
                storageProvider.markDirty(slot);
            return;
        }

        protoStack = itemPrototype.copy();
        protoStack.func_190920_e(0);

        refreshOreDictMatches();
        setStoredItemCount(amount, mark, false);

        DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
        MinecraftForge.EVENT_BUS.post(event);

        if (mark)
            storageProvider.markDirty(slot);
    }

    @Override
    public int getStoredItemCount () {
        if (!protoStack.func_190926_b() && storageProvider.isVendingUnlimited(slot))
            return Integer.MAX_VALUE;

        return protoStack.func_190916_E();
    }

    @Override
    public void setStoredItemCount (int amount) {
        setStoredItemCount(amount, true, true);
    }

    public void setStoredItemCount (int amount, boolean mark, boolean clearOnEmpty) {
        if (protoStack.func_190926_b() || storageProvider.isVendingUnlimited(slot))
            return;

        protoStack.func_190920_e(Math.min(amount, getMaxCapacity()));

        if (amount == 0) {
            if (clearOnEmpty) {
                if (!storageProvider.isLocked(slot, LockAttribute.LOCK_POPULATED))
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
        if (itemPrototype.func_190926_b())
            return 0;

        if (isUnlimited)
            return Integer.MAX_VALUE;

        return itemPrototype.getItem().getItemStackLimit(itemPrototype) * stackCapacity;
    }

    @Override
    public int getRemainingCapacity () {
        if (protoStack.func_190926_b())
            return 0;

        if (storageProvider.isVendingUnlimited(slot))
            return Integer.MAX_VALUE;

        return getMaxCapacity() - getStoredItemCount();
    }

    @Override
    public int getStoredItemStackSize () {
        if (protoStack.func_190926_b())
            return 0;

        return protoStack.getItem().getItemStackLimit(protoStack);
    }

    @Override
    protected int getItemCapacityForInventoryStack () {
        if (storageProvider.isVoid(slot))
            return Integer.MAX_VALUE;
        else
            return getMaxCapacity();
    }

    @Override
    public boolean canItemBeStored (@Nonnull ItemStack itemPrototype) {
        if (protoStack.func_190926_b() && !isItemLocked(LockAttribute.LOCK_EMPTY))
            return true;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean canItemBeExtracted (@Nonnull ItemStack itemPrototype) {
        if (protoStack.func_190926_b())
            return false;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean isEmpty () {
        return protoStack.func_190926_b();
    }

    @Override
    public void attributeChanged () {
        updateAttributeCache();
    }

    private void updateAttributeCache () {
        isUnlimited = storageProvider.isStorageUnlimited(slot) || storageProvider.isVendingUnlimited(slot);
        stackCapacity = storageProvider.getSlotStackCapacity(slot);
    }

    public void writeToNBT (NBTTagCompound tag) {
        if (!protoStack.func_190926_b()) {
            tag.setShort("Item", (short) Item.getIdFromItem(protoStack.getItem()));
            tag.setShort("Meta", (short) protoStack.getItemDamage());
            tag.setInteger("Count", protoStack.func_190916_E());

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
        protoStack = ItemStack.field_190927_a;
        super.reset();

        DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Override
    public boolean isVoid () {
        return storageProvider.isVoid(slot);
    }

    @Override
    public boolean isShrouded () {
        return storageProvider.isShrouded(slot);
    }

    @Override
    public boolean setIsShrouded (boolean state) {
        return storageProvider.setIsShrouded(slot, state);
    }

    @Override
    public boolean isItemLocked (LockAttribute attr) {
        return storageProvider.isLocked(slot, attr);
    }

    @Override
    public boolean canItemLock (LockAttribute attr) {
        return false;
    }

    @Override
    public void setItemLocked (LockAttribute attr, boolean isLocked) { }
}

