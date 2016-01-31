package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UniqueMetaRegistry<E>
{
    private Map<UniqueMetaIdentifier, E> registry;

    public UniqueMetaRegistry () {
        registry = new HashMap<UniqueMetaIdentifier, E>();
    }

    public void register (UniqueMetaIdentifier id, E entry) {
        registry.put(id, entry);
    }

    public E getEntry (UniqueMetaIdentifier id) {
        if (id == null)
            return null;

        if (registry.containsKey(id))
            return registry.get(id);

        if (id.meta != OreDictionary.WILDCARD_VALUE) {
            id = new UniqueMetaIdentifier(id.modId, id.name);
            if (registry.containsKey(id))
                return registry.get(id);

            if (!id.name.isEmpty()) {
                id = new UniqueMetaIdentifier(id.modId);
                if (registry.containsKey(id))
                    return registry.get(id);
            }
        }

        return null;
    }

    public void remove (UniqueMetaIdentifier id) {
        if (id != null)
            registry.remove(id);
    }

    public Set<Map.Entry<UniqueMetaIdentifier, E>> entrySet () {
        return registry.entrySet();
    }

    public void clear () {
        registry.clear();
    }
}
