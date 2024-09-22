package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Arrays;
import java.util.Iterator;

public class DrawerGroupStorage implements Storage<ItemVariant>
{
    IDrawerGroup group;

    public DrawerGroupStorage(IDrawerGroup group) {
        this.group = group;
    }

    @Override
    public long insert (ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        long amount = 0;

        for (int slot : group.getAccessibleDrawerSlots()) {
            Storage<ItemVariant> store = new DrawerStorage(group.getDrawer(slot));
            amount += store.insert(resource, maxAmount - amount, transaction);
            if (amount == maxAmount)
                break;
        }

        return amount;
    }

    @Override
    public long extract (ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        long amount = 0;

        for (int slot : group.getAccessibleDrawerSlots()) {
            Storage<ItemVariant> store = new DrawerStorage(group.getDrawer(slot));
            amount += store.extract(resource, maxAmount - amount, transaction);
            if (amount == maxAmount)
                break;
        }

        return amount;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator () {
        return Arrays.stream(group.getAccessibleDrawerSlots())
            .mapToObj(slot -> (StorageView<ItemVariant>)new DrawerStorage(group.getDrawer(slot)))
            .iterator();
    }
}
