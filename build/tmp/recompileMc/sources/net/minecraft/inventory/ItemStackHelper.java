package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public class ItemStackHelper
{
    @Nullable
    public static ItemStack getAndSplit(ItemStack[] stacks, int index, int amount)
    {
        if (index >= 0 && index < stacks.length && stacks[index] != null && amount > 0)
        {
            ItemStack itemstack = stacks[index].splitStack(amount);

            if (stacks[index].stackSize == 0)
            {
                stacks[index] = null;
            }

            return itemstack;
        }
        else
        {
            return null;
        }
    }

    @Nullable
    public static ItemStack getAndRemove(ItemStack[] stacks, int index)
    {
        if (index >= 0 && index < stacks.length)
        {
            ItemStack itemstack = stacks[index];
            stacks[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }
}