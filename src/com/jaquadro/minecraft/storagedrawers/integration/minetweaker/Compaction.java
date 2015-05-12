package com.jaquadro.minecraft.storagedrawers.integration.minetweaker;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.storagedrawers.Compaction")
public class Compaction
{
    @ZenMethod
    public static void add (IItemStack upper, IItemStack lower, int conversion) {
        ItemStack upperStack = MineTweakerMC.getItemStack(upper);
        ItemStack lowerStack = MineTweakerMC.getItemStack(lower);

        if (upperStack == null || lowerStack == null)
            MineTweakerAPI.logError("Tried to add compacting tier with invalid item stack.");
        else if (conversion != 4 && conversion != 9)
            MineTweakerAPI.logError("Tried to add compacting tier with invalid conversion value (must be 4 or 9).");
        else
            MineTweakerAPI.apply(new AddRecordAction(upperStack, lowerStack, conversion));
    }

    private static class AddRecordAction implements IUndoableAction
    {
        ItemStack upper;
        ItemStack lower;
        int conversionRate;
        boolean added;

        public AddRecordAction (ItemStack upper, ItemStack lower, int conversionRate) {
            this.upper = upper;
            this.lower = lower;
            this.conversionRate = conversionRate;
        }

        @Override
        public void apply () {
            added = StorageDrawers.compRegistry.register(upper, lower, conversionRate);
        }

        @Override
        public boolean canUndo () {
            return true;
        }

        @Override
        public void undo () {
            if (added)
                StorageDrawers.compRegistry.unregisterUpperTarget(upper);
        }

        @Override
        public String describe () {
            if (added)
                return "Adding compacting tier: 1 '" + upper.getDisplayName() + "' = " + conversionRate + " '" + lower.getDisplayName() + "'.";
            else
                return "Failed to add compacting tier.";
        }

        @Override
        public String describeUndo () {
            if (added)
                return "Removing previously added compacting tier: 1 '" + upper.getDisplayName() + "' = " + conversionRate + " '" + lower.getDisplayName() + "'.";
            else
                return "";
        }

        @Override
        public Object getOverrideKey () {
            return null;
        }
    }
}
