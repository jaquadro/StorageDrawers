package com.jaquadro.minecraft.storagedrawers.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommonProxy
{
    public int drawersRenderID = -1;
    public int controllerRenderID = -1;
    public int drawersCustomRenderID = -1;

    public void registerRenderers ()
    { }

    public void updatePlayerInventory (EntityPlayer player) {
        if (player instanceof EntityPlayerMP)
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
    }
}
