package com.jaquadro.minecraft.storagedrawers.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IExtendedBlockClickHandler
{
    public void onBlockClicked (World world, BlockPos pos, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ, boolean invertShift);
}
