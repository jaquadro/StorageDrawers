package com.jaquadro.minecraft.storagedrawers.inventory;

import com.google.common.collect.MapMaker;
import com.jaquadro.minecraft.storagedrawers.api.storage.Drawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DrawerStorageImpl extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements SlottedStorage<ItemVariant>
{
    public record DrawerGroupKey(IDrawerGroup drawer, Direction direction) { }

    private static final Map<DrawerGroupKey, DrawerStorageImpl> WRAPPERS = new MapMaker().weakValues().makeMap();

    public static DrawerStorageImpl of (IDrawerGroup group, Direction dir) {
        DrawerStorageImpl storage = WRAPPERS.computeIfAbsent(new DrawerGroupKey(group, dir), DrawerStorageImpl::new);
        storage.resizeSlotList();
        return storage;
    }

    public static DrawerStorageImpl of (IDrawerGroup group) {
        return of(group, null);
    }

    final IDrawerGroup group;
    final Direction side;
    final List<DrawerStackStorage> backingList;

    public DrawerStorageImpl (DrawerGroupKey key) {
        this(key.drawer, key.direction);
    }

    private DrawerStorageImpl (IDrawerGroup group, Direction dir) {
        super(Collections.emptyList());
        this.group = group;
        this.side = dir;
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
