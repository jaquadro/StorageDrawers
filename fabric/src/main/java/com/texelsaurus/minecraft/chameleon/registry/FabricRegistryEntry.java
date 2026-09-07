package com.texelsaurus.minecraft.chameleon.registry;

import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class FabricRegistryEntry<T> implements RegistryEntry<T>
{
    private final Identifier loc;
    private final Supplier<T> objSupplier;
    private T obj;

    public FabricRegistryEntry(Identifier loc, Supplier<T> supplier) {
        this.loc = loc;
        this.objSupplier = supplier;
    }

    @Override
    public Identifier getId () {
        return loc;
    }

    @Override
    public T get () {
        if (obj == null)
            obj = objSupplier.get();

        return obj;
    }
}