package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.registry.ChameleonRegistry;
import com.texelsaurus.minecraft.chameleon.registry.ForgeRegistry;
import net.minecraft.core.Registry;

public class ForgeRegistries implements ChameleonRegistries
{
    @Override
    public <T> ChameleonRegistry<T> create (Registry<T> registry, String id) {
        return new ForgeRegistry<>(registry, id);
    }
}
