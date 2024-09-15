package com.texelsaurus.minecraft.chameleon.registry;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class NeoforgeRegistryEntry<R, T extends R> implements RegistryEntry<T>
{
    private final DeferredHolder<R, T> holder;

    public NeoforgeRegistryEntry(DeferredHolder<R, T> holder) {
        this.holder = holder;
    }

    @Override
    public ResourceLocation getId () {
        return holder.getId();
    }

    @Override
    public T get () {
        return holder.get();
    }
}