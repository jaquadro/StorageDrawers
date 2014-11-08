package com.jaquadro.minecraft.storagedrawers.integration.ae2;

import appeng.api.AEApi;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IExternalStorageHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class DrawerExternalStorageHandler implements IExternalStorageHandler
{
    @Override
    public boolean canHandle (TileEntity te, ForgeDirection d, StorageChannel channel, BaseActionSource mySrc) {
        return channel == StorageChannel.ITEMS && te instanceof IDrawerGroup;
    }

    @Override
    public IMEInventory getInventory (TileEntity te, ForgeDirection d, StorageChannel channel, BaseActionSource src) {
        // TODO: Needs to be MEMonitor; DSU implementation uses non-API
        if (channel == StorageChannel.ITEMS)
            return new DrawerMEInventory((IDrawerGroup)te);

        return null;
    }
}
