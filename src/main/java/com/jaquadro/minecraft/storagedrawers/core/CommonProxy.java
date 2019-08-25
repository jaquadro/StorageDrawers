package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonProxy
{
    public final ResourceLocation iconConcealmentOverlayResource = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_concealment");
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

    public CommonProxy () {
        MinecraftForge.EVENT_BUS.addListener(this::playerLeftClick);
        MinecraftForge.EVENT_BUS.addListener(this::playerRightClick);
    }

    public void registerRenderers ()
    { }

    public void updatePlayerInventory (PlayerEntity player) {
        if (player instanceof ServerPlayerEntity)
            ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
    }

    private void playerLeftClick (PlayerInteractEvent.LeftClickBlock event) {
        //if (event.getWorld().isRemote) {
            BlockPos pos = event.getPos();
            BlockState state = event.getWorld().getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof BlockDrawers) {
                if (!((BlockDrawers) block).creativeCanBreakBlock(state, event.getWorld(), pos, event.getPlayer())) {
                    state.onBlockClicked(event.getWorld(), pos, event.getPlayer());
                    event.setCanceled(true);
                }
            }
        //}
    }

    private void playerRightClick (PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == Hand.MAIN_HAND && event.getItemStack().isEmpty()) {
            TileEntity tile = event.getWorld().getTileEntity(event.getPos());
            if (tile instanceof TileEntityDrawers) {
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }
}
