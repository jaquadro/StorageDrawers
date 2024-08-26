package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.EmptyDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.storage.StorageUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DrawerItemRepository implements IItemRepository
{
    protected IDrawerGroup group;

    public DrawerItemRepository (IDrawerGroup group) {
        this.group = group;
    }

    @NotNull
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

    @NotNull
    @Override
    public ItemStack insertItem (@NotNull ItemStack stack, boolean simulate, Predicate<ItemStack> predicate) {
        int amount = stack.getCount();

        // First use strict capacity check
        for (int slot : group.getAccessibleDrawerSlots()) {
            IDrawer drawer = group.getDrawer(slot);
            if (!drawer.isEnabled())
                continue;
            if (!testPredicateInsert(drawer, stack, predicate))
                continue;

            boolean empty = drawer.isEmpty();
            if (empty && !simulate)
                drawer = drawer.setStoredItem(stack);

            int capacity = empty ? drawer.getMaxCapacity(stack) : drawer.getRemainingCapacity();
            int adjusted = Math.min(amount, capacity);
            amount = (simulate)
                ? Math.max(amount - capacity, 0)
                : (amount - adjusted) + drawer.adjustStoredItemCount(adjusted);

            if (amount == 0)
                break;
        }

        // Then relax check
        if (amount > 0) {
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
                    break;
            }
        }

        IDrawerAttributes attrs = group.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(EmptyDrawerAttributes.EMPTY);
        if (!simulate && attrs.isBalancedFill() && !attrs.isUnlimitedVending())
            StorageUtil.rebalanceDrawers(group, stack);

        return (amount == 0)
            ? ItemStack.EMPTY
            : stackResult(stack, amount);
    }

    @NotNull
    @Override
    public ItemStack extractItem (@NotNull ItemStack stack, int amount, boolean simulate, Predicate<ItemStack> predicate) {
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
                break;
        }

        IDrawerAttributes attrs = group.getCapability(CapabilityDrawerAttributes.DRAWER_ATTRIBUTES_CAPABILITY).orElse(EmptyDrawerAttributes.EMPTY);
        if (!simulate && attrs.isBalancedFill() && !attrs.isUnlimitedVending())
            StorageUtil.rebalanceDrawers(group, stack);

        return (amount == remaining)
            ? ItemStack.EMPTY
            : stackResult(stack, amount - remaining);
    }

    @Override
    public int getStoredItemCount (@NotNull ItemStack stack, Predicate<ItemStack> predicate) {
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
    public int getRemainingItemCapacity (@NotNull ItemStack stack, Predicate<ItemStack> predicate) {
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
    public int getItemCapacity (@NotNull ItemStack stack, Predicate<ItemStack> predicate) {
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

    protected boolean testPredicateInsert (IDrawer drawer, @NotNull ItemStack stack, Predicate<ItemStack> predicate) {
        if (predicate instanceof DefaultPredicate)
            return drawer.canItemBeStored(stack) || predicate.test(drawer.getStoredItemPrototype());
        else
            return drawer.canItemBeStored(stack, predicate);
    }

    protected boolean testPredicateExtract (IDrawer drawer, @NotNull ItemStack stack, Predicate<ItemStack> predicate) {
        if (predicate instanceof DefaultPredicate)
            return drawer.canItemBeExtracted(stack) || predicate.test(drawer.getStoredItemPrototype());
        else
            return drawer.canItemBeStored(stack, predicate);
    }

    protected ItemStack stackResult (@NotNull ItemStack stack, int amount) {
        ItemStack result = stack.copy();
        result.setCount(amount);
        return result;
    }
}
