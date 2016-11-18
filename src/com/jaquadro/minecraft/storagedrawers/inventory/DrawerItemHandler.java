package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IPriorityGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.ISmartGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class DrawerItemHandler implements IItemHandler
{
    private IDrawerGroup group;

    public DrawerItemHandler (IDrawerGroup group) {
        this.group = group;
    }

    @Override
    public int getSlots () {
        return group.getDrawerCount() + 2;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot (int slot) {
        if (slotIsVirtual(slot))
            return ItemStack.field_190927_a;

        slot -= 1;
        if (group instanceof IPriorityGroup) {
            int[] order = ((IPriorityGroup) group).getAccessibleDrawerSlots();
            slot = (slot >= 0 && slot < order.length) ? order[slot] : -1;
        }

        IDrawer drawer = group.getDrawerIfEnabled(slot);
        if (drawer == null || drawer.isEmpty())
            return ItemStack.field_190927_a;

        return drawer.getStoredItemCopy();
    }

    @Override
    @Nonnull
    public ItemStack insertItem (int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slotIsVirtual(slot)) {
            if (StorageDrawers.config.cache.enableItemConversion)
                return insertItemFullScan(stack, simulate);
            else
                return stack;
        }

        slot -= 1;
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

    @Nonnull
    private ItemStack insertItemFullScan (@Nonnull ItemStack stack, boolean simulate) {
        if (group instanceof ISmartGroup) {
            for (int i : ((ISmartGroup) group).enumerateDrawersForInsertion(stack, false)) {
                stack = insertItemInternal(i, stack, simulate);
                if (!stack.func_190926_b())
                    break;
            }
        }
        else if (group instanceof IPriorityGroup) {
            int[] order = ((IPriorityGroup) group).getAccessibleDrawerSlots();
            for (int i = 0; i < order.length; i++) {
                stack = insertItemInternal(i, stack, simulate);
                if (!stack.func_190926_b())
                    break;
            }
        }
        else {
            for (int i = 0; i < group.getDrawerCount(); i++) {
                stack = insertItemInternal(i, stack, simulate);
                if (!stack.func_190926_b())
                    break;
            }
        }

        return stack;
    }

    @Nonnull
    private ItemStack insertItemInternal (int slot, @Nonnull ItemStack stack, boolean simulate) {
        IDrawer drawer = group.getDrawerIfEnabled(slot);
        if (drawer == null || !drawer.canItemBeStored(stack))
            return stack;

        int availableCount = drawer.isEmpty() ? drawer.getMaxCapacity(stack) : drawer.getRemainingCapacity();
        if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid())
            availableCount = Integer.MAX_VALUE;

        int stackSize = stack.func_190916_E();
        int insertCount = Math.min(stackSize, availableCount);
        int remainder = stackSize - insertCount;

        if (remainder == stackSize)
            return stack;

        if (!simulate) {
            if (drawer.isEmpty())
                drawer.setStoredItemRedir(stack, insertCount);
            else
                drawer.setStoredItemCount(drawer.getStoredItemCount() + insertCount);
        }

        if (remainder == 0)
            return ItemStack.field_190927_a;

        ItemStack returnStack = stack.copy();
        returnStack.func_190920_e(remainder);

        return returnStack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem (int slot, int amount, boolean simulate) {
        if (slotIsVirtual(slot))
            return ItemStack.field_190927_a;

        slot -= 1;
        if (group instanceof IPriorityGroup) {
            int[] order = ((IPriorityGroup) group).getAccessibleDrawerSlots();
            slot = (slot >= 0 && slot < order.length) ? order[slot] : -1;
        }

        IDrawer drawer = group.getDrawerIfEnabled(slot);
        if (drawer == null || drawer.isEmpty() || drawer.getStoredItemCount() == 0)
            return ItemStack.field_190927_a;

        ItemStack returnStack = drawer.getStoredItemCopy();

        int stackSize = Math.min(Math.min(returnStack.func_190916_E(), returnStack.getMaxStackSize()), amount);
        returnStack.func_190920_e(stackSize);

        if (!simulate)
            drawer.setStoredItemCount(drawer.getStoredItemCount() - stackSize);

        return returnStack;
    }

    private boolean slotIsVirtual (int slot) {
        return slot == 0 || slot == group.getDrawerCount() + 1;
    }
}
