package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository.DefaultPredicate;
import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository.ItemRecord;

public class DrawerItemRepository implements IItemRepository
{
    protected IDrawerGroup group;

    public DrawerItemRepository (IDrawerGroup group) {
        this.group = group;
    }

    @Nonnull
    @Override
    public NonNullList<ItemRecord> getAllItems () {
        NonNullList<ItemRecord> records = NonNullList.create();
        if (group == null)
            return records;

        for (int slot : group.getAccessibleDrawerSlots()) {
            IDrawer drawer = group.getDrawer(slot);
            if (drawer.isEmpty())
                continue;

            ItemStack stack = drawer.getStoredItemPrototype();
            records.add(new ItemRecord(stack, drawer.getStoredItemCount()));
        }

        return records;
    }

    @Nonnull
    @Override
    public ItemStack insertItem (@Nonnull ItemStack stack, boolean simulate, Predicate<ItemStack> predicate) {
        int amount = stack.getCount();

        for (int slot : group.getAccessibleDrawerSlots()) {
            IDrawer drawer = group.getDrawer(slot);
            if (!drawer.isEnabled())
                continue;
            if (!testPredicateInsert(drawer, stack, predicate))
                continue;

            boolean empty = drawer.isEmpty();
            if (empty && !simulate)
                drawer = drawer.setStoredItem(stack);

            amount = (simulate)
                ? Math.max(amount - (empty ? drawer.getAcceptingMaxCapacity(stack) : drawer.getAcceptingRemainingCapacity()), 0)
                : drawer.adjustStoredItemCount(amount);

            if (amount == 0)
                return ItemStack.EMPTY;
        }

        return stackResult(stack, amount);
    }

    @Nonnull
    @Override
    public ItemStack extractItem (@Nonnull ItemStack stack, int amount, boolean simulate, Predicate<ItemStack> predicate) {
        int remaining = amount;

        for (int slot : group.getAccessibleDrawerSlots()) {
            IDrawer drawer = group.getDrawer(slot);
            if (!drawer.isEnabled())
                continue;
            if (!testPredicateExtract(drawer, stack, predicate))
                continue;

            remaining = (simulate)
                ? Math.max(remaining - drawer.getStoredItemCount(), 0)
                : drawer.adjustStoredItemCount(-remaining);

            if (remaining == 0)
                return stackResult(stack, amount);
        }

        return (amount == remaining)
            ? ItemStack.EMPTY
            : stackResult(stack, amount - remaining);
    }

    @Override
    public int getStoredItemCount (@Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
        long count = 0;
        for (int slot : group.getAccessibleDrawerSlots()) {
            IDrawer drawer = group.getDrawer(slot);
            if (!testPredicateInsert(drawer, stack, predicate))
                continue;

            count += drawer.getStoredItemCount();
            if (count >= Integer.MAX_VALUE)
                return Integer.MAX_VALUE;
        }

        return (int)count;
    }

    @Override
    public int getRemainingItemCapacity (@Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
        long remainder = 0;
        for (int slot : group.getAccessibleDrawerSlots()) {
            IDrawer drawer = group.getDrawer(slot);
            if (!testPredicateInsert(drawer, stack, predicate))
                continue;

            remainder += drawer.getRemainingCapacity();
            if (remainder >= Integer.MAX_VALUE)
                return Integer.MAX_VALUE;
        }

        return (int)remainder;
    }

    @Override
    public int getItemCapacity (@Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
        long capacity = 0;
        for (int slot : group.getAccessibleDrawerSlots()) {
            IDrawer drawer = group.getDrawer(slot);
            if (!testPredicateInsert(drawer, stack, predicate))
                continue;

            capacity += drawer.getMaxCapacity();
            if (capacity >= Integer.MAX_VALUE)
                return Integer.MAX_VALUE;
        }

        return (int)capacity;
    }

    protected boolean testPredicateInsert (IDrawer drawer, @Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
        if (predicate instanceof DefaultPredicate) {
            if (!drawer.canItemBeStored(stack) && !predicate.test(drawer.getStoredItemPrototype()))
                return false;
        }
        else if (!drawer.canItemBeStored(stack, predicate))
            return false;

        return true;
    }

    protected boolean testPredicateExtract (IDrawer drawer, @Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
        if (predicate instanceof DefaultPredicate) {
            if (!drawer.canItemBeExtracted(stack) && !predicate.test(drawer.getStoredItemPrototype()))
                return false;
        }
        else if (!drawer.canItemBeStored(stack, predicate))
            return false;

        return true;
    }

    protected ItemStack stackResult (@Nonnull ItemStack stack, int amount) {
        ItemStack result = stack.copy();
        result.setCount(amount);
        return result;
    }
}
