package net.minecraft.world.chunk;

import net.minecraft.block.state.IBlockState;

interface IBlockStatePaletteResizer
{
    int onResize(int p_186008_1_, IBlockState state);
}