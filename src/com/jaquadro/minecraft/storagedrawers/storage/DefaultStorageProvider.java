package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class DefaultStorageProvider implements IStorageProvider
{
    TileEntity tile;
    IDrawerGroup group;

    public DefaultStorageProvider (TileEntity tileEntity, IDrawerGroup drawerGroup) {
        tile = tileEntity;
        group = drawerGroup;
    }

    @Override
    public boolean isCentrallyManaged () {
        return false;
    }

    @Override
    public int getSlotCount (int slot) {
        return 0;
    }

    @Override
    public void setSlotCount (int slot, int amount) { }

    @Override
    public int getSlotStackCapacity (int slot) {
        return 0;
    }

    @Override
    public boolean isLocked (int slot) {
        return false;
    }

    @Override
    public void markAmountDirty (int slot) {
        if (tile.getWorldObj().isRemote)
            return;

        int count = group.getDrawer(slot).getStoredItemCount();

        IMessage message = new CountUpdateMessage(tile.xCoord, tile.yCoord, tile.zCoord, slot, count);
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, 500);

        StorageDrawers.network.sendToAllAround(message, targetPoint);
    }

    @Override
    public void markDirty (int slot) {
        if (tile.getWorldObj().isRemote)
            return;

        tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
    }
}
