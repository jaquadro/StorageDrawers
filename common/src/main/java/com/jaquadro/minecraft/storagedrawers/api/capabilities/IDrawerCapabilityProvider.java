package com.jaquadro.minecraft.storagedrawers.api.capabilities;

import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;

public interface IDrawerCapabilityProvider
{
    default <T> T getCapability(ChameleonCapability<T> capability) {
        return null;
    }
}
