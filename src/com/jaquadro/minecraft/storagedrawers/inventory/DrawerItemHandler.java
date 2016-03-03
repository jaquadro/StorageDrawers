package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IPriorityGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class DrawerItemHandler implements IItemHandler
{
    private IDrawerGroup group;

    public DrawerItemHandler (IDrawerGroup group) {
        this.group = group;
    }

    @Override
    public int getSlots () {
        return group.getDrawerCount();
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        if (group instanceof IPriorityGroup) {
            int[] order = ((IPriorityGroup) group).getAccessibleDrawerSlots();
            slot = (slot >= 0 && slot < order.length) ? order[slot] : -1;
        }

        if (!group.isDrawerEnabled(slot))
            return null;

        IDrawer drawer = group.getDrawer(slot);
        if (drawer.isEmpty())
            return null;

        return drawer.getStoredItemCopy();
    }

    @Override
    public ItemStack insertItem (int slot, ItemStack stack, boolean simulate) {
        if (group instanceof IPriorityGroup) {
            int[] order = ((IPriorityGroup) group).getAccessibleDrawerSlots();
            slot = (slot >= 0 && slot < order.length) ? order[slot] : -1;
        }

        if (!group.isDrawerEnabled(slot))
            return stack;

        IDrawer drawer = group.getDrawer(slot);
        if (!drawer.canItemBeStored(stack))
            return stack;

        int availableCount = drawer.isEmpty() ? drawer.getMaxCapacity(stack) : drawer.getRemainingCapacity();
        if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid())
            availableCount = Integer.MAX_VALUE;

        int insertCount = Math.min(stack.stackSize, availableCount);
        int remainder = stack.stackSize - insertCount;

        if (remainder == stack.stackSize)
            return stack;

        if (!simulate) {
            if (drawer.isEmpty())
                drawer.setStoredItem(stack, insertCount);
            else
                drawer.setStoredItemCount(drawer.getStoredItemCount() + insertCount);
        }

        if (remainder == 0)
            return null;

        ItemStack returnStack = stack.copy();
        returnStack.stackSize = remainder;

        return returnStack;
    }

    @Override
    public ItemStack extractItem (int slot, int amount, boolean simulate) {
        if (group instanceof IPriorityGroup) {
            int[] order = ((IPriorityGroup) group).getAccessibleDrawerSlots();
            slot = (slot >= 0 && slot < order.length) ? order[slot] : -1;
        }

        if (!group.isDrawerEnabled(slot))
            return null;

        IDrawer drawer = group.getDrawer(slot);
        if (drawer.isEmpty() || drawer.getStoredItemCount() == 0)
            return null;

        ItemStack returnStack = drawer.getStoredItemCopy();
        returnStack.stackSize = Math.min(returnStack.stackSize, amount);
        returnStack.stackSize = Math.min(returnStack.stackSize, returnStack.getMaxStackSize());

        if (!simulate)
            drawer.setStoredItemCount(drawer.getStoredItemCount() - returnStack.stackSize);

        return returnStack;
    }
}
