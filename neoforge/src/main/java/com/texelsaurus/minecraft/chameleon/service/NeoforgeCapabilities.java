package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import com.texelsaurus.minecraft.chameleon.capabilities.NeoforgeCapability;
import net.minecraft.resources.Identifier;

public class NeoforgeCapabilities implements ChameleonCapabilities
{
    @Override
    public <T, C> ChameleonCapability<T> create (Identifier location, Class<T> clazz, Class<C> context) {
        return new NeoforgeCapability<>(location, clazz, context);
    }
}
