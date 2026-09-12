package com.jaquadro.minecraft.storagedrawers.service;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PlatformItemTransfer implements ItemTransferService
{
    @Override
    public boolean pushItems (IDrawerGroup group, int itemCount, BlockEntity target, Direction side) {
        Storage<ItemVariant> storage = findStorage(target, side);
        if (storage == null || !storage.supportsInsertion())
            return false;

        for (int i = 0; i < group.getDrawerCount(); i++) {
            if (pushItemsFromDrawer(group.getDrawer(i), itemCount, storage))
                return true;
        }

        return false;
    }

    private boolean pushItemsFromDrawer (IDrawer drawer, int itemCount, Storage<ItemVariant> storage) {
        if (!drawer.isEnabled() || drawer.isEmpty() || drawer.getAttributes().isSuspended())
            return false;

        int pushCount = Math.min(drawer.getStoredItemCount(), drawer.getStoredItemPrototype().getMaxStackSize());
        pushCount = Math.min(pushCount, itemCount);
        if (pushCount <= 0)
            return false;

        ItemVariant variant = ItemVariant.of(drawer.getStoredItemPrototype());

        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(variant, pushCount, transaction);
            if (inserted > 0) {
                transaction.commit();
                drawer.setStoredItemCount(drawer.getStoredItemCount() - (int) inserted);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean pullItems (IDrawerGroup group, int itemCount, BlockEntity target, Direction side) {
        Storage<ItemVariant> storage = findStorage(target, side);
        if (storage == null || !storage.supportsExtraction())
            return false;

        for (int i = 0; i < group.getDrawerCount(); i++) {
            if (pullItemsIntoDrawer(group.getDrawer(i), itemCount, storage))
                return true;
        }

        return false;
    }

    private boolean pullItemsIntoDrawer (IDrawer drawer, int itemCount, Storage<ItemVariant> storage) {
        if (!drawer.isEnabled() || drawer.isEmpty() || drawer.getAttributes().isSuspended())
            return false;

        int pullCount = Math.min(drawer.getAcceptingRemainingCapacity(), drawer.getStoredItemPrototype().getMaxStackSize());
        pullCount = Math.min(pullCount, itemCount);
        if (pullCount <= 0)
            return false;

        ItemVariant variant = ItemVariant.of(drawer.getStoredItemPrototype());

        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = storage.extract(variant, pullCount, transaction);
            if (extracted > 0) {
                transaction.commit();
                drawer.setStoredItemCount(drawer.getStoredItemCount() + (int) extracted);
                return true;
            }
        }

        return false;
    }

    private static Storage<ItemVariant> findStorage (BlockEntity target, Direction side) {
        Level level = target.getLevel();
        if (level == null)
            return null;

        return ItemStorage.SIDED.find(level, target.getBlockPos(), side);
    }
}
