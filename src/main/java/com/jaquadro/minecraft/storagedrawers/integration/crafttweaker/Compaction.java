package com.jaquadro.minecraft.storagedrawers.integration.crafttweaker;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.storagedrawers.Compaction")
public class Compaction
{
    @ZenMethod
    public static void add (IItemStack upper, IItemStack lower, int conversion) {
        if (upper == null || lower == null) {
            CraftTweakerAPI.logError("Tried to add compacting tier with invalid item stack.");
            return;
        }

        ItemStack upperStack = (ItemStack)upper.getInternal();
        ItemStack lowerStack = (ItemStack)lower.getInternal();

        if (upperStack == null || lowerStack == null)
            CraftTweakerAPI.logError("Tried to add compacting tier with invalid item stack.");
        else
            CraftTweakerAPI.apply(new AddRecordAction(upperStack, lowerStack, conversion));
    }

    @ZenMethod
    public static void remove (IItemStack upper) {
        if (upper == null) {
            CraftTweakerAPI.logError("Tried to remove compacting tier with invalid item stack.");
            return;
        }

        ItemStack upperStack = (ItemStack)upper.getInternal();

        if (upperStack == null)
            CraftTweakerAPI.logError("Tried to remove compacting tier with invalid item stack.");
        else
            CraftTweakerAPI.apply(new RemoveRecordAction(upperStack));
    }

    private static class AddRecordAction implements IAction
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
        public String describe () {
            if (added)
                return "Adding compacting tier: 1 '" + upper.getDisplayName() + "' = " + conversionRate + " '" + lower.getDisplayName() + "'.";
            else
                return "Failed to add compacting tier.";
        }
    }

    private static class RemoveRecordAction implements IAction
    {
        ItemStack upper;
        boolean removed;

        public RemoveRecordAction (ItemStack upper) {
            this.upper = upper;
        }

        @Override
        public void apply () {
            removed = StorageDrawers.compRegistry.unregisterUpperTarget(upper);
        }

        @Override
        public String describe () {
            if (removed)
                return "Removing existing compacting tier with upper item '" + upper.getDisplayName() + ".";
            else
                return "";
        }
    }
}
