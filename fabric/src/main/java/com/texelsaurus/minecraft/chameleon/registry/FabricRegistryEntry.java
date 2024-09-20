package com.texelsaurus.minecraft.chameleon.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class FabricRegistryEntry<T> implements RegistryEntry<T>
{
    private final ResourceLocation loc;
    private final Supplier<T> objSupplier;
    private T obj;

    public FabricRegistryEntry(ResourceLocation loc, Supplier<T> supplier) {
        this.loc = loc;
        this.objSupplier = supplier;
    }

    @Override
    public ResourceLocation getId () {
        return loc;
    }

    @Override
    public T get () {
        if (obj == null)
            obj = objSupplier.get();

        return obj;
    }
}