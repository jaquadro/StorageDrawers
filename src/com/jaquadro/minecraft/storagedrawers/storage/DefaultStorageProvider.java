package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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
    public boolean isLocked ( int slot, LockAttribute attr) {
        return false;
    }

    @Override
    public boolean isVoid (int slot) {
        return false;
    }

    @Override
    public boolean isShrouded (int slot) {
        return false;
    }

    @Override
    public boolean setIsShrouded (int slot, boolean state) {
        return false;
    }

    @Override
    public boolean isShowingQuantity (int slot) {
        return false;
    }

    @Override
    public boolean setIsShowingQuantity (int slot, boolean state) {
        return false;
    }

    @Override
    public boolean isStorageUnlimited (int slot) {
        return false;
    }

    @Override
    public boolean isVendingUnlimited (int slot) {
        return false;
    }

    @Override
    public boolean isRedstone (int slot) {
        return false;
    }

    @Override
    public void markAmountDirty (int slot) {
        if (tile.getWorld().isRemote)
            return;

        int count = group.getDrawer(slot).getStoredItemCount();

        //IMessage message = new CountUpdateMessage(tile.getPos(), slot, count);
        //NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimensionId(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 500);

        for (Object playerObj : tile.getWorld().playerEntities) {
            if (!(playerObj instanceof EntityPlayerMP))
                continue;

            EntityPlayerMP player = (EntityPlayerMP)playerObj;
            if (player.getPosition().distanceSq(tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ()) < 500 * 500)
                StorageDrawers.network.sendTo(new CountUpdateMessage(tile.getPos(), slot, count), player);
        }

        tile.markDirty();

        //StorageDrawers.network.sendToAllAround(message, targetPoint);

        if (isRedstone(slot)) {
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            tile.getWorld().notifyNeighborsOfStateChange(tile.getPos(), state.getBlock());
            tile.getWorld().notifyNeighborsOfStateChange(tile.getPos().down(), state.getBlock());
        }
    }

    @Override
    public void markDirty (int slot) {
        if (tile.getWorld().isRemote)
            return;

        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        tile.getWorld().notifyBlockUpdate(tile.getPos(), state, state, 3);
        tile.markDirty();
    }
}
