package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.event.DrawerPopulatedEvent;
import com.jaquadro.minecraft.storagedrawers.api.inventory.IInventoryAdapter;
import com.jaquadro.minecraft.storagedrawers.api.inventory.SlotType;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.inventory.InventoryStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class DrawerData extends BaseDrawerData
{
    private static final ItemStack nullStack = new ItemStack((Item)null);

    private IStorageProvider storageProvider;
    private int slot;

    private ItemStack protoStack;
    private int count;

    public DrawerData (IStorageProvider provider, int slot) {
        storageProvider = provider;
        protoStack = nullStack;
        this.slot = slot;

        postInit();
    }

    @Override
    public ItemStack getStoredItemPrototype () {
        if (protoStack == nullStack)
            return null;

        return protoStack;
    }

    @Override
    public void setStoredItem (ItemStack itemPrototype, int amount) {
        setStoredItem(itemPrototype, amount, true);
    }

    private void setStoredItem (ItemStack itemPrototype, int amount, boolean mark) {
        if (itemPrototype == null) {
            setStoredItemCount(0, false, true);
            protoStack = nullStack;
            inventoryStack.reset();

            DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
            MinecraftForge.EVENT_BUS.post(event);

            if (mark)
                storageProvider.markDirty(slot);
            return;
        }

        protoStack = itemPrototype.copy();
        protoStack.stackSize = 1;

        refreshOreDictMatches();
        setStoredItemCount(amount, mark, false);
        inventoryStack.reset();

        DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
        MinecraftForge.EVENT_BUS.post(event);

        if (mark)
            storageProvider.markDirty(slot);
    }

    @Override
    public int getStoredItemCount () {
        return count;
    }

    @Override
    public void setStoredItemCount (int amount) {
        setStoredItemCount(amount, true, true);
    }

    public void setStoredItemCount (int amount, boolean mark, boolean clearOnEmpty) {
        count = amount;
        if (amount == 0) {
            if (clearOnEmpty) {
                if (!storageProvider.isLocked(slot))
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
        if (protoStack.getItem() == null)
            return 0;

        return protoStack.getItem().getItemStackLimit(protoStack) * storageProvider.getSlotStackCapacity(slot);
    }

    @Override
    public int getRemainingCapacity () {
        if (protoStack.getItem() == null)
            return 0;

        return getMaxCapacity() - getStoredItemCount();
    }

    @Override
    public int getStoredItemStackSize () {
        if (protoStack.getItem() == null)
            return 0;

        return protoStack.getItem().getItemStackLimit(protoStack);
    }

    @Override
    public boolean canItemBeStored (ItemStack itemPrototype) {
        if (protoStack == nullStack)
            return true;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean canItemBeExtracted (ItemStack itemPrototype) {
        if (protoStack == nullStack)
            return false;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean isEmpty () {
        return protoStack == nullStack;
    }

    public void writeToNBT (NBTTagCompound tag) {
        if (protoStack.getItem() != null) {
            tag.setShort("Item", (short) Item.getIdFromItem(protoStack.getItem()));
            tag.setShort("Meta", (short) protoStack.getItemDamage());
            tag.setInteger("Count", count);

            if (protoStack.getTagCompound() != null)
                tag.setTag("Tags", protoStack.getTagCompound());
        }
    }

    public void readFromNBT (NBTTagCompound tag) {
        if (tag.hasKey("Item") && tag.hasKey("Count")) {
            ItemStack stack = new ItemStack(Item.getItemById(tag.getShort("Item")));
            stack.setItemDamage(tag.getShort("Meta"));
            if (tag.hasKey("Tags"))
                stack.setTagCompound(tag.getCompoundTag("Tags"));

            setStoredItem(stack, tag.getInteger("Count"), false);
        }
        else {
            reset();
        }
    }

    @Override
    protected void reset () {
        protoStack = nullStack;
        super.reset();

        DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
    }
}

