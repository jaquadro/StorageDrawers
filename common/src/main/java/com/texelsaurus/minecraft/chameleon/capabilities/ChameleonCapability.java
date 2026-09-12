package com.texelsaurus.minecraft.chameleon.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public interface ChameleonCapability<T>
{
    Identifier id ();

    T getCapability(Level level, BlockPos pos);
}
