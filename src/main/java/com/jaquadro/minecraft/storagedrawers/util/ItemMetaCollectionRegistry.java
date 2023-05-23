package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.item.Item;

import java.util.*;

public class ItemMetaCollectionRegistry<E>
{
    private ItemMetaRegistry<Collection<E>> registry;
    private boolean compactTopLevel;

    public ItemMetaCollectionRegistry () {
        this(false);
    }

    public ItemMetaCollectionRegistry (boolean compactTopLevel) {
        this.registry = new ItemMetaRegistry<Collection<E>>(compactTopLevel);
        this.compactTopLevel = compactTopLevel;
    }

    public void register (Item item, int meta, E entry) {
        Collection<E> list = registry.getEntry(item, meta);
        if (list == null) {
            list = new TreeSet<E>();
            registry.register(item, meta, list);
        }

        list.add(entry);
    }

    public Collection<E> getEntries (Item item, int meta) {
        return registry.getEntry(item, meta);
    }

    public void remove (Item item, int meta) {
        registry.remove(item, meta);
    }

    public void clear (Item item, int meta) {
        Collection<E> list = registry.getEntry(item, meta);
        if (list != null)
            list.clear();
    }

    public void clear (Item item) {
        for (Map.Entry<Integer, Collection<E>> map : registry.entrySet(item))
            map.getValue().clear();
    }

    public void clear () {
        for (Map.Entry<Item, Map<Integer, Collection<E>>> map : registry.entrySet()) {
            for (Collection<E> list : map.getValue().values())
                list.clear();
        }
    }

    public Set<Map.Entry<Item, Map<Integer, Collection<E>>>> entrySet () {
        return registry.entrySet();
    }
}
