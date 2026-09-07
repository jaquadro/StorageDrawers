package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import net.minecraft.resources.Identifier;

public interface ChameleonCapabilities
{
    <T, C> ChameleonCapability<T> create(Identifier location, Class<T> clazz, Class<C> context);
}
