package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.NeoforgeRegistry;
import net.minecraft.core.Registry;

public class NeoforgeRegistries implements ChameleonRegistries
{
    @Override
    public <T> ChameleonRegistry<T> create (Registry<T> registry, String id) {
        return new NeoforgeRegistry<>(registry, id);
    }
}
