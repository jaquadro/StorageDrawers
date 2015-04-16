package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.inventory.IInventoryAdapter;
import com.jaquadro.minecraft.storagedrawers.api.inventory.SlotType;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.inventory.InventoryStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDrawerData implements IDrawer, IInventoryAdapter
{
    protected InventoryStack inventoryStack;
    private List<ItemStack> oreDictMatches;
    private Map<String, Object> auxData;

    protected BaseDrawerData () {
        inventoryStack = new DrawerInventoryStack();
    }

    protected void postInit () {
        inventoryStack.init();
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
                String oreName = OreDictionary.getOreName(id);
                if (!StorageDrawers.oreDictRegistry.isEntryValid(oreName))
                    continue;

                List<ItemStack> list = OreDictionary.getOres(oreName);
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

    public boolean syncInventoryIfNeeded () {
        return inventoryStack.markDirtyIfNeeded();
    }

    @Override
    public Object getExtendedData (String key) {
        if (auxData == null || !auxData.containsKey(key))
            return null;

        return auxData.get(key);
    }

    @Override
    public void setExtendedData (String key, Object data) {
        if (auxData == null)
            auxData = new HashMap<String, Object>();

        auxData.put(key, data);
    }

    public boolean areItemsEqual (ItemStack item) {
        ItemStack protoStack = getStoredItemPrototype();
        if (protoStack == null || item == null)
            return false;
        if (protoStack.getItem() == null || item.getItem() == null)
            return false;

        if (!protoStack.isItemEqual(item)) {
            if (!StorageDrawers.config.cache.enableItemConversion)
                return false;
            if (oreDictMatches == null)
                return false;
            if (protoStack.getItem() == item.getItem())
                return false;

            boolean oreMatch = false;
            for (int i = 0, n = oreDictMatches.size(); i < n; i++) {
                if (item.isItemEqual(oreDictMatches.get(i))) {
                    oreMatch = true;
                    break;
                }
            }

            if (!oreMatch)
                return false;
        }

        return ItemStack.areItemStackTagsEqual(protoStack, item);
    }

    public static boolean areItemsEqual (ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null)
            return false;
        if (stack1.getItem() == null || stack2.getItem() == null)
            return false;

        if (!stack1.isItemEqual(stack2)) {
            if (!StorageDrawers.config.cache.enableItemConversion)
                return false;
            if (stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE)
                return false;
            if (stack1.getItem() == stack2.getItem())
                return false;

            int[] ids1 = OreDictionary.getOreIDs(stack1);
            int[] ids2 = OreDictionary.getOreIDs(stack2);
            if (ids1.length == 0 || ids2.length == 0)
                return false;

            boolean oreMatch = false;
            for (int id1 : ids1) {
                for (int id2 : ids2) {
                    if (id1 != id2)
                        continue;

                    String name = OreDictionary.getOreName(id1);
                    if (StorageDrawers.oreDictRegistry.isEntryValid(name)) {
                        oreMatch = true;
                        break;
                    }
                }

                if (oreMatch)
                    break;
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
