package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonProxy
{
    /*public final ResourceLocation iconConcealmentOverlayResource = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_concealment");
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
    };*/

    public CommonProxy () {
        MinecraftForge.EVENT_BUS.addListener(this::playerLeftClick);
        MinecraftForge.EVENT_BUS.addListener(this::playerRightClick);
    }

    public void registerRenderers ()
    { }

    public void updatePlayerInventory (Player player) {
        // TODO: Update line: if (player instanceof ServerPlayer)
        // TODO: Update line:     ((ServerPlayer) player).refreshContainer(player.inventoryMenu);
    }

    private void playerLeftClick (PlayerInteractEvent.LeftClickBlock event) {
        //if (event.getWorld().isRemote) {
            BlockPos pos = event.getPos();
            BlockState state = event.getWorld().getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof BlockDrawers) {
                if (event.getPlayer().isCreative()) {
                    if (!((BlockDrawers) block).creativeCanBreakBlock(state, event.getWorld(), pos, event.getPlayer())) {
                        state.attack(event.getWorld(), pos, event.getPlayer());
                        event.setCanceled(true);
                    }
                }
            }
        //}
    }

    private void playerRightClick (PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && event.getItemStack().isEmpty()) {
            BlockEntity tile = event.getWorld().getBlockEntity(event.getPos());
            if (tile instanceof TileEntityDrawers) {
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }
}
