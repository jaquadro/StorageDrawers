package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.FabricRegistry;
import net.minecraft.core.Registry;

public class FabricRegistries implements ChameleonRegistries
{
    @Override
    public <T> ChameleonRegistry<T> create (Registry<T> registry, String id) {
        return new FabricRegistry<>(registry, id);
    }
}
