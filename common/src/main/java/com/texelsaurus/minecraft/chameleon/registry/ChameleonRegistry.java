package com.texelsaurus.minecraft.chameleon.registry;

import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ChameleonRegistry<T> extends ChameleonInit
{
    <C extends T> RegistryEntry<C> register(String id, Supplier<C> supplier);

    Collection<RegistryEntry<T>> getEntries();

    default Stream<RegistryEntry<T>> stream() {
        return getEntries().stream();
    }
}
