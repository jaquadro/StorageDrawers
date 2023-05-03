package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemMetaRegistry<E>
{
    private Map<Item, Map<Integer, E>> registry;
    private boolean compactTopLevel;

    public ItemMetaRegistry () {
        registry = new HashMap<Item, Map<Integer, E>>();
    }

    public ItemMetaRegistry (boolean compactTopLevel) {
        this();
        this.compactTopLevel = compactTopLevel;
    }

    public void register (Item item, int meta, E entry) {
        Map<Integer, E> metamap = registry.get(item);
        if (metamap == null) {
            metamap = new HashMap<Integer, E>();
            registry.put(item, metamap);
        }

        metamap.put(meta, entry);
    }

    public E getEntry (Item item, int meta) {
        Map<Integer, E> metamap = registry.get(item);
        if (metamap == null)
            return null;

        return metamap.get(meta);
    }

    public void remove (Item item, int meta) {
        Map<Integer, E> metamap = registry.get(item);
        if (metamap == null)
            return;

        metamap.remove(meta);
        if (compactTopLevel && metamap.isEmpty())
            registry.remove(item);
    }

    public void clear (Item item) {
        Map<Integer, E> metamap = registry.get(item);
        if (metamap == null)
            return;

        metamap.clear();
        if (compactTopLevel)
            registry.remove(item);
    }

    public void clear () {
        if (compactTopLevel)
            registry.clear();
        else {
            for (Item item : registry.keySet())
                clear(item);
        }
    }

    public Set<Map.Entry<Item, Map<Integer, E>>> entrySet () {
        return registry.entrySet();
    }

    public Set<Map.Entry<Integer, E>> entrySet (Item item) {
        Map<Integer, E> metamap = registry.get(item);
        if (metamap == null) {
            metamap = new HashMap<Integer, E>();
            registry.put(item, metamap);
        }

        return metamap.entrySet();
    }
}
