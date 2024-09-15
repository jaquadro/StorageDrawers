package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import net.minecraft.core.Registry;

public interface ChameleonRegistries
{
    <T> ChameleonRegistry<T> create(Registry<T> registry, String id);
}
