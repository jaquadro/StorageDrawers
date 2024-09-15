package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import com.texelsaurus.minecraft.chameleon.capabilities.NeoforgeCapability;
import net.minecraft.resources.ResourceLocation;

public class NeoforgeCapabilities implements ChameleonCapabilities
{
    @Override
    public <T, C> ChameleonCapability<T> create (ResourceLocation location, Class<T> clazz, Class<C> context) {
        return new NeoforgeCapability<>(location, clazz, context);
    }
}
