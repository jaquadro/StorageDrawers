package com.jaquadro.minecraft.storagedrawers.inventory;

import com.google.common.collect.MapMaker;
import com.jaquadro.minecraft.storagedrawers.api.storage.Drawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DrawerStorageImpl extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements SlottedStorage<ItemVariant>
{
    private static final Map<IDrawerGroup, DrawerStorageImpl> WRAPPERS = new MapMaker().weakValues().makeMap();

    public static DrawerStorageImpl of (IDrawerGroup group) {
        DrawerStorageImpl storage = WRAPPERS.computeIfAbsent(group, DrawerStorageImpl::new);
        storage.resizeSlotList();
        return storage;
    }

    final IDrawerGroup group;
    final List<DrawerStackStorage> backingList;

    public DrawerStorageImpl (IDrawerGroup group) {
        super(Collections.emptyList());
        this.group = group;
        backingList = new ArrayList<>();
    }

    @Override
    public @UnmodifiableView List<SingleSlotStorage<ItemVariant>> getSlots () {
        return parts;
    }

    private void resizeSlotList() {
        int[] slots = group.getAccessibleDrawerSlots();

        if (slots.length != parts.size()) {
            while (backingList.size() < slots.length)
                backingList.add(new DrawerStackStorage(this, backingList.size()));

            parts = Collections.unmodifiableList(backingList.subList(0, slots.length));
        }

        for (int i = 0; i < slots.length; i++)
            backingList.get(i).updateSlot(slots[i]);
    }

    @Override
    public int getSlotCount () {
        return getSlots().size();
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot (int slot) {
        return getSlots().get(slot);
    }

    IDrawer getDrawer (int slot) {
        if (slot < 0 || slot >= group.getDrawerCount())
            return Drawers.DISABLED;

        return group.getDrawer(slot);
    }
}
