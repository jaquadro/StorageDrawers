package com.jaquadro.minecraft.storagedrawers.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockDestroyHandler
{
    void onBlockDestroyed (World world, BlockPos pos);
}
