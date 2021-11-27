package com.jaquadro.minecraft.storagedrawers.capabilities;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class DrawerItemHandler implements IItemHandler
{
    public static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private IDrawerGroup group;

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
    @Nonnull
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
    @Nonnull
    public ItemStack insertItem (int slot, @Nonnull ItemStack stack, boolean simulate) {
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

    @Nonnull
    private ItemStack insertItemFullScan (@Nonnull ItemStack stack, boolean simulate) {
        IItemRepository itemRepo = group.getCapability(ITEM_REPOSITORY_CAPABILITY, null).orElse(null);
        if (itemRepo != null)
            return itemRepo.insertItem(stack, simulate);

        for (int i = 0; i < group.getDrawerCount(); i++) {
            stack = insertItemInternal(i, stack, simulate);
            if (stack.isEmpty())
                break;
        }

        return stack;
    }

    @Nonnull
    private ItemStack insertItemInternal (int slot, @Nonnull ItemStack stack, boolean simulate) {
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
    @Nonnull
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

        @Nonnull ItemStack prototype = drawer.getStoredItemPrototype();
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

        IDrawer drawer = group.getDrawer(slot);
        if (!drawer.isEnabled())
            return 0;
        if (drawer.isEmpty())
            return drawer.getMaxCapacity(ItemStack.EMPTY);

        return drawer.getMaxCapacity();
    }

    @Override
    // TODO: Implement proper
    public boolean isItemValid (int slot, @Nonnull ItemStack stack) {
        return true;
    }

    private boolean slotIsVirtual (int slot) {
        return slot == 0;
    }

    private ItemStack stackResult (@Nonnull ItemStack stack, int amount) {
        ItemStack result = stack.copy();
        result.setCount(amount);
        return result;
    }
}
