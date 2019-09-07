package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.item.Item;

import java.util.*;

public class ItemCollectionRegistry<E>
{
    private Map<Item, Collection<E>> registry;

    public ItemCollectionRegistry () {
        this.registry = new HashMap<>();
    }

    public void register (Item item, E entry) {
        Collection<E> list = registry.computeIfAbsent(item, k -> new TreeSet<>());
        list.add(entry);
    }

    public Collection<E> getEntries (Item item) {
        return registry.get(item);
    }

    public void remove (Item item) {
        registry.remove(item);
    }

    public void clear (Item item) {
        Collection<E> list = registry.get(item);
        if (list != null)
            list.clear();
    }

    public void clear () {
        for (Map.Entry<Item, Collection<E>> map : registry.entrySet()) {
            map.getValue().clear();
        }
    }

    public Set<Map.Entry<Item, Collection<E>>> entrySet () {
        return registry.entrySet();
    }
}
