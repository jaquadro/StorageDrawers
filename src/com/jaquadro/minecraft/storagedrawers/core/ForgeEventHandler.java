package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ForgeEventHandler
{
    @SubscribeEvent
    public void playerInteracts (PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);
            if (tile instanceof TileEntityDrawers) {
                int dir = ((TileEntityDrawers) tile).getDirection();
                if (dir == event.face) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
