package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapPopulator
{
    /**
     * Create a Map from the given keys and values. This method creates a LinkedHashMap.
     */
    public static <K, V> Map<K, V> createMap(Iterable<K> keys, Iterable<V> values)
    {
        return populateMap(keys, values, Maps.<K, V>newLinkedHashMap());
    }

    /**
     * Populate the given Map with the given keys and values.
     */
    public static <K, V> Map<K, V> populateMap(Iterable<K> keys, Iterable<V> values, Map<K, V> map)
    {
        Iterator<V> iterator = values.iterator();

        for (K k : keys)
        {
            map.put(k, iterator.next());
        }

        if (iterator.hasNext())
        {
            throw new NoSuchElementException();
        }
        else
        {
            return map;
        }
    }
}