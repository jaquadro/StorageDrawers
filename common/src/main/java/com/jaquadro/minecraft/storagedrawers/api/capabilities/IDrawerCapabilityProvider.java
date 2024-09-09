package com.jaquadro.minecraft.storagedrawers.api.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IDrawerCapabilityProvider
{
    default <T> T getCapability(IDrawerCapability<T> capability) {
        return null;
    }
}
