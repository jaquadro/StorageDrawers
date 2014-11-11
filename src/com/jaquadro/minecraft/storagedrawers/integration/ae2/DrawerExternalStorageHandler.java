package com.jaquadro.minecraft.storagedrawers.integration.ae2;

import appeng.api.AEApi;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IExternalStorageHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
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

        try {
            Class adaptorClass = Class.forName("appeng.util.inv.IMEAdaptor");
            Object adaptor = adaptorClass.getConstructor(IMEInventory.class, BaseActionSource.class).newInstance(new DrawerMEInventory((IDrawerGroup) te), src);

            Class monitorClass = Class.forName("appeng.me.storage.MEMonitorIInventory");
            Class iadaptor = Class.forName("appeng.util.InventoryAdaptor");
            Object monitor = monitorClass.getConstructor(iadaptor).newInstance(adaptor);

            if (channel == StorageChannel.ITEMS)
                return (IMEInventory) monitor;
                //return new DrawerMonitorHandler(new MEAdaptor(new DrawerMEInventory((IDrawerGroup) te), src));
        }
        catch (Throwable t) {
            Throwable u = t;
        }

        return null;
    }
}
