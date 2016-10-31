package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IPriorityGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.ISmartGroup;
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
        return group.getDrawerCount() + 1;
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        if (slot == fallbackInsertSlot())
            return null;

        if (group instanceof IPriorityGroup) {
            int[] order = ((IPriorityGroup) group).getAccessibleDrawerSlots();
            slot = (slot >= 0 && slot < order.length) ? order[slot] : -1;
        }

        IDrawer drawer = group.getDrawerIfEnabled(slot);
        if (drawer == null || drawer.isEmpty())
            return null;

        return drawer.getStoredItemCopy();
    }

    @Override
    public ItemStack insertItem (int slot, ItemStack stack, boolean simulate) {
        if (slot == fallbackInsertSlot()) {
            if (StorageDrawers.config.cache.enableItemConversion)
                return insertItemFullScan(stack, simulate);
            else
                return stack;
        }

        int orderedSlot = slot;
        int prevSlot = slot - 1;

        if (group instanceof IPriorityGroup) {
            int[] order = ((IPriorityGroup) group).getAccessibleDrawerSlots();
            orderedSlot = (slot >= 0 && slot < order.length) ? order[slot] : -1;
            prevSlot = (slot >= 1 && slot < order.length) ? order[slot - 1] : -1;
        }

        if (StorageDrawers.config.cache.enableItemConversion && orderedSlot > 0) {
            IDrawer drawer = group.getDrawerIfEnabled(orderedSlot);
            if (drawer != null && drawer.isEmpty()) {
                if (prevSlot == -1 || !group.isDrawerEnabled(prevSlot))
                    return insertItemFullScan(stack, simulate);
                else {
                    IDrawer prevDrawer = group.getDrawer(prevSlot);
                    if (!prevDrawer.isEmpty())
                        return insertItemFullScan(stack, simulate);
                }
            }
        }

        return insertItemInternal(orderedSlot, stack, simulate);
    }

    private ItemStack insertItemFullScan (ItemStack stack, boolean simulate) {
        if (group instanceof ISmartGroup) {
            for (int i : ((ISmartGroup) group).enumerateDrawersForInsertion(stack, false)) {
                stack = insertItemInternal(i, stack, simulate);
                if (stack == null)
                    break;
            }
        }
        else if (group instanceof IPriorityGroup) {
            int[] order = ((IPriorityGroup) group).getAccessibleDrawerSlots();
            for (int i = 0; i < order.length; i++) {
                stack = insertItemInternal(i, stack, simulate);
                if (stack == null)
                    break;
            }
        }
        else {
            for (int i = 0; i < group.getDrawerCount(); i++) {
                stack = insertItemInternal(i, stack, simulate);
                if (stack == null)
                    break;
            }
        }

        return stack;
    }

    private ItemStack insertItemInternal (int slot, ItemStack stack, boolean simulate) {
        IDrawer drawer = group.getDrawerIfEnabled(slot);
        if (drawer == null || !drawer.canItemBeStored(stack))
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
                drawer.setStoredItemRedir(stack, insertCount);
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
        if (slot == fallbackInsertSlot())
            return null;

        if (group instanceof IPriorityGroup) {
            int[] order = ((IPriorityGroup) group).getAccessibleDrawerSlots();
            slot = (slot >= 0 && slot < order.length) ? order[slot] : -1;
        }

        IDrawer drawer = group.getDrawerIfEnabled(slot);
        if (drawer == null || drawer.isEmpty() || drawer.getStoredItemCount() == 0)
            return null;

        ItemStack returnStack = drawer.getStoredItemCopy();
        returnStack.stackSize = Math.min(returnStack.stackSize, amount);
        returnStack.stackSize = Math.min(returnStack.stackSize, returnStack.getMaxStackSize());

        if (!simulate)
            drawer.setStoredItemCount(drawer.getStoredItemCount() - returnStack.stackSize);

        return returnStack;
    }

    private int fallbackInsertSlot () {
        return group.getDrawerCount();
    }
}
