package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.inventory.IInventoryAdapter;
import com.jaquadro.minecraft.storagedrawers.api.inventory.SlotType;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.inventory.InventoryStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDrawerData implements IDrawer, IInventoryAdapter
{
    protected InventoryStack inventoryStack;
    private List<ItemStack> oreDictMatches;

    protected BaseDrawerData () {
        inventoryStack = new DrawerInventoryStack();
    }

    protected void postInit () {
        inventoryStack.init();
    }

    public boolean areItemsEqual (ItemStack item) {
        return areItemsEqual(getStoredItemPrototype(), item);
    }

    protected void reset () {
        oreDictMatches = null;
        inventoryStack.reset();
    }

    @Override
    public ItemStack getStoredItemCopy () {
        ItemStack protoStack = getStoredItemPrototype();
        if (protoStack == null)
            return null;

        ItemStack stack = protoStack.copy();
        stack.stackSize = getStoredItemCount();

        return stack;
    }

    protected void refreshOreDictMatches () {
        int[] oreIDs = OreDictionary.getOreIDs(getStoredItemPrototype());
        if (oreIDs.length == 0)
            oreDictMatches = null;
        else {
            oreDictMatches = new ArrayList<ItemStack>();
            for (int id : oreIDs) {
                if (StorageDrawers.oreDictRegistry.isEntryBlacklisted(OreDictionary.getOreName(id)))
                    continue;

                List<ItemStack> list = OreDictionary.getOres(OreDictionary.getOreName(id));
                for (int i = 0, n = list.size(); i < n; i++) {
                    if (list.get(i).getItemDamage() == OreDictionary.WILDCARD_VALUE)
                        continue;
                    oreDictMatches.add(list.get(i));
                }
            }

            if (oreDictMatches.size() == 0)
                oreDictMatches = null;
        }
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
                        List<ItemStack> oreList = OreDictionary.getOres(OreDictionary.getOreName(oreIndexLeft));
                        for (int i = 0, n = oreList.size(); i < n; i++) {
                            if (stack1.isItemEqual(oreList.get(i)))
                                oreMatch = true;
                        }
                        if (!oreMatch)
                            continue;

                        oreMatch = false;
                        for (int i = 0, n = oreList.size(); i < n; i++) {
                            if (stack2.isItemEqual(oreList.get(i)))
                                oreMatch = true;
                        }
                        if (!oreMatch)
                            continue;

                        oreMatch = false;
                        for (int i = 0, n = oreList.size(); i < n; i++) {
                            if (oreList.get(i).getItemDamage() != OreDictionary.WILDCARD_VALUE)
                                oreMatch = true;
                        }
                        if (!oreMatch)
                            continue;

                        break BRK_ORE_MATCH;
                    }
                }
            }

            if (!oreMatch)
                return false;
        }

        return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    class DrawerInventoryStack extends InventoryStack
    {
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
