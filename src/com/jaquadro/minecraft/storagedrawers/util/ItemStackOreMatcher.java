package com.jaquadro.minecraft.storagedrawers.util;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemStackOreMatcher extends ItemStackMatcher
{
    private List<ItemStack> oreDictMatches;

    public ItemStackOreMatcher (@Nonnull ItemStack stack) {
        super(stack);
        refreshOreDictMatches();
    }

    @Override
    public boolean matches (@Nonnull ItemStack stack) {
        if (!this.stack.isItemEqual(stack)) {
            if (oreDictMatches == null)
                return false;
            if (this.stack.getItem() == stack.getItem())
                return false;

            boolean oreMatch = false;
            for (ItemStack oreDictMatch : oreDictMatches) {
                if (stack.isItemEqual(oreDictMatch)) {
                    oreMatch = true;
                    break;
                }
            }

            if (!oreMatch)
                return false;
        }

        return ItemStack.areItemStackTagsEqual(this.stack, stack);
    }

    public void refreshOreDictMatches () {
        if (stack.isEmpty()) {
            oreDictMatches = null;
            return;
        }

        int[] oreIDs = OreDictionary.getOreIDs(stack);
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

    public static boolean areItemsEqual (@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        return areItemsEqual(stack1, stack2, true);
    }

    public static boolean areItemsEqual (@Nonnull ItemStack stack1, @Nonnull ItemStack stack2, boolean oreDictStrictMode) {
        if (!stack1.isEmpty() && !stack2.isEmpty() && !stack1.isItemEqual(stack2)) {
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
