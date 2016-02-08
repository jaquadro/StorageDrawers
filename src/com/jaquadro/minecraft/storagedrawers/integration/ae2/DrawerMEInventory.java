/*package com.jaquadro.minecraft.storagedrawers.integration.ae2;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ILockable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.item.ItemStack;

public class DrawerMEInventory implements IMEInventory<IAEItemStack>
{
    final IDrawerGroup group;

    public DrawerMEInventory (IDrawerGroup drawerGroup) {
        group = drawerGroup;
    }

    @Override
    public IAEItemStack injectItems (IAEItemStack input, Actionable type, BaseActionSource src) {
        long itemsLeft = input.getStackSize();

        if (group instanceof ISmartGroup) {
            for (int slot : ((ISmartGroup) group).enumerateDrawersForInsertion(input.getItemStack(), false)) {
                IDrawer drawer = group.getDrawer(slot);
                ItemStack itemProto = drawer.getStoredItemPrototype();
                if (itemProto == null)
                    drawer.setStoredItem(input.getItemStack(), 0);

                itemsLeft = injectItemsIntoDrawer(drawer, itemsLeft, type);

                if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid())
                    itemsLeft = 0;
                if (itemsLeft == 0)
                    return null;
            }
        }
        else {
            int[] order = null;
            if (group instanceof IPriorityGroup)
                order = ((IPriorityGroup) group).getAccessibleDrawerSlots();


            for (int i = 0, n = group.getDrawerCount(); i < n; i++) {
                int slot = (order != null) ? order[i] : i;
                if (!group.isDrawerEnabled(slot))
                    continue;

                IDrawer drawer = group.getDrawer(slot);
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
                int slot = (order != null) ? order[i] : i;
                if (!group.isDrawerEnabled(slot))
                    continue;

                if (group instanceof ILockable && ((ILockable) group).isLocked(LockAttribute.LOCK_EMPTY))
                    continue;

                IDrawer drawer = group.getDrawer(slot);
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

        int storableItems = capacity - storedItems;
        if (drawer instanceof IFractionalDrawer) {
            IFractionalDrawer fracDrawer = (IFractionalDrawer)drawer;
            if (!fracDrawer.isSmallestUnit() && fracDrawer.getStoredItemRemainder() > 0)
                storableItems--;
        }

        if (storableItems == 0)
            return itemCount;

        long remainder = Math.max(itemCount - storableItems, 0);
        storedItems += Math.min(itemCount, storableItems);

        if (type == Actionable.MODULATE)
            drawer.setStoredItemCount(storedItems);

        return remainder;
    }

    @Override
    public IAEItemStack extractItems (IAEItemStack request, Actionable mode, BaseActionSource src) {
        long itemsLeft = request.getStackSize();
        if (group instanceof ISmartGroup) {
            for (int slot : ((ISmartGroup) group).enumerateDrawersForExtraction(request.getItemStack(), true)) {
                if (itemsLeft == 0)
                    break;

                IDrawer drawer = group.getDrawer(slot);
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
        else {
            int[] order = null;
            if (group instanceof IPriorityGroup)
                order = ((IPriorityGroup) group).getAccessibleDrawerSlots();


            for (int i = 0, n = group.getDrawerCount(); i < n; i++) {
                int slot = (order != null) ? order[i] : i;
                if (!group.isDrawerEnabled(slot))
                    continue;

                IDrawer drawer = group.getDrawer(slot);
                if (drawer.canItemBeExtracted(request.getItemStack())) {
                    int itemCount = drawer.getStoredItemCount();
                    if (itemsLeft > itemCount) {
                        if (mode == Actionable.MODULATE)
                            drawer.setStoredItemCount(0);
                        itemsLeft -= itemCount;
                    } else {
                        if (mode == Actionable.MODULATE)
                            drawer.setStoredItemCount(itemCount - (int) itemsLeft);
                        itemsLeft = 0;
                        break;
                    }
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
        int[] order = null;
        if (group instanceof IPriorityGroup)
            order = ((IPriorityGroup) group).getAccessibleDrawerSlots();

        for (int i = 0, n = group.getDrawerCount(); i < n; i++) {
            int slot = (order != null) ? order[i] : i;
            if (!group.isDrawerEnabled(slot))
                continue;

            IDrawer drawer = group.getDrawer(slot);
            if (!drawer.isEmpty())
                out.add(AEApi.instance().storage().createItemStack(drawer.getStoredItemCopy()));
        }

        return out;
    }

    @Override
    public StorageChannel getChannel () {
        return StorageChannel.ITEMS;
    }
}
*/