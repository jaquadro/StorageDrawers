package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemStackMatcher
{
    public static ItemStackMatcher EMPTY = new ItemStackMatcher(ItemStack.EMPTY);

    @Nonnull
    protected ItemStack stack;

    public ItemStackMatcher (@Nonnull ItemStack stack) {
        this.stack = stack;
    }

    public boolean matches (@Nonnull ItemStack stack) {
        return areItemsEqual(this.stack, stack);
    }

    public static boolean areItemsEqual (@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        if (!stack1.sameItem(stack2))
            return false;

        return ItemStack.tagMatches(stack1, stack2);
    }
}
