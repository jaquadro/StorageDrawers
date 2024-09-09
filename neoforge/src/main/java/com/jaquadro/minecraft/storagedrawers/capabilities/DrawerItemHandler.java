package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class DrawerItemHandler implements IItemHandler
{
    private final IDrawerGroup group;

    public DrawerItemHandler (IDrawerGroup group) {
        this.group = group;
    }

    @Override
    public int getSlots () {
        if (!group.isGroupValid())
            return 0;
        return group.getDrawerCount() + 1;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot (int slot) {
        if (!group.isGroupValid())
            return ItemStack.EMPTY;
        if (slotIsVirtual(slot))
            return ItemStack.EMPTY;

        slot -= 1;
        int[] order = group.getAccessibleDrawerSlots();
        slot = (slot >= 0 && slot < order.length) ? order[slot] : -1;

        IDrawer drawer = group.getDrawer(slot);
        if (!drawer.isEnabled() || drawer.isEmpty())
            return ItemStack.EMPTY;

        ItemStack stack = drawer.getStoredItemPrototype().copy();
        stack.setCount(drawer.getStoredItemCount());

        return stack;
    }

    @Override
    @NotNull
    public ItemStack insertItem (int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!group.isGroupValid())
            return stack;

        if (slotIsVirtual(slot)) {
            // TODO: Why was ItemConversion check needed here?
            // if (CommonConfig.GENERAL.enableItemConversion.get())
                return insertItemFullScan(stack, simulate);
            //else
            //    return stack;
        }

        slot -= 1;
        int[] order = group.getAccessibleDrawerSlots();
        int orderedSlot = (slot >= 0 && slot < order.length) ? order[slot] : -1;
        int prevSlot = (slot >= 1 && slot < order.length) ? order[slot - 1] : -1;

        // TODO: Why was ItemConversion check needed here?
        if (orderedSlot > 0 /* && CommonConfig.GENERAL.enableItemConversion.get() */) {
            IDrawer drawer = group.getDrawer(orderedSlot);
            if (drawer.isEnabled() && drawer.isEmpty()) {
                IDrawer prevDrawer = group.getDrawer(prevSlot);
                if (!prevDrawer.isEnabled() || !prevDrawer.isEmpty())
                    return insertItemFullScan(stack, simulate);
            }
        }

        return insertItemInternal(orderedSlot, stack, simulate);
    }

    @NotNull
    private ItemStack insertItemFullScan (@NotNull ItemStack stack, boolean simulate) {
        IItemRepository itemRepo = group.getCapability(Capabilities.ITEM_REPOSITORY);
        if (itemRepo != null)
            return itemRepo.insertItem(stack, simulate);

        for (int i = 0; i < group.getDrawerCount(); i++) {
            stack = insertItemInternal(i, stack, simulate);
            if (stack.isEmpty())
                break;
        }

        return stack;
    }

    @NotNull
    private ItemStack insertItemInternal (int slot, @NotNull ItemStack stack, boolean simulate) {
        IDrawer drawer = group.getDrawer(slot);
        if (!drawer.canItemBeStored(stack))
            return stack;

        if (drawer.isEmpty() && !simulate)
            drawer = drawer.setStoredItem(stack);

        boolean empty = drawer.isEmpty();
        int remainder = (simulate)
            ? Math.max(stack.getCount() - (empty ? drawer.getAcceptingMaxCapacity(stack) : drawer.getAcceptingRemainingCapacity()), 0)
            : drawer.adjustStoredItemCount(stack.getCount());

        if (remainder == stack.getCount())
            return stack;
        if (remainder == 0)
            return ItemStack.EMPTY;

        return stackResult(stack, remainder);
    }

    @Override
    @NotNull
    public ItemStack extractItem (int slot, int amount, boolean simulate) {
        if (!group.isGroupValid())
            return ItemStack.EMPTY;
        if (slotIsVirtual(slot))
            return ItemStack.EMPTY;

        slot -= 1;
        int[] order = group.getAccessibleDrawerSlots();
        slot = (slot >= 0 && slot < order.length) ? order[slot] : -1;

        IDrawer drawer = group.getDrawer(slot);
        if (!drawer.isEnabled() || drawer.isEmpty() || drawer.getStoredItemCount() == 0)
            return ItemStack.EMPTY;

        @NotNull ItemStack prototype = drawer.getStoredItemPrototype();
        int remaining = (simulate)
            ? Math.max(amount - drawer.getStoredItemCount(), 0)
            : drawer.adjustStoredItemCount(-amount);

        return stackResult(prototype, amount - remaining);
    }

    @Override
    public int getSlotLimit (int slot) {
        if (!group.isGroupValid())
            return 0;
        if (slotIsVirtual(slot))
            return Integer.MAX_VALUE;

        slot -= 1;
        int[] order = group.getAccessibleDrawerSlots();
        slot = (slot >= 0 && slot < order.length) ? order[slot] : -1;

        IDrawer drawer = group.getDrawer(slot);
        if (!drawer.isEnabled())
            return 0;
        if (drawer.isEmpty())
            return drawer.getMaxCapacity(ItemStack.EMPTY);

        return drawer.getMaxCapacity();
    }

    @Override
    // TODO: Implement proper
    public boolean isItemValid (int slot, @NotNull ItemStack stack) {
        return true;
    }

    private boolean slotIsVirtual (int slot) {
        return slot == 0;
    }

    private ItemStack stackResult (@NotNull ItemStack stack, int amount) {
        ItemStack result = stack.copy();
        result.setCount(amount);
        return result;
    }
}
