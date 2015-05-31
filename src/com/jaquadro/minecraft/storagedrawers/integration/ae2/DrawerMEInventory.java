package com.jaquadro.minecraft.storagedrawers.integration.ae2;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.item.ItemStack;

public class DrawerMEInventory //implements IMEInventory<IAEItemStack>
{
    /*final IDrawerGroup group;

    public DrawerMEInventory (IDrawerGroup drawerGroup) {
        group = drawerGroup;
    }

    @Override
    public IAEItemStack injectItems (IAEItemStack input, Actionable type, BaseActionSource src) {
        long itemsLeft = input.getStackSize();
        for (int i = 0, n = group.getDrawerCount(); i < n; i++) {
            if (!group.isDrawerEnabled(i))
                continue;

            IDrawer drawer = group.getDrawer(i);
            ItemStack itemProto = drawer.getStoredItemPrototype();
            if (itemProto != null) {
                if (drawer.canItemBeStored(input.getItemStack())) {
                    itemsLeft = injectItemsIntoDrawer(drawer, itemsLeft, type);
                    if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid())
                        itemsLeft = 0;
                    if (itemsLeft == 0)
                        return null;
                }
            }
        }

        for (int i = 0, n = group.getDrawerCount(); i < n; i++) {
            if (!group.isDrawerEnabled(i))
                continue;

            if (group instanceof ILockable && ((ILockable) group).isLocked(LockAttribute.LOCK_EMPTY))
                continue;

            IDrawer drawer = group.getDrawer(i);
            ItemStack itemProto = drawer.getStoredItemPrototype();

            if (itemProto == null && drawer instanceof ILockable && ((ILockable) drawer).isLocked(LockAttribute.LOCK_EMPTY))
                continue;

            if (itemProto == null) {
                itemProto = input.getItemStack();
                if (drawer.canItemBeStored(itemProto)) {
                    drawer.setStoredItem(itemProto, 0);
                    itemsLeft = injectItemsIntoDrawer(drawer, itemsLeft, type);
                    if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid())
                        itemsLeft = 0;
                    if (itemsLeft == 0)
                        return null;
                }
            }
        }

        if (itemsLeft > 0) {
            IAEItemStack overflow = AEApi.instance().storage().createItemStack(input.getItemStack());
            overflow.setStackSize(itemsLeft);
            return overflow;
        }

        return input;
    }

    private long injectItemsIntoDrawer (IDrawer drawer, long itemCount, Actionable type) {
        int capacity = drawer.getMaxCapacity();
        int storedItems = drawer.getStoredItemCount();
        if (capacity == storedItems)
            return itemCount;

        storedItems += itemCount;
        if (storedItems > capacity) {
            if (type == Actionable.MODULATE)
                drawer.setStoredItemCount(capacity);

            return storedItems - capacity;
        }
        else {
            if (type == Actionable.MODULATE)
                drawer.setStoredItemCount(storedItems);

            return 0;
        }
    }

    @Override
    public IAEItemStack extractItems (IAEItemStack request, Actionable mode, BaseActionSource src) {
        long itemsLeft = request.getStackSize();
        for (int i = 0, n = group.getDrawerCount(); i < n; i++) {
            if (!group.isDrawerEnabled(i))
                continue;

            IDrawer drawer = group.getDrawer(i);
            if (drawer.canItemBeExtracted(request.getItemStack())) {
                int itemCount = drawer.getStoredItemCount();
                if (itemsLeft > itemCount) {
                    if (mode == Actionable.MODULATE)
                        drawer.setStoredItemCount(0);
                    itemsLeft -= itemCount;
                }
                else {
                    if (mode == Actionable.MODULATE)
                        drawer.setStoredItemCount(itemCount - (int)itemsLeft);
                    itemsLeft = 0;
                    break;
                }
            }
        }

        if (itemsLeft < request.getStackSize()) {
            ItemStack fulfillment = request.getItemStack().copy();
            fulfillment.stackSize -= itemsLeft;
            return AEApi.instance().storage().createItemStack(fulfillment);
        }

        return null;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems (IItemList<IAEItemStack> out) {
        for (int i = 0, n = group.getDrawerCount(); i < n; i++) {
            if (!group.isDrawerEnabled(i))
                continue;

            IDrawer drawer = group.getDrawer(i);
            if (!drawer.isEmpty())
                out.add(AEApi.instance().storage().createItemStack(drawer.getStoredItemCopy()));
        }

        return out;
    }

    @Override
    public StorageChannel getChannel () {
        return StorageChannel.ITEMS;
    }*/
}
