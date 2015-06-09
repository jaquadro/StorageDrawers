package com.jaquadro.minecraft.storagedrawers.inventory;

import net.minecraft.item.ItemStack;

public abstract class InventoryStack
{
    private ItemStack nativeStack;
    private ItemStack inStack;
    private ItemStack outStack;

    private int nativeCount;
    private int inCount;
    private int outCount;

    public void init () {
        reset();
    }

    public void reset () {
        nativeStack = getNewItemStack();
        inStack = getNewItemStack();
        outStack = getNewItemStack();

        nativeCount = 0;
        inCount = 0;
        outCount = 0;

        refresh();
    }

    public ItemStack getNativeStack () {
        return nativeStack;
    }

    public ItemStack getInStack () {
        if (inStack == null || inStack.stackSize == 0)
            return null;

        return inStack;
    }

    public ItemStack getOutStack () {
        return outStack;
    }

    public void setInStack (ItemStack stack) {
        if (stack != null) {
            if (inStack == null)
                applyDiff(stack.stackSize);
            else
                applyDiff(stack.stackSize - inCount);
        }

        inStack = stack;
        syncInStack();
        syncOutStack();
    }

    private void syncInStack () {
        if (inStack == null) {
            inStack = getNewItemStack();
            inCount = 0;
        }

        if (inStack != null) {
            int itemStackLimit = getItemStackSize();
            int itemCount = getItemCount();
            int remainingLimit = getItemCapacity() - itemCount;

            inCount = itemStackLimit - Math.min(itemStackLimit, remainingLimit);
            inStack.stackSize = inCount;
        }
    }

    public void setOutStack (ItemStack stack) {
        if (outStack != null) {
            if (stack == null)
                applyDiff(0 - outCount);
            else
                applyDiff(0 - outCount + stack.stackSize);
        }

        outStack = stack;
        syncOutStack();
    }

    private void syncOutStack () {
        if (outStack == null) {
            outStack = getNewItemStack();
            outCount = 0;
        }

        if (outStack != null) {
            int itemStackLimit = getItemStackSize();
            int itemCount = getItemCount();

            outCount = Math.min(itemStackLimit, itemCount);
            outStack.stackSize = outCount;
        }
    }

    protected abstract ItemStack getNewItemStack ();
    protected abstract int getItemStackSize ();
    protected abstract int getItemCount ();
    protected abstract int getItemCapacity ();

    public void markDirty () {
        applyDiff(getDiff());
        refresh();
    }

    public boolean markDirtyIfNeeded () {
        int diff = getDiff();
        if (diff != 0) {
            applyDiff(diff);
            refresh();
            return true;
        }

        return false;
    }

    public int getDiff () {
        if (nativeStack == null)
            return 0;

        int diffNative = nativeStack.stackSize - nativeCount;
        int diffIn = inStack.stackSize - inCount;
        int diffOut = outStack.stackSize - outCount;

        return diffNative + diffIn + diffOut;
    }

    protected abstract void applyDiff (int diff);

    protected void refresh () {
        int itemStackLimit = getItemStackSize();
        int itemCount = getItemCount();
        int remainingLimit = getItemCapacity() - itemCount;

        if (nativeStack != null) {
            nativeCount = itemCount;
            nativeStack.stackSize = nativeCount;
        }

        if (inStack != null) {
            inCount = itemStackLimit - Math.min(itemStackLimit, remainingLimit);
            inStack.stackSize = inCount;
        }

        if (outStack != null) {
            outCount = Math.min(itemStackLimit, itemCount);
            outStack.stackSize = outCount;
        }
    }
}
