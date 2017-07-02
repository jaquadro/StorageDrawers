package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.FractionalDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawersComp;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityDrawersComp extends TileEntityDrawers
{
    private GroupData groupData;

    private int capacity = 0;

    public TileEntityDrawersComp () {
        groupData = new GroupData(3);
        groupData.setCapabilityProvider(this);

        injectPortableData(groupData);
    }

    @Override
    protected IDrawerGroup getGroup () {
        return groupData;
    }

    private class GroupData extends FractionalDrawerGroup
    {
        public GroupData (int slotCount) {
            super(slotCount);
        }

        @Override
        protected World getWorld () {
            return TileEntityDrawersComp.this.getWorld();
        }

        @Override
        protected void log (String message) {
            if (!getWorld().isRemote && StorageDrawers.config.cache.debugTrace)
                StorageDrawers.log.info(message);
        }

        @Override
        protected int getStackCapacity () {
            return upgrades().getStorageMultiplier() * getEffectiveDrawerCapacity();
        }

        @Override
        protected void onItemChanged () {
            if (getWorld() != null && !getWorld().isRemote) {
                markDirty();
                markBlockForUpdate();
            }
        }

        @Override
        protected void onAmountChanged () {
            if (getWorld() != null && !getWorld().isRemote) {
                IMessage message = new CountUpdateMessage(getPos(), 0, getPooledCount());
                NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 500);

                StorageDrawers.network.sendToAllAround(message, targetPoint);

                markDirty();
            }
        }
    }

    @Override
    public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerDrawersComp(playerInventory, this);
    }

    @Override
    public String getGuiID () {
        return StorageDrawers.MOD_ID + ":compDrawers";
    }

    @Override
    public int getDrawerCapacity () {
        if (getWorld() == null || getWorld().isRemote)
            return super.getDrawerCapacity();

        if (capacity == 0) {
            ConfigManager config = StorageDrawers.config;
            capacity = config.getBlockBaseStorage("compdrawers");

            if (capacity <= 0)
                capacity = 1;
        }

        return capacity;
    }

    @Override
    public boolean dataPacketRequiresRenderUpdate () {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientUpdateCount (final int slot, final int count) {
        if (!getWorld().isRemote)
            return;

        Minecraft.getMinecraft().addScheduledTask(() -> TileEntityDrawersComp.this.clientUpdateCountAsync(count));
    }

    @SideOnly(Side.CLIENT)
    private void clientUpdateCountAsync (int count) {
        groupData.setPooledCount(count);
    }

    @Override
    public String getName () {
        return hasCustomName() ? super.getName() : "storagedrawers.container.compDrawers";
    }
}
