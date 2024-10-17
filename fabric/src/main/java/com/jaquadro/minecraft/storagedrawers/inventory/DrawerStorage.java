package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.impl.transfer.TransferApiImpl;

import java.util.Iterator;
/*
public class DrawerStorage extends SnapshotParticipant<IDrawer> implements Storage<ItemVariant>, StorageView<ItemVariant>
{
    private IDrawer drawer;
    private IDrawer snapshot;

    public DrawerStorage (IDrawer drawer) {
        this.drawer = drawer;
        this.snapshot = drawer;
    }

    @Override
    protected void onFinalCommit () {
        this.drawer.setStoredItem(snapshot.getStoredItemPrototype(), snapshot.getStoredItemCount());
    }

    @Override
    public long insert (ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        IDrawer currentDrawer = this.snapshot;

        if (currentDrawer.canItemBeStored(resource.toStack())) {
            int remaining = currentDrawer.isEmpty()
                ? currentDrawer.getMaxCapacity()
                : currentDrawer.getAcceptingRemainingCapacity();

            int inserted = (int)Math.min(maxAmount, remaining);
            if (inserted > 0) {
                updateSnapshots(transaction);
                currentDrawer = snapshot;
                if (currentDrawer.isEmpty())
                    currentDrawer.setStoredItem(resource.toStack(), inserted);
                else
                    currentDrawer.adjustStoredItemCount(inserted);
                snapshot = currentDrawer;

                return inserted;
            }
        }

        return 0;
    }

    @Override
    public long extract (ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        IDrawer currentDrawer = snapshot;

        if (currentDrawer.canItemBeExtracted(resource.toStack())) {
            int extracted = (int)Math.min(currentDrawer.getStoredItemCount(), maxAmount);
            if (extracted > 0) {
                updateSnapshots(transaction);
                currentDrawer = snapshot;
                currentDrawer.adjustStoredItemCount(-extracted);
                snapshot = currentDrawer;

                return extracted;
            }
        }

        return 0;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator () {
        return TransferApiImpl.singletonIterator(this);
    }

    @Override
    public boolean isResourceBlank () {
        return snapshot.isEmpty();
    }

    @Override
    public ItemVariant getResource () {
        return ItemVariant.of(snapshot.getStoredItemPrototype());
    }

    @Override
    public long getAmount () {
        return snapshot.getStoredItemCount();
    }

    @Override
    public long getCapacity () {
        return snapshot.getMaxCapacity();
    }

    @Override
    protected IDrawer createSnapshot () {
        IDrawer original = snapshot;
        snapshot = snapshot.copy();
        return original;
    }

    @Override
    protected void readSnapshot (IDrawer snapshot) {
        this.snapshot = snapshot;
    }
}
*/