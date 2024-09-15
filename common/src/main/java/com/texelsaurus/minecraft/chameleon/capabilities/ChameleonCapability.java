package com.texelsaurus.minecraft.chameleon.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ChameleonCapability<T>
{
    T getCapability(Level level, BlockPos pos);
}
