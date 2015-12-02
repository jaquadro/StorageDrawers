package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

public class ForgeEventHandler
{
    @SubscribeEvent
    public void blockBreak (BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof EntityPlayerMP) {
            EntityPlayerMP playermp = (EntityPlayerMP)event.getPlayer();
            if (playermp.theItemInWorldManager.getGameType() == WorldSettings.GameType.ADVENTURE) {
                TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);
                if (tile instanceof TileEntityDrawers) {
                    if (playermp.capabilities.allowEdit)
                        return;
                    if (playermp.getCurrentEquippedItem() != null) {
                        ItemStack itemstack = playermp.getCurrentEquippedItem();
                        Block block = event.world.getBlock(event.x, event.y, event.z);
                        if (itemstack.func_150998_b(block) || itemstack.func_150997_a(block) > 1.0F)
                            return;
                    }
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void playerInteracts (PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            if (event.entityPlayer.capabilities.isCreativeMode) {
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
}
