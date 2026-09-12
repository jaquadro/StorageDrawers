package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.capabilities.PlatformCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

public class ItemTransfer
{
    public static boolean addItemsFromDrawers (IDrawerGroup group, int itemCount, BlockEntity target, Direction dir) {
        if (target == null)
            return false;

        Optional<IItemHandler> cap = target.getCapability(PlatformCapabilities.NATIVE_FORGE_ITEM_HANDLER, dir).resolve();
        if (cap.isPresent()) {
            IItemHandler handler = cap.get();
            for (int i = 0; i < group.getDrawerCount(); i++) {
                IDrawer drawer = group.getDrawer(i);
                if (addItemsFromDrawer(drawer, itemCount, handler))
                    return true;
            }
        }

        return false;
    }

    private static boolean addItemsFromDrawer (IDrawer drawer, int itemCount, IItemHandler handler) {
        if (!drawer.isEnabled() || drawer.isEmpty() || drawer.getAttributes().isSuspended())
            return false;

        int addCount = Math.min(drawer.getStoredItemCount(), drawer.getStoredItemPrototype().getMaxStackSize());
        addCount = Math.min(addCount, itemCount);

        ItemStack stack = drawer.getStoredItemPrototype().copyWithCount(addCount);
        int remaining = addCount;

        int slotCount = handler.getSlots();
        for (int i = 0; i < slotCount; i++) {
            if (!handler.isItemValid(i, stack))
                continue;

            try {
                ItemStack result = handler.insertItem(i, stack, false);
                remaining = result.getCount();
            } catch (Exception e) {
                return false;
            }

            if (remaining == 0)
                break;
        }

        if (remaining < addCount) {
            drawer.setStoredItemCount(drawer.getStoredItemCount() - (addCount - remaining));
            return true;
        }

        return false;
    }

    public static boolean takeItemsIntoDrawers (IDrawerGroup group, int itemCount, BlockEntity target, Direction dir) {
        if (target == null)
            return false;

        Optional<IItemHandler> cap = target.getCapability(PlatformCapabilities.NATIVE_FORGE_ITEM_HANDLER, dir).resolve();
        if (cap.isPresent()) {
            IItemHandler handler = cap.get();
            for (int i = 0; i < group.getDrawerCount(); i++) {
                IDrawer drawer = group.getDrawer(i);
                if (takeItemsIntoDrawer(drawer, itemCount, handler))
                    return true;
            }
        }

        return false;
    }

    private static boolean takeItemsIntoDrawer (IDrawer drawer, int itemCount, IItemHandler handler) {
        if (!drawer.isEnabled() || drawer.isEmpty() || drawer.getAttributes().isSuspended())
            return false;

        int remaining = itemCount;
        int slotCount = handler.getSlots();
        for (int i = 0; i < slotCount; i++) {
            try {
                ItemStack existingItem = handler.getStackInSlot(i);
                if (existingItem.isEmpty())
                    continue;

                if (!drawer.canItemBeStored(existingItem))
                    continue;

                int pullCount = Math.min(drawer.getAcceptingRemainingCapacity(), drawer.getStoredItemPrototype().getMaxStackSize());
                pullCount = Math.min(pullCount, remaining);

                ItemStack result = handler.extractItem(i, pullCount, false);
                if (drawer.isEmpty())
                    drawer.setStoredItem(result, result.getCount());
                else
                    drawer.setStoredItemCount(drawer.getStoredItemCount() + result.getCount());

                remaining -= result.getCount();
            } catch (Exception e) {
                return false;
            }

            if (remaining == 0)
                break;
        }

        return remaining < itemCount;
    }
}
