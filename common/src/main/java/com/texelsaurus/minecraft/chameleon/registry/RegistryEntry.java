package com.texelsaurus.minecraft.chameleon.registry;

import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public interface RegistryEntry<T> extends Supplier<T>
{
    Identifier getId();
}
