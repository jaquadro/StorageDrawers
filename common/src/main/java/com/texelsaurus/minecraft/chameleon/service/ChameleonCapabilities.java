package com.texelsaurus.minecraft.chameleon.service;

import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import net.minecraft.resources.ResourceLocation;

public interface ChameleonCapabilities
{
    <T, C> ChameleonCapability<T> create(ResourceLocation location, Class<T> clazz, Class<C> context);
}
