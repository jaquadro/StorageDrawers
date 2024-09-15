package com.texelsaurus.minecraft.chameleon.registry;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class RegistryEntryList<T>
{
    private final List<RegistryEntry<T>> entries = new ArrayList<>();

    public <C extends T, E extends RegistryEntry<C>> E add(E entry) {
        entries.add((RegistryEntry<T>) entry);
        return entry;
    }

    public List<RegistryEntry<T>> getEntries() {
        return ImmutableList.copyOf(entries);
    }
}
