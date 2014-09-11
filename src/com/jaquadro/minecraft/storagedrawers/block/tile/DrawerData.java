package com.jaquadro.minecraft.storagedrawers.block.tile;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class DrawerData
{
    private int slot;
    private Item item;
    public int meta;
    public int count;
    public NBTTagCompound attrs;

    private IStorageProvider storageProvider;
    private ItemStack protoStack;

    public DrawerData (IStorageProvider storageProvider, int slot) {
        this.storageProvider = storageProvider;
        this.slot = slot;

        reset();
    }

    public Item getItem () {
        return item;
    }

    public void setItem (Item item) {
        this.item = item;
        protoStack = new ItemStack(item);
    }

    public void writeToNBT (NBTTagCompound tag) {
        if (item != null) {
            tag.setShort("Item", (short) Item.getIdFromItem(item));
            tag.setShort("Meta", (short) meta);
            tag.setInteger("Count", count);

            if (attrs != null)
                tag.setTag("Tags", attrs);
        }
    }

    public void readFromNBT (NBTTagCompound tag) {
        if (tag.hasKey("Item")) {
            item = Item.getItemById(tag.getShort("Item"));
            meta = tag.getShort("Meta");
            count = tag.getInteger("Count");

            if (tag.hasKey("Tags"))
                attrs = tag.getCompoundTag("Tags");

            protoStack = new ItemStack(item);
        }
    }

    public void reset () {
        item = null;
        meta = 0;
        count = 0;
        attrs = null;
    }

    public int stackCapacity () {
        return storageProvider.getSlotCapacity(slot);
    }

    public int itemStackMaxSize () {
        if (item == null)
            return 0;

        return item.getItemStackLimit(protoStack);
    }

    public int maxCapacity () {
        if (item == null)
            return 0;

        protoStack.setItemDamage(meta);
        protoStack.setTagCompound(attrs);

        return item.getItemStackLimit(protoStack) * stackCapacity();
    }

    public int remainingCapacity () {
        if (item == null)
            return 0;

        return maxCapacity() - count;
    }
}
