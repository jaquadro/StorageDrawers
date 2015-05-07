package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy
{
    public int drawersRenderID = -1;
    public int controllerRenderID = -1;

    public void registerRenderers ()
    { }

    public void updatePlayerInventory (EntityPlayer player) {
        if (player instanceof EntityPlayerMP)
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
    }

    @SubscribeEvent
    public void playerInteracts (PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && event.entityPlayer.capabilities.isCreativeMode) {
            TileEntity tile = event.world.getTileEntity(event.pos);
            if (tile instanceof TileEntityDrawers) {
                int dir = ((TileEntityDrawers) tile).getDirection();
                if (dir == event.face.ordinal()) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
