package com.jaquadro.minecraft.storagedrawers.block.tile;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class DrawerData
{
    private static final ItemStack nullStack = new ItemStack((Item)null);

    private int slot;
    //private Item item;
    //private int meta;
    public int count;
    //private NBTTagCompound attrs;

    private IStorageProvider storageProvider;
    private ItemStack protoStack;

    private List<ItemStack> oreDictMatches;

    public DrawerData (IStorageProvider storageProvider, int slot) {
        this.storageProvider = storageProvider;
        this.slot = slot;
        this.protoStack = nullStack;

        reset();
    }

    public Item getItem () {
        return protoStack.getItem();
    }

    public int getMeta () {
        return protoStack.getItemDamage();
    }

    public NBTTagCompound getAttrs () {
        return protoStack.getTagCompound();
    }

    public void setAttrs (NBTTagCompound attrs) {
        if (protoStack != nullStack)
            protoStack.setTagCompound(attrs);
    }

    public void setItem (Item item, int meta) {
        setItem(item, meta, null);
    }

    public void setItem (Item item, int meta, NBTTagCompound attrs) {
        protoStack = new ItemStack(item, 1, meta);
        protoStack.setTagCompound(attrs);

        int[] oreIDs = OreDictionary.getOreIDs(protoStack);
        if (oreIDs.length == 0)
            oreDictMatches = null;
        else if (oreIDs.length == 1)
            oreDictMatches = OreDictionary.getOres(OreDictionary.getOreName(oreIDs[0]));
        else {
            oreDictMatches = new ArrayList<ItemStack>();
            for (int id : oreIDs) {
                List<ItemStack> list = OreDictionary.getOres(OreDictionary.getOreName(id));
                for (ItemStack oreItem : list)
                    oreDictMatches.add(oreItem);
            }
        }
    }

    public void setItem (ItemStack stack) {
        setItem(stack.getItem(), stack.getItemDamage(), stack.getTagCompound());
    }

    // Can't actually enforce write access, so take this as a severe advisory.
    // Don't needlessly force me to feed the Garbage Collector, folks.
    public ItemStack getReadOnlyItemStack () {
        return protoStack;
    }

    public ItemStack getNewItemStack () {
        if (protoStack == null)
            return null;

        return protoStack.copy();
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
        protoStack = nullStack;

        if (tag.hasKey("Item")) {
            setItem(Item.getItemById(tag.getShort("Item")), tag.getShort("Meta"));
            count = tag.getInteger("Count");

            if (tag.hasKey("Tags"))
                protoStack.setTagCompound(tag.getCompoundTag("Tags"));
        }
    }

    public void reset () {
        count = 0;
        protoStack = nullStack;
        oreDictMatches = null;
    }

    public int stackCapacity () {
        return storageProvider.getSlotCapacity(slot);
    }

    public int itemStackMaxSize () {
        if (protoStack.getItem() == null)
            return 0;

        return protoStack.getItem().getItemStackLimit(protoStack);
    }

    public int maxCapacity () {
        if (protoStack.getItem() == null)
            return 0;

        return protoStack.getItem().getItemStackLimit(protoStack) * stackCapacity();
    }

    public int remainingCapacity () {
        if (protoStack.getItem() == null)
            return 0;

        return maxCapacity() - count;
    }

    public boolean areItemsEqual (ItemStack stack) {
        if (protoStack == null || stack == null)
            return false;
        if (protoStack.getItem() == null || stack.getItem() == null)
            return false;

        if (!protoStack.isItemEqual(stack)) {
            if (oreDictMatches == null)
                return false;

            boolean oreMatch = false;
            for (int i = 0; i < oreDictMatches.size(); i++) {
                oreMatch |= stack.isItemEqual(oreDictMatches.get(i));
                if (oreMatch)
                    break;
            }

            if (!oreMatch)
                return false;
        }

        return ItemStack.areItemStackTagsEqual(protoStack, stack);
    }
}
