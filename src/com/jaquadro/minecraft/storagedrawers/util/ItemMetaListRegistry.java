package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemMetaListRegistry<E>
{
    private ItemMetaRegistry<List<E>> registry;
    private boolean compactTopLevel;

    public ItemMetaListRegistry () {
        this(false);
    }

    public ItemMetaListRegistry (boolean compactTopLevel) {
        this.registry = new ItemMetaRegistry<List<E>>(compactTopLevel);
        this.compactTopLevel = compactTopLevel;
    }

    public void register (Item item, int meta, E entry) {
        List<E> list = registry.getEntry(item, meta);
        if (list == null) {
            list = new ArrayList<E>();
            registry.register(item, meta, list);
        }

        list.add(entry);
    }

    public List<E> getEntries (Item item, int meta) {
        return registry.getEntry(item, meta);
    }

    public void remove (Item item, int meta) {
        registry.remove(item, meta);
    }

    public void clear (Item item, int meta) {
        List<E> list = registry.getEntry(item, meta);
        if (list != null)
            list.clear();
    }

    public void clear (Item item) {
        for (Map.Entry<Integer, List<E>> map : registry.entrySet(item))
            map.getValue().clear();
    }

    public void clear () {
        for (Map.Entry<Item, Map<Integer, List<E>>> map : registry.entrySet()) {
            for (List<E> list : map.getValue().values())
                list.clear();
        }
    }

    public Set<Map.Entry<Item, Map<Integer, List<E>>>> entrySet () {
        return registry.entrySet();
    }
}
