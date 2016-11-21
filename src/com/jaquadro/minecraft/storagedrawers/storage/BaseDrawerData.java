package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDrawerData implements IDrawer
{
    private List<ItemStack> oreDictMatches;
    private Map<String, Object> auxData;

    protected BaseDrawerData () {

    }

    protected void postInit () {

    }

    protected void reset () {
        oreDictMatches = null;
    }

    protected void refreshOreDictMatches () {
        ItemStack protoStack = getStoredItemPrototype();
        if (protoStack.func_190926_b()) {
            oreDictMatches = null;
            return;
        }

        int[] oreIDs = OreDictionary.getOreIDs(protoStack);
        if (oreIDs.length == 0)
            oreDictMatches = null;
        else {
            oreDictMatches = new ArrayList<>();
            for (int id : oreIDs) {
                String oreName = OreDictionary.getOreName(id);
                if (!StorageDrawers.oreDictRegistry.isEntryValid(oreName))
                    continue;

                List<ItemStack> list = OreDictionary.getOres(oreName);
                for (ItemStack aList : list) {
                    if (aList.getItemDamage() == OreDictionary.WILDCARD_VALUE)
                        continue;
                    oreDictMatches.add(aList);
                }
            }

            if (oreDictMatches.size() == 0)
                oreDictMatches = null;
        }
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
            auxData = new HashMap<>();

        auxData.put(key, data);
    }

    @Override
    public void attributeChanged () { }

    protected int getItemCapacityForInventoryStack () {
        return getMaxCapacity();
    }

    public boolean areItemsEqual (@Nonnull ItemStack item) {
        ItemStack protoStack = getStoredItemPrototype();
        if (!protoStack.func_190926_b() && !protoStack.isItemEqual(item)) {
            if (!StorageDrawers.config.cache.enableItemConversion)
                return false;
            if (oreDictMatches == null)
                return false;
            if (protoStack.getItem() == item.getItem())
                return false;

            boolean oreMatch = false;
            for (ItemStack oreDictMatche : oreDictMatches) {
                if (item.isItemEqual(oreDictMatche)) {
                    oreMatch = true;
                    break;
                }
            }

            if (!oreMatch)
                return false;
        }

        return ItemStack.areItemStackTagsEqual(protoStack, item);
    }

    public static boolean areItemsEqual (@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        return areItemsEqual(stack1, stack2, true);
    }

    public static boolean areItemsEqual (@Nonnull ItemStack stack1, @Nonnull ItemStack stack2, boolean oreDictStrictMode) {
        if (!stack1.func_190926_b() && !stack2.func_190926_b() && !stack1.isItemEqual(stack2)) {
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
                    if (!oreDictStrictMode || StorageDrawers.oreDictRegistry.isEntryValid(name)) {
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
}
