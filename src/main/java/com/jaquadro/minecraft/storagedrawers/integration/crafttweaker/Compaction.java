package com.jaquadro.minecraft.storagedrawers.integration.crafttweaker;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
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
    public static void remove (@Optional IItemStack upper, @Optional IItemStack lower) {
        if (upper == null && lower == null) {
            CraftTweakerAPI.logError("Tried to remove compacting tier(s) with invalid item stacks.");
            return;
        }

        ItemStack upperStack = CraftTweakerMC.getItemStack(upper);
        ItemStack lowerStack = CraftTweakerMC.getItemStack(lower);

        if (upperStack == null && lowerStack == null)
            CraftTweakerAPI.logError("Tried to remove compacting tier(s) with invalid item stacks.");
        else
            CraftTweakerAPI.apply(new RemoveRecordAction(upperStack, lowerStack));
    }

    private static class AddRecordAction implements IAction
    {
        ItemStack upper;
        ItemStack lower;
        int conversionRate;

        public AddRecordAction (ItemStack upper, ItemStack lower, int conversionRate) {
            this.upper = upper;
            this.lower = lower;
            this.conversionRate = conversionRate;
        }

        @Override
        public void apply () {
            boolean added = StorageDrawers.compRegistry.register(upper, lower, conversionRate);
            if (!added)
                CraftTweakerAPI.logError("Failed to add compacting recipe with upper item '" + upper.getDisplayName() + "'.");
        }

        @Override
        public String describe () {
            return "Adding compacting tier: 1 '" + upper.getDisplayName() + "' = " + conversionRate + " '" + lower.getDisplayName() + "'.";
        }
    }

    private static class RemoveRecordAction implements IAction
    {
        ItemStack upper;
        ItemStack lower;

        public RemoveRecordAction (ItemStack upper, ItemStack lower) {
            this.upper = upper;
            this.lower = lower;
        }

        @Override
        public void apply () {
            if (upper != ItemStack.EMPTY)
                StorageDrawers.compRegistry.unregisterUpperTarget(upper);
            if (lower != ItemStack.EMPTY)
                StorageDrawers.compRegistry.unregisterLowerTarget(lower);
        }

        @Override
        public String describe () {
            return "Removing existing compacting tier(s) with upper item '"
                        + (upper != ItemStack.EMPTY ? upper.getDisplayName() : "null") +
                    "' and/or lower item '"
                        + (lower != ItemStack.EMPTY ? lower.getDisplayName() : "null") +
                    "'.";
        }
    }
}
