package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.inventory.IInventoryAdapter;
import com.jaquadro.minecraft.storagedrawers.api.inventory.SlotType;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.inventory.InventoryStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class DrawerData implements IDrawer, IInventoryAdapter
{
    private static final ItemStack nullStack = new ItemStack((Item)null);

    private IStorageProvider storageProvider;
    private int slot;

    private ItemStack protoStack;
    private InventoryStack inventoryStack;

    private int count;
    private List<ItemStack> oreDictMatches;

    public DrawerData (IStorageProvider provider, int slot) {
        storageProvider = provider;
        this.slot = slot;

        protoStack = nullStack;
        inventoryStack = new DrawerInventoryStack();
    }

    @Override
    public ItemStack getStoredItemPrototype () {
        if (protoStack == nullStack)
            return null;

        return protoStack;
    }

    @Override
    public ItemStack getStoredItemCopy () {
        if (protoStack == nullStack)
            return null;

        ItemStack stack = protoStack.copy();
        stack.stackSize = count;

        return stack;
    }

    @Override
    public void setStoredItem (ItemStack itemPrototype, int amount) {
        setStoredItem(itemPrototype, amount, true);
    }

    private void setStoredItem (ItemStack itemPrototype, int amount, boolean mark) {
        if (itemPrototype == null) {
            reset();
            if (mark)
                storageProvider.markDirty(slot);
            return;
        }

        protoStack = itemPrototype.copy();
        protoStack.stackSize = 1;

        refreshOreDictMatches();
        setStoredItemCount(amount, mark, false);
        inventoryStack.reset();

        if (mark)
            storageProvider.markDirty(slot);
    }

    @Override
    public int getStoredItemCount () {
        if (storageProvider.isCountCentrallyManaged())
            return storageProvider.getSlotCount(slot);
        else
            return count;
    }

    @Override
    public void setStoredItemCount (int amount) {
        setStoredItemCount(amount, true, true);
    }

    public void setStoredItemCount (int amount, boolean mark, boolean clearOnEmpty) {
        if (storageProvider.isCountCentrallyManaged())
            storageProvider.setSlotCount(amount);
        else
            count = amount;

        if (amount == 0) {
            if (clearOnEmpty) {
                protoStack = nullStack;
                oreDictMatches = null;
                inventoryStack.reset();
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
            protoStack = nullStack;
            oreDictMatches = null;
            inventoryStack.reset();
        }
    }

    private void reset () {
        setStoredItemCount(0, false, true);
        protoStack = nullStack;
        oreDictMatches = null;
        inventoryStack.reset();
    }

    public boolean areItemsEqual (ItemStack item) {
        return areItemsEqual(protoStack, item);
    }

    private void refreshOreDictMatches () {
        int[] oreIDs = OreDictionary.getOreIDs(protoStack);
        if (oreIDs.length == 0)
            oreDictMatches = null;
        else {
            oreDictMatches = new ArrayList<ItemStack>();
            for (int id : oreIDs) {
                if (StorageDrawers.oreDictRegistry.isEntryBlacklisted(OreDictionary.getOreName(id)))
                    continue;

                List<ItemStack> list = OreDictionary.getOres(OreDictionary.getOreName(id));
                for (int i = 0, n = list.size(); i < n; i++)
                    oreDictMatches.add(list.get(i));
            }

            if (oreDictMatches.size() == 0)
                oreDictMatches = null;
        }
    }

    public static boolean areItemsEqual (ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null)
            return false;
        if (stack1.getItem() == null || stack2.getItem() == null)
            return false;

        if (!stack1.isItemEqual(stack2)) {
            int[] ids1 = OreDictionary.getOreIDs(stack1);
            int[] ids2 = OreDictionary.getOreIDs(stack2);
            if (ids1.length == 0 || ids2.length == 0)
                return false;

            boolean oreMatch = false;

            BRK_ORE_MATCH:
            for (int oreIndexLeft : ids1) {
                if (StorageDrawers.oreDictRegistry.isEntryBlacklisted(OreDictionary.getOreName(oreIndexLeft)))
                    continue;

                for (int oreIndexRight : ids2) {
                    if (StorageDrawers.oreDictRegistry.isEntryBlacklisted(OreDictionary.getOreName(oreIndexRight)))
                        continue;

                    if (oreIndexLeft == oreIndexRight) {
                        oreMatch = true;
                        break BRK_ORE_MATCH;
                    }
                }
            }

            if (!oreMatch)
                return false;
        }

        return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    @Override
    public ItemStack getInventoryStack (SlotType slotType) {
        switch (slotType) {
            case INPUT:
                return inventoryStack.getInStack();
            case OUTPUT:
                return inventoryStack.getOutStack();
            default:
                return inventoryStack.getNativeStack();
        }
    }

    @Override
    public void syncInventory () {
        inventoryStack.markDirty();
    }

    class DrawerInventoryStack extends InventoryStack
    {
        public DrawerInventoryStack () {
            init();
        }

        @Override
        protected ItemStack getNewItemStack () {
            return getStoredItemCopy();
        }

        @Override
        protected int getItemStackSize () {
            return getStoredItemStackSize();
        }

        @Override
        protected int getItemCount () {
            return getStoredItemCount();
        }

        @Override
        protected int getItemCapacity () {
            return getMaxCapacity();
        }

        @Override
        protected void applyDiff (int diff) {
            if (diff != 0)
                setStoredItemCount(getStoredItemCount() + diff);
        }
    }
}

