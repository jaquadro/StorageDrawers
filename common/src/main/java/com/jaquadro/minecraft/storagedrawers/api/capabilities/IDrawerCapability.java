package com.jaquadro.minecraft.storagedrawers.api.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IDrawerCapability<T>
{
    T getCapability(Level level, BlockPos pos);
}
