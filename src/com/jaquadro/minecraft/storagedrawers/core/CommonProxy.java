package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy
{
    public final ResourceLocation iconLockResource = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/lock_icon");
    public final ResourceLocation iconClaimResource = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/claim_icon");
    public final ResourceLocation iconClaimLockResource = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/claim_lock_icon");
    public final ResourceLocation iconVoidResource = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/void_icon");

    public final ResourceLocation iconIndicatorCompOnResource = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_comp_on");
    public final ResourceLocation iconIndicatorCompOffResource = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_comp_off");

    public final ResourceLocation[] iconIndicatorOnResource = new ResourceLocation[] {
        null,
        new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_1_on"),
        new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_2_on"),
        null,
        new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_4_on"),
    };
    public final ResourceLocation[] iconIndicatorOffResource = new ResourceLocation[] {
        null,
        new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_1_off"),
        new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_2_off"),
        null,
        new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_4_off"),
    };

    public final ResourceLocation iconShroudCover = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/drawers_oak_trim");
    public final ResourceLocation iconTapeCover = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/tape");

    public void initDynamic ()
    { }

    public void registerRenderers ()
    { }

    public void updatePlayerInventory (EntityPlayer player) {
        if (player instanceof EntityPlayerMP)
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
    }

    @SubscribeEvent
    public void playerInteracts (PlayerInteractEvent event) {
        if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && event.getEntityPlayer().capabilities.isCreativeMode) {
            TileEntity tile = event.getWorld().getTileEntity(event.getPos());
            if (tile instanceof TileEntityDrawers) {
                int dir = ((TileEntityDrawers) tile).getDirection();
                if (dir == event.getFace().ordinal()) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    public void registerDrawer (Block block) { }
}
