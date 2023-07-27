package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackMatcher
{
    public static ItemStackMatcher EMPTY = new ItemStackMatcher(ItemStack.EMPTY);

    @NotNull
    protected ItemStack stack;

    public ItemStackMatcher (@NotNull ItemStack stack) {
        this.stack = stack;
    }

    public boolean matches (@NotNull ItemStack stack) {
        return areItemsEqual(this.stack, stack);
    }

    public static boolean areItemsEqual (@NotNull ItemStack stack1, @NotNull ItemStack stack2) {
        if (stack1.isEmpty() || !ItemStack.isSameItem(stack1, stack2))
            return false;

        return ItemStack.isSameItemSameTags(stack1, stack2);
    }
}
