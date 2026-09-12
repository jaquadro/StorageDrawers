package com.texelsaurus.minecraft.chameleon.registry;

import net.minecraft.resources.Identifier;
import net.minecraftforge.registries.RegistryObject;

public class ForgeRegistryEntry<R, T extends R> implements RegistryEntry<T>
{
    private final RegistryObject<T> holder;

    public ForgeRegistryEntry (RegistryObject<T> holder) {
        this.holder = holder;
    }

    @Override
    public Identifier getId () {
        return holder.getId();
    }

    @Override
    public T get () {
        return holder.get();
    }
}