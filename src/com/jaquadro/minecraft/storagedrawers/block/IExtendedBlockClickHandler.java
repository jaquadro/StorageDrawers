package com.jaquadro.minecraft.storagedrawers.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IExtendedBlockClickHandler
{
    public void onBlockClicked (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ);
}
